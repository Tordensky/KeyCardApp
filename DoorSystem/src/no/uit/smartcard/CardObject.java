package no.uit.smartcard;

import java.io.UnsupportedEncodingException;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;

public abstract class CardObject {
	
	protected Card card;
	protected CardChannel channel;
	
	public CardObject(Card card, CardChannel channel){
		this.card = card;
		this.channel = channel;
	}
	
	/**
	 * 	Reads the NDEF message on the card and parses it to get the message stored inside
	 * @return String The message stored in the NDEF message
	 * @throws CardException This exception occurs if there is something wrong with anything containing the smartcard part. 
	 * @throws UnsupportedEncodingException Happens if the NDEF message has become corrupt 
	 */
	public abstract byte[] readCard() throws CardException, UnsupportedEncodingException;
	
	/**
	 * Writes the a memo to the card,
	 * Memo are a type that is spceified to an application, to learn more about this read the documentation on NDEF messages
	 * @param type - the memo type
	 * @param message - what the memo type should contain
	 * @throws CardException
	 * @throws IllegalArgumentException - if the message is to big this excpetion happens
	 * @throws UnsupportedEncodingException
	 */
	public void writeMemoCard(byte[] type ,String message) throws CardException, UnsupportedEncodingException, IllegalArgumentException{
		byte[] payload = message.getBytes("UTF-8");
		writeCard(type, payload, NdefRecord.TNF_MIME_MEDIA);
	}
	
	/**
	 * Writes the specified uri to the card
	 * Note the uri should contain the usual header information to know what type of uri this is
	 * if nothing is specified the type is automatically set to http
	 * @param uri
	 * @throws UnsupportedEncodingException
	 * @throws IllegalArgumentException - if the message is to big this excpetion happens
	 * @throws CardException
	 */
	public void writeUriCard(String uri) throws UnsupportedEncodingException, CardException, IllegalArgumentException{
		String parseUrl = "";
		byte[] payload = null;
		if (uri.startsWith("https://")){
			parseUrl = uri.replaceAll("https://", "");
			parseUrl = parseUrl.replaceAll("www.", "");
			byte[] parseUrlByte = parseUrl.getBytes("UTF-8"); 
			byte[] header = {(byte)2};
			
			payload = new byte[parseUrlByte.length + header.length];

			System.arraycopy(header , 0, payload,0,header.length);
			System.arraycopy(parseUrlByte , 0, payload,1, parseUrlByte.length);
		}
		
		else if (uri.startsWith("http://")){
			parseUrl = uri.replaceAll("http://", "");
			parseUrl = parseUrl.replaceAll("www.", "");
			byte[] parseUrlByte = parseUrl.getBytes("UTF-8"); 
			byte[] header = {(byte)1};
			
			payload = new byte[parseUrlByte.length + header.length];
			
			System.arraycopy(header , 0, payload,0,1);
			System.arraycopy(parseUrlByte , 0, payload,1, parseUrlByte.length);
		
		}
		
		
		else{
			parseUrl = uri.replaceAll("www.", "");
			byte[] parseUrlByte = parseUrl.getBytes("UTF-8"); 
			byte[] header = {(byte)1};
			
			payload = new byte[parseUrlByte.length + header.length];
			
			System.arraycopy(header , 0, payload,0,header.length);
			System.arraycopy(parseUrlByte , 0, payload,1, parseUrlByte.length);
		
		}
		writeCard(NdefRecord.RTD_URI, payload);

		
	}
	
	/**
	 * Writes the specified text to the card
	 * @param message
	 * @throws CardException
	 * @throws IllegalArgumentException - if the message is to big this excpetion happens
	 * @throws UnsupportedEncodingException
	 */
	public void writeTextToCard(String message) throws CardException, UnsupportedEncodingException, IllegalArgumentException{
		byte[] tmp = message.getBytes("UTF-8");
		
		//the first bytes in the data specifies the language, so these are simply written in
		byte[] header = {(byte)0x2,  (byte)0x65,(byte) 0x6E};
		
		byte[] payload = new byte[tmp.length + header.length];
		
		System.arraycopy(header, 0, payload, 0, header.length);
		System.arraycopy(tmp, 0, payload, header.length, tmp.length);
		writeCard(NdefRecord.RTD_TEXT, payload);
	}
	
	/**
	 * Writes the specified type and data to the card
	 * This method can be used with helper methods, in those special cases where the data contains extra information 
	 * @param type
	 * @param payload
	 * @throws IllegalArgumentException - if the message is to big this excpetion happens
	 * @throws CardException
	 */
	protected abstract void writeCard(byte[] type, byte[] payload) throws CardException, IllegalArgumentException;
	
	/**
	 * Use this function in cases where TNF type is diffrent, these are situations where there is wanted to write MIME types to cards
	 * @param type
	 * @param payload
	 * @param TNF_TYPE - The diffrent supported TNF types can be found in NdefRecord.java
	 */
	protected abstract void writeCard(byte[] type, byte[] payload, short TNF_TYPE) throws CardException, IllegalArgumentException;
	
	
	/**
	 * Checks if the card is unlocked or not
	 * @return boolean
	 * @throws CardException
	 */
	public abstract boolean isCardLocked() throws CardException;
	
	
	/**
	 * Checks if the card is formated or not
	 * @return
	 * @throws CardException
	 */
	public abstract void lockCard() throws CardException;
	
	/**
	 * Formats the card
	 * @throws CardException
	 */
	protected abstract void formatCard() throws CardException;
	
	/**
	 * Checks if the card is formated or not
	 * @return
	 * @throws CardException
	 */
	protected abstract boolean isCardFormated() throws CardException;

	

}
