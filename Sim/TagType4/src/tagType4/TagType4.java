/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tagType4;

import javacard.framework.*;

/**
 *
 * @author Alexander Svendsen
 * This implementation is based on an example found in Appendix B of 
 * http://apps4android.org/nfc-specifications/NFCForum-TS-Type-4-Tag_2.0.pdf
 * Type 4 Tag Operation Specification, with minor tweaks.
 * 
 * Welcome to a world where there is little storage and many different types 
 * is no longer supported. It's like going back to the 80's with a high
 * level programming language
 */
public class TagType4 extends Applet {
  
    /* Supported commands. As Specified by the tag type 4 standars
     * Note: It should also support the Select command, but the framework has
     * it's own check for these
     */
    /*** ISO7816_SELECT_APPLICATIONS = A4, Selections of applications, or files ***/
    private final static byte ISO7816_READ_BINARY   = (byte) 0xB0; //Read data from file
    private final static byte ISO7816_UPDATE_BINARY = (byte) 0xD6; //Update (erase and write) data to file
    
    
    /** An Tag type 4 application shall consist of at least two EF files [ISO(IEC_7816-4]
     * The Capability Container file (CC file)
     * The NDEF file (Containing the actual NDEF records)
     * 
     * These two files shall order all their bytes big-endian byte order
     */
     private EF CC = new EF( (short) 0xE103, //CC file id, always this it seems
             new byte[] { 
                 0x00, (byte)0x0F,      /* CCLEN (15 Bytes), Length of the CC file */
                 0x20,                  /* Mapping version 2.0 */
                 0x00,(byte)0xFF,       /* Maximum data size that can be read with a 
                                         * single ReadBinary command */
                 0x00,(byte)0x7F,       /* Maximum data size that can be written with a
                                         * single UpdateBinary command. Note can not be FF since this
                                         * is proprietary*/
                 0x04,                  /* T Field of the NDEF File Control TLV, Special option */
                 0x06,                  /* L Field of the NDEF File Control TLV, Length of the rest of the TLV */
                                        /* The next bytes are a part of the V Field of the NDEF File Control TLV */
                 (byte) 0xE1, 0x04,     /* File Identifer of the NDEF file */
                 0x00, (byte)0xFF,      /* Maximum NDEF size (255 bytes) */
                 0x00,                  /* NDEF read access condition, nothing */
                 0x00                   /* NDEF write access condition, nothing */
            });
     private EF NDEF = new EF((short) 0xE104 ,new byte[(short)0xFF]); // Empty NDEF File
 
     
     
    /**
     * The Tag type 4 should keep track of which file is selected, if any,
     * and change this only
     */
     private final static short SELECTED = 1;
     private final static short SELECTED_CC_FILE = 2;
     private final static short SELECTED_NDEF_FILE = 3;
     
     
     private static short STATE = SELECTED;
    
    
    
    
    /**
     * Installs this applet.
     * @param bArray the array containing installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the parameter data in bArray
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new TagType4(bArray,bOffset,bLength);
    }

    /**
     * Default constructor
     * Only this class's install method should create the applet object.
     *  @param baBuffer the array constaining installation parameters
     *  @param sOffset the starting offset in baBuffer
     *  @param bLength the length in bytes of the data parameter in baBuffer
     */
    private TagType4(byte[] baBuffer, short sOffset, byte bLength) {
        byte aidLen = baBuffer[sOffset];
        
        //Since the ndef message is empty the 2 first bytes need to be set to 0
        //It shows the length of the ndef message
        NDEF.data[0] = 0;
        NDEF.data[1] = 0;
        
        if (aidLen == (byte) 0) {
            register();
        } else {
            register(baBuffer, (short) (sOffset + 1), aidLen);
        }
    }

    /**
     * Note, not my own code, got it from the summer project, and i think it 
     * was given by Kjell. Seams like we must have these to get all the data
     */
    /* Helper methods for getting outstanding bytes if any on platforms that
    does not transfers all data at once.
    Neccessary for G&D SIM cards. */

    private short getBytesLeft(byte[] buf) {
        return (short) ((buf[ISO7816.OFFSET_LC] & 0xFF) -
                        ((buf.length & 0xFF) - ISO7816.OFFSET_CDATA));
    }

    private boolean hasBytesLeft(APDU apdu, byte[] buf) {
        return (getBytesLeft(buf) > 0)
            ? true
            : false;
    }

    /* Will only work in cases where outstanding bytes are less or equal
     to the size of the internal buffer maintained by the APDU class.
     This buffer is probably 133 bytes long (5 bytes header and 128 bytes
     data). */
    private void readBytesLeft(APDU apdu, byte[] buf) {
        short bytesLeft = getBytesLeft(buf); 

        if (bytesLeft > 0) {
            short readCount = apdu.setIncomingAndReceive();

            while (bytesLeft > 0) {
                bytesLeft -= readCount;
                readCount = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
            }
        }
    }

    /**
     * Ends here
     */
    
    
    /**
     * Process the incoming select statements
     * @param byte 
     */
    public void select(byte[] buf){
       
        //could check the P1P2 options for errors, but not important for functionality

       if(buf[ISO7816.OFFSET_LC] == 2){
           //We get out the Id from the data and check if this is equal to any of the ID's in the system
            short fileID = Util.makeShort(buf[ISO7816.OFFSET_CDATA], buf[ISO7816.OFFSET_CDATA + 1]);
            if (fileID == CC.id){
                STATE = SELECTED_CC_FILE;
            }else if(fileID == NDEF.id){
                STATE = SELECTED_NDEF_FILE;
            }else{
                STATE = SELECTED;
                ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
            }
            return;
       }
       
       STATE = SELECTED;
       ISOException.throwIt(ISO7816.SW_DATA_INVALID);
    }
    
    
    /**
     * Function to read the specified EF file
     * @param buf 
     */
    public void read(APDU apdu, byte[] buf){
        byte[] data  = (STATE == SELECTED_CC_FILE) ? CC.data:NDEF.data;
        short offset = Util.makeShort(buf[ISO7816.OFFSET_P1], buf[ISO7816.OFFSET_P2]);
        
        short le = buf[ISO7816.OFFSET_LC];
        apdu.setOutgoing();
        Util.arrayCopyNonAtomic(data, offset, buf, (short) 0, le);
        apdu.setOutgoingLength(le);
        apdu.sendBytes((short) 0,le);
    }
    
    /**
     * Function for writing data to the NDEF message
     * @param buf 
     */
    public void update(byte[] buf){
        //TODO: ERROR testing?
        
        short offset = Util.makeShort(buf[ISO7816.OFFSET_P1], buf[ISO7816.OFFSET_P2]);
        short le = buf[ISO7816.OFFSET_LC];
        Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_CDATA, NDEF.data, offset, le);
    }
    
    /**
     * Processes an incoming APDU.
     * @see APDU
     * @param apdu the incoming APDU
     */
    public void process(APDU apdu) {
        if (selectingApplet()){
            //The application is selected, but that don't mean the files are
            STATE = SELECTED;
            return;
        }
        byte[] buf = apdu.getBuffer();
        
        /**
         * Only Support three different instructions as specified at the top
         **/
        if(buf[ISO7816.OFFSET_INS] == ISO7816.INS_SELECT){
            if (hasBytesLeft(apdu, buf) == true) {
                readBytesLeft(apdu, buf);
                buf = apdu.getBuffer();
            }
            select(buf);
        }else if (buf[ISO7816.OFFSET_INS] == ISO7816_UPDATE_BINARY) {
            if (STATE != SELECTED_NDEF_FILE){
                
                //Re initialize it if the state is wrong and a wrong command is issued
                STATE = SELECTED;
                ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
            }
            if (hasBytesLeft(apdu, buf) == true) {
                readBytesLeft(apdu, buf);
                buf = apdu.getBuffer();
            }
            update(buf);
            
            
            
        }else if (buf[ISO7816.OFFSET_INS] == ISO7816_READ_BINARY) {
            if (STATE == SELECTED){
                //Unsure about the exception
                ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
            }
            read(apdu, buf);
  
        }
        else{
            //If the INS parameters is not among the supproted commands specified at the beginning, an execption is thrown
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
