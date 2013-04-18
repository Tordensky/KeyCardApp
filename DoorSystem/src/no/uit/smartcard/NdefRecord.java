package no.uit.smartcard;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * @author Alexander Svendsen
 * An class both for generating ndef records and parsing it
 * 
 * Represents a logical (unchunked) NDEF (NFC Data Exchange Format) record.
 * 
 * An NDEF record always contains:
*
* 3-bit TNF (Type Name Format) field: Indicates how to interpret the type field
* Variable length type: Describes the record format
* Variable length ID: A unique identifier for the record
* Variable length payload: The actual data payload
* The underlying record representation may be chunked across several NDEF records when the payload is large.
 */
public class NdefRecord {
	
	
	
	
	/**
	 * Indicates no type, id, or payload is associated with this NDEF Record.
	 */
	public static final short TNF_EMPTY = 0x00;
	
	/**
	 * Indicates the type field uses the RTD type name format.
	 */
	public static final short TNF_WELL_KNOWN = 0x01;
	
	/**
	 * Indicates the type field contains a value that follows the media-type BNF construct defined by RFC 2046.
	 * This type is in use when that applications themselves wants to write their type to the TAG
	 */
	public static final short TNF_MIME_MEDIA = 0x02;
	
	/**
	 * Indicates the type field contains a value that follows the absolute-URI BNF construct defined by RFC 3986.
	 */
	public static final short TNF_ABSOLUTE_URI = 0x03;
	
	/**
	 * Indicates the type field contains a value that follows the RTD external name specification.
	 */
	public static final short TNF_EXTERNAL_TYPE = 0x04;
	
	/**
	 * Indicates the payload type is unknown.
	 */
	public static final short TNF_UNKNOWN = 0x05;
	
	/**
	 * Indicates the payload is an intermediate or final chunk of a chunked NDEF Record.
	 */
	public static final short TNF_UNCHANGED = 0x06;

	
	/**
	 * Indicates the TNF is reserved
	 * Documentation suggest that this type should be handled as TNF_UNKOWN at this time
	 * @hide
	 */
	public static final short TNF_RESERVED = 0x07;
	

    /**
     * RTD Text type. For use with TNF_WELL_KNOWN.
     */
    public static final byte[] RTD_TEXT = {0x54};  // "T"

    /**
     * RTD URI type. For use with TNF_WELL_KNOWN.
     */
    public static final byte[] RTD_URI = {0x55};   // "U"

    /**
     * RTD Smart Poster type. For use with TNF_WELL_KNOWN.
     * TODO: not suported yet
     */
    public static final byte[] RTD_SMART_POSTER = {0x53, 0x70};  // "Sp"

    /**
     * RTD Alternative Carrier type. For use with TNF_WELL_KNOWN.
     * TODO: not suported yet
     */
    public static final byte[] RTD_ALTERNATIVE_CARRIER = {0x61, 0x63};  // "ac"

    /**
     * RTD Handover Carrier type. For use with TNF_WELL_KNOWN.
     * TODO: not suported yet
     */
    public static final byte[] RTD_HANDOVER_CARRIER = {0x48, 0x63};  // "Hc"

    /**
     * RTD Handover Request type. For use with TNF_WELL_KNOWN.
     * TODO: not suported yet
     */
    public static final byte[] RTD_HANDOVER_REQUEST = {0x48, 0x72};  // "Hr"

    /**
     * RTD Handover Select type. For use with TNF_WELL_KNOWN.
     * TODO: not supported yet
     */
    public static final byte[] RTD_HANDOVER_SELECT = {0x48, 0x73}; // "Hs"


    
    /**
     *	Header Flags 
     */
    
    /**
     * Message Begin: Symbolizes that this is the first record in the NDEF message
     */
    private static final byte FLAG_MB = (byte) 0x80;
    
    /**
     * Message End: Symbolizes that this is the last record in the NDEF message. The first record can also be the last
     * Note: Altough a standard that each record is in order from first to last, this is no requirement. the applications may chose themeself the order in the records, but the first and last in that order
     * must have this flags set or it is invalid
     */
    private static final byte FLAG_ME = (byte) 0x40;
    
    /**
     * Chunked Data flag
     */
    private static final byte FLAG_CF = (byte) 0x20;
    
    /**
     * Short record: If set there is only one byte Payload Length if not there is at least 4 bytes for the payload length
     */
    private static final byte FLAG_SR = (byte) 0x10;
    
    /**
     * ID Length: If set the record contains both an ID field and ID lenght field
     */
    private static final byte FLAG_IL = (byte) 0x08;

    private  byte mFlags;
    private  short mTnf;
    private  byte[] mType;
    private  byte[] mId;
    private  byte[] mPayload;

	//each NDEF record looks like this:
	//bit      0			1			2			3			4		5	  6	    7				
	//		 [	MB	|	ME	|	CF		|	SR		|	IL	|		TNF		]		
	//      [							Type Length								] 
	//      [							Payload Length 3						] 
	//      [							Payload Length 2						] 
	//      [							Payload Length 1						] 
	//      [							Payload Length 0						] 
	//      [							ID Length									] 
	//      [							Type											] 
	//      [							ID												] 
	//      [							Payload										] 

	
    /**
     * Construct an NDEF Record.
     * Note that this solution only supports use of one record, since there is no tag at this time that supports more data storage then a record can have
     * @param tnf  a 3-bit TNF constant
     * @param type byte array, containing zero to 255 bytes, must not be null
     * @param id   byte array, containing zero to 255 bytes, must not be null
     * @param payload byte array, containing zero to (2 ** 32 - 1) bytes,
     *                must not be null
     * @throws IllegalArgumentException if input are invalid
     */
    public NdefRecord(short tnf, byte[] type, byte[] id, byte[] payload) throws IllegalArgumentException {
        //TODO: Support multiple records
    		/* check arguments */
        if ((type == null) || (id == null) || (payload == null)) {
            throw new IllegalArgumentException("Illegal null argument");
        }

        if (tnf < 0 || tnf > 0x07) {
            throw new IllegalArgumentException("TNF out of range " + tnf);
        }

        /* generate flag */
        byte flags = FLAG_MB | FLAG_ME;

        /* Determine if it is a short record */
        if(payload.length < 0xFF) {
            flags |= FLAG_SR;
        }

        /* Determine if an id is present */
        if(id.length != 0) {
            flags |= FLAG_IL;
        }

        mFlags = flags;
        mTnf = tnf;
        mType = type.clone();
        mId = id.clone();
        mPayload = payload.clone();
    }
    /**
     * Construct an NDEF Record from raw bytes.
     * <p>
     * Validation is performed to make sure the header is valid, and that
     * the id, type and payload sizes appear to be valid.
     *
     * @throws FormatException if the data is not a valid NDEF record
     */
    public NdefRecord(byte[] data) throws IllegalArgumentException {
        /* Prevent compiler to complain about unassigned final fields */
        mFlags = 0;
        mTnf = 0;
        mType = null;
        mId = null;
        mPayload = null;
        /* Perform actual parsing */
        if (parseNdefRecord(data) == -1) {
            throw new IllegalArgumentException("Error while parsing NDEF record, invalid NDEF record");
        }
    }
    
    /**
     * Returns the 3-bit TNF.
     * <p>
     * TNF is the top-level type.
     */
    public short getTnf() {
        return mTnf;
    }

    /**
     * Returns the variable length Type field.
     * <p>
     * This should be used in conjunction with the TNF field to determine the
     * payload format.
     */
    public byte[] getType() {
        return mType.clone();
    }

    /**
     * Returns the variable length ID.
     */
    public byte[] getId() {
        return mId.clone();
    }

    /**
     * Returns the variable length payload.
     */
    public byte[] getPayload() {
        return mPayload.clone();
    }

    /**
     * Returns this entire NDEF Record as a byte array.
     */
    public byte[] toByteArray() {
        return generate(mFlags, mTnf, mType, mId, mPayload);
    }
    
    /**
     * Parses the raw bytes to an ndef record
     * @param data
     * @return -1 if an error occured in the parsing
     */
    private int parseNdefRecord(byte[] data) {
    	System.out.println(data.length);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte header = buffer.get();
        this.mFlags = (byte)(header & 0x7c);
        this.mTnf = (byte)(header & 0x07);
        
        byte typeLength;
        int payloadLength;
        byte idLength = 0x00;
        
        typeLength = buffer.get();
        if ((mFlags & FLAG_SR) > 0) {
                payloadLength = 0xFF & buffer.get();
        } else {
                payloadLength = buffer.getInt();
        }
        if ((mFlags & FLAG_IL) > 0) {
                idLength = buffer.get();
        }
        
        mType = new byte[typeLength & 0xFF];
        buffer.get(mType);
        
        mId = new byte[idLength & 0xFF];
        buffer.get(mId);
        
        mPayload = new byte[payloadLength & 0xFF];

        System.out.println(buffer.capacity());
        buffer.get(mPayload);
        //Whats wrong here is simply that the data sendt inn is to small, where is the rest?
        return 0;
    }
    
    
    /**
     * Returns an byte array of the whole NDEF record with the given parameters 
     * @param flags
     * @param tnf
     * @param type
     * @param id
     * @param data
     * @return
     */
    private byte[] generate(short flags, short tnf, byte[] type, byte[] id, byte[] data) {
        if (data.length < 255) {
                flags = (short)(flags | FLAG_SR);
        }
        
        boolean idPresent = (flags & FLAG_IL) != 0;
        boolean shortRecord = (flags & FLAG_SR) != 0;
        int fixedBytes = (idPresent) ? 7 : 6;
        if (shortRecord) {
                fixedBytes -= 3;
        }
        ByteBuffer record = ByteBuffer.allocate(fixedBytes + id.length + type.length + data.length);
        record.put((byte)(flags | tnf));
        record.put((byte)(0xFF & type.length));

        if (!shortRecord) {
                record.put((byte)(0xFF & (data.length >>> 24)));
                record.put((byte)(0xFF & (data.length >>> 16)));
                record.put((byte)((0xFF & data.length >>> 8)));
        }
        record.put((byte)((0xFF & data.length)));

        if (idPresent) {
                record.put((byte)id.length);
        }
        record.put(type);
        record.put(id);
        
        record.put(data);
        return record.array();
    }
}
