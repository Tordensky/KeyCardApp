package no.uit.smartcard;

/**
 * 
 * @author Alexander Svendsen
 * An class for parsing and generation an NDEF message
 * Note that this class only supports creation of a single record message for simplicity,
 * since at this time no tag have the room for what maximum bytes the data can be on one record 
 *
 */
public class NdefMessage {
	
	//Each Ndef message must end with the notation 0xFE
	private final byte END_NOTATION = (byte) 0xFE;
	
	//As of now the version of the ndef standard is 0x03, but this does not seem to matter for any application as everyone seem to ignore it 
	private final byte VERSION_NUMBER = (byte) 0x03;
	
	private NdefRecord[] mRecords;


    /**
     * Create an NDEF message from NDEF records.
     */
    public NdefMessage(NdefRecord[] records) {
        mRecords = new NdefRecord[records.length];
        System.arraycopy(records, 0, mRecords, 0, records.length);
    }

	
	/**
	 * Contructs a NDEF message from raw bytes
	 * @param data - raw data to be written
	 * @throws IlligalArgumentException
	 */
	public NdefMessage(byte[] data) throws IllegalArgumentException{
		mRecords = null;
		System.out.println(data.length);
        if (parseNdefMessage(data) == -1) {
            throw new IllegalArgumentException("Error while parsing NDEF message");
        }
	}
	

	/**
    * Get the NDEF records inside this NDEF message.
    *
    * @return array of zero or more NDEF records.
    */
   public NdefRecord[] getRecords() {
       return mRecords.clone();
   }

   /**
    * Returns a byte array representation of this entire NDEF message.
    */
   public byte[] toByteArray() {
       if ((mRecords == null) || (mRecords.length == 0))
           return new byte[0];

       
       byte[] msg = {};

       for (int i = 0; i < mRecords.length; i++) {
           byte[] record = mRecords[i].toByteArray();
           byte[] tmp = new byte[msg.length + record.length];

           System.arraycopy(msg, 0, tmp, 0, msg.length);
           System.arraycopy(record, 0, tmp, msg.length, record.length);

           msg = tmp;
       }

       //need to add the ndef message structure around it
       
       byte[] header =  {VERSION_NUMBER, (byte) msg.length};
       byte[] end = {END_NOTATION};
       
       
       byte[] ndefMessage = new byte[header.length+ end.length +msg.length];
       
       System.arraycopy( header , 0, ndefMessage, 0, header.length);
       System.arraycopy(msg, 0, ndefMessage, header.length, msg.length);
       System.arraycopy(end, 0, ndefMessage, header.length + msg.length, end.length);
       
       return ndefMessage;
   }
   
   private int parseNdefMessage(byte[] data){
	   byte[] record = new byte[(int)data[1]];
	   System.arraycopy(data, 2, record, 0, (int)data[1]);
	   NdefRecord r = new NdefRecord(record);
	  
	   mRecords = new NdefRecord[1];
       mRecords[0] = r;
	   return 0;
   }
   
}
