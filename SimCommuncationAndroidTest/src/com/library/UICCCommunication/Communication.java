/**
 * 
 */
package com.library.UICCCommunication;

import java.util.Arrays;

import org.apache.http.impl.conn.Wire;
import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.Session;

import android.util.Log;

/**
 * @author Alexander Svendsen
 * A library class that simply handles the communication to the UICC card
 * It does not setup the communication channel, this is left for the user
 */
public class Communication{
	private byte[] AID = {(byte)0xD2, 0x76, 0x00, 0x00, (byte)0x85, 0x01, 0x01};
	private final String LOG_TAG = "UICC log";
	// D2 76 00 00 85 01 01
	/**
	 * Function to read the data on the UICC card
	 * @param arg0 Its up to the user to ensure that the SEservice is correctly connected
	 */
	public final byte[] readData(SEService arg0){
		
		return null;
	}
	
	/**
	 * Function to write the data to the UICC card
	 * @param arg0 Its up to the user to ensure that the SEservice is correctly connected
	 * @param data The data to be written onto the UICC card, ensure that the data does not exceed 254 bytes
	 */
	public final void writeData(SEService arg0, byte[] data){
		try {
	  	      Log.i(LOG_TAG, "Retrieve available readers...");
		      Reader[] readers = arg0.getReaders();
		      if (readers.length < 1)
		         return;
	
		      Log.i(LOG_TAG, "Create Session from the first reader...");
		      Session session = readers[0].openSession();
	
		      Log.i(LOG_TAG,"Create logical channel within the session...");
		      Channel channel = session.openLogicalChannel(AID);
	
      
		      //Select Ndef
		      byte[] NdefSelect = new byte[] {0x00, (byte) 0xA4, 0x00, 0x0C, 0x02, (byte) 0xE1, 0x04};
		      byte[] respApdu = channel.transmit(NdefSelect);
		      Log.d(LOG_TAG,"Array in respone for select: " + Arrays.toString(respApdu));
		      
		      byte[] writeCommand = new byte[]
		    		  {(byte)0x00, (byte)0xD6,(byte) 0x00, (byte)0x00, (byte)data.length};
		      
		      byte[] wholeCommand = new byte[writeCommand.length + data.length];
		      System.arraycopy(writeCommand, 0, wholeCommand, 0, writeCommand.length);
		      System.arraycopy(data, 0, wholeCommand, writeCommand.length, data.length);
		      //wholeCommand = new byte[] {0x00, (byte) 0xB0, 0x00, 0x00, 0x02};

		      
		      Log.i(LOG_TAG,Arrays.toString(wholeCommand));
		      respApdu = channel.transmit(wholeCommand);
	
		      channel.close();
	
		      Log.d(LOG_TAG,"Array in respone for write data: " + Arrays.toString(respApdu));
	      
		   } catch (Exception e) {
		      Log.e(LOG_TAG, "Error occured:", e);
		   }
	}
	
}
