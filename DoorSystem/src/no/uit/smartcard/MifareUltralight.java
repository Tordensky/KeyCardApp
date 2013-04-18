package no.uit.smartcard;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

/**
 * An class that can communicate with the Mifare Ultralight card
 * @author Alexander Svendsen
 */
public class MifareUltralight extends CardObject{

	//Option header, if the card does not have this header the card has not been formated yet
	private final static byte[] OPT ={(byte)0xE1, (byte)0x10,(byte)0x06, (byte)0x00};
	private final static int MAX_SIZE = 48;
	
	
	public MifareUltralight(Card card, CardChannel channel){
		super(card, channel);
	}
	
	@Override
	public byte[] readCard() throws CardException, UnsupportedEncodingException {

		//Mifare ultralight starts it's data fields from page 4 and below
		int sector = 4;
		ResponseAPDU r;

		//For some reason the card refuses to read more than one page at a time, this loop is to ensure that everything gets read. 
		byte[] rawData = {};
		do{
				//NOTE: every communications with the card happens over APDU commands.
				byte[] APDU = {(byte)0xFF, (byte)0xB0, (byte)0x00, (byte)sector , (byte)0x04};
				r = channel.transmit(new CommandAPDU(APDU));
				byte[] tmpData = new byte[rawData.length + r.getData().length];
				System.arraycopy(rawData, 0, tmpData, 0, rawData.length);
		        System.arraycopy(r.getData(), 0, tmpData, rawData.length, r.getData().length);
				rawData = tmpData;
				sector ++;
			}while(r.getSW() == 0x9000); //Only stops reading the pages until an error occur from the card, meaning there is no more sectors to be read 

		if (rawData.length <= 0){
			throw new CardException("No valid response from terminal");
		}
		return rawData;
//		NdefMessage ndefMSG= new NdefMessage(rawData);	
//		NdefRecord record = ndefMSG.getRecords()[0];
//		
//		
//		byte[] payload = record.getPayload();
//
//		
//		//If the message is an URI, there is a need to read the first byte and check which type it is, then add the correct URI header.
//		String message = "";
//		byte[] type = record.getType();
//		if (Arrays.equals(type , NdefRecord.RTD_URI)){
//			if (payload[0] == 1){
//				message += "http://";
//			}
//			else if (payload[0] == 2){
//				message += "https://";
//			}
//			byte[] tmp = new byte[payload.length -1 ];
//			System.arraycopy(payload, 1, tmp, 0, payload.length - 1);
//			payload = tmp;
//		}
//		//the first three bytes in an text message contains language information, no real use in this application, so simply remove it
//		else if(Arrays.equals(type ,NdefRecord.RTD_TEXT)	){
//			int length = payload[0] + 1;
//			byte[] tmp = new byte[payload.length - length ];
//			System.arraycopy(payload, length, tmp, 0, payload.length - length);
//			payload = tmp;
//		}
//		
//		message += new String(payload, "UTF-8");
//		
//		return message;
		
	}

	@Override
	protected void writeCard(byte[] type, byte[] payload, short TNF_TYPE) throws CardException, IllegalArgumentException{
		if (!isCardFormated()){
			this.formatCard();
		}

		NdefRecord record = new NdefRecord((short) TNF_TYPE, type,  new byte[0], payload);
		NdefRecord[] records = new NdefRecord[1];
		
		
		records[0] = record;
		
		NdefMessage msg = new NdefMessage(records);
		
		ResponseAPDU r;
		byte[] data = msg.toByteArray();
		
		if (data.length > MAX_SIZE){
			throw new IllegalArgumentException("Message is to big");
		}
		
		int sector = 4;
		for (int i = 0; i < data.length; i += 4, sector ++){

			byte para1 =(i >= data.length) ?  (byte)0x00 :  data[i];

			byte para2 =(i+ 1 >= data.length) ?  (byte)0x00 :  (byte)data[i+1];

			byte para3 =(i+2 >= data.length) ?  (byte)0x00 :  (byte)data[i+ 2];

			byte para4 =(i+ 3 >= data.length) ?  (byte)0x00 :  (byte)data[i+ 3];

			byte[] APDU = {(byte)0xFF, (byte)0xD6, (byte)0x00, (byte)sector , (byte)0x04, para1, para2, para3,para4 };
			r = channel.transmit(new CommandAPDU(APDU));
		}
	}
	
	@Override
	protected void writeCard(byte[] type, byte[] payload) throws CardException, IllegalArgumentException {
		if (!isCardFormated()){
			this.formatCard();
		}

		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, type,  new byte[0], payload);
		NdefRecord[] records = new NdefRecord[1];
		
		records[0] = record;
		
		NdefMessage msg = new NdefMessage(records);
		
		ResponseAPDU r;
		byte[] data = msg.toByteArray();
		
		if (data.length > MAX_SIZE){
			throw new IllegalArgumentException("Message is to big");
		}
		
		int sector = 4;
		for (int i = 0; i < data.length; i += 4, sector ++){

			byte para1 =(i >= data.length) ?  (byte)0x00 :  data[i];

			byte para2 =(i+ 1 >= data.length) ?  (byte)0x00 :  (byte)data[i+1];

			byte para3 =(i+2 >= data.length) ?  (byte)0x00 :  (byte)data[i+ 2];

			byte para4 =(i+ 3 >= data.length) ?  (byte)0x00 :  (byte)data[i+ 3];

			byte[] APDU = {(byte)0xFF, (byte)0xD6, (byte)0x00, (byte)sector , (byte)0x04, para1, para2, para3,para4 };
			r = channel.transmit(new CommandAPDU(APDU));
		}
		
	}

	@Override
	public boolean isCardLocked() throws CardException {
	
		ResponseAPDU r;
		byte[] APDU = {(byte)0xFF, (byte)0xB0, (byte)0x00, (byte)0x02 , (byte)0x04};
		r = channel.transmit(new CommandAPDU(APDU));
		
		if (r.getData().length < 4){
			return false;
		}
		//TODO: should look further into what pages are looked, but for now evrything is locked all the time
		if ((r.getData()[2] & 0xff ) != 0 ||  (r.getData()[3] & 0xff ) != 0 )
			return true;
		return false;
	}

	@Override
	public void lockCard() throws CardException {
		
		ResponseAPDU r;
		byte[] APDU1 = {(byte)0xFF, (byte)0xB0, (byte)0x00, (byte)0x02 , (byte)0x04};
		r = channel.transmit(new CommandAPDU(APDU1));
		
		//TODO: should lock the sectors more specific, but this works for now, since it locks evrything
		byte[] APDU2 = {(byte)0xFF, (byte)0xD6, (byte)0x00, (byte)0x02 , (byte)0x04, r.getData()[0], r.getData()[1], (byte)0xF8, (byte)0xFF};
		r = channel.transmit(new CommandAPDU(APDU2));
		
	}

	@Override
	protected void formatCard() throws CardException {
		byte[] APDU = {(byte)0xFF, (byte)0xD6, (byte)0x00, (byte)0x03 , (byte)0x04, OPT[0], OPT[1],OPT[2],OPT[3]};
		channel.transmit(new CommandAPDU(APDU));
		
	}

	@Override
	protected boolean isCardFormated() throws CardException {
		ResponseAPDU r;
		byte[] APDU = {(byte)0xFF, (byte)0xB0, (byte)0x00, (byte)0x03 , (byte)0x04};
		r = channel.transmit(new CommandAPDU(APDU));
		
		if (Arrays.equals(r.getData() ,OPT))
			return true;
		return false;
		
	}

}
