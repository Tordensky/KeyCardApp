package no.uit.smartcard;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;



//This class should always run and give an event when a mobile is placed on the reader
public class Communication {
	
	protected Card card;
	protected CardChannel channel;
	private CardTerminal terminal;
	private CardObject typeCard;
	
	private byte[] AID = {(byte)0xD2,0x76, 0x00,0x00, (byte)0x85,0x01,0x01};
	private final static byte[] ATR_MIFARE_ULTRALIGHT = {(byte)59, (byte)-113, (byte)-128, (byte)1, (byte)-128, (byte)79, (byte)12, (byte)-96, (byte)0, (byte)0, (byte)3, (byte)6, (byte)3, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)104};
	private final static byte[] ATR_MOBILE = {(byte)59, (byte)-127, (byte)128, (byte)1, (byte)-128, (byte)-128 };
	
	
	private final static String key = "A6B7C6";
	public Communication() throws CardException{
		this.setupTerminal();
	}
	
	//TESTING
	public static void main(String[] args){
		try {
		    Communication cc = new Communication();
		    System.out.println(cc.connect());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public byte[] readDataMobile() throws CardException{
		 
		 //Read the len of data
		 byte[] readCommand = new byte[]
	    		  {(byte)0x00, (byte)0xB0,(byte) 0x00, (byte)0x00, (byte)0x02};
	    
		 ResponseAPDU r;
		 r  = channel.transmit(new CommandAPDU(readCommand));
		 
		 System.out.println("Data recived: " + Arrays.toString(r.getData()));
		 System.out.println(r.getSW());
		 
		 short len = (short) ((r.getData()[0] << 8) & 0xff00);
		 len += r.getData()[1] & 0xff;

	
		 System.out.println("Lengt of data recived: " + len);
		 readCommand[4] = (byte)len;
		 readCommand[3] = 2;
		 //Read the data
		 r  = channel.transmit(new CommandAPDU(readCommand));
		 
		 System.out.println("Data recived: " + Arrays.toString(r.getData()));
		 
		 return r.getData();
		
	}
	public byte[] readDataCard() throws CardException, UnsupportedEncodingException{
		return typeCard.readCard();
	}
	
	private boolean contains(byte[] source, byte[] sub){
		
		int len = 0; int j= 0; int tmp = 0;
		for (int i = 0; i < source.length; i ++, j += tmp){
			
			if (j >= sub.length)
				break;

			if (source[i] == sub[j]){
				tmp = 1;
				len++;
			}
			else{
				tmp = 0;
				j = 0;
			}
		}

		if (len == sub.length)
			return true;
		
		return false;
	}
	private void selectAplet() throws CardException{
		ResponseAPDU r;
		
		byte[] selectCommand = {(byte)0x00, (byte) 0xA4, (byte) 0x04,  (byte)0x00, (byte)AID.length}; 
		byte[] APDU = new byte[selectCommand.length + AID.length];
		
		System.arraycopy(selectCommand, 0, APDU, 0, selectCommand.length);
		System.arraycopy(AID, 0, APDU ,selectCommand.length, AID.length);
		
		r = channel.transmit(new CommandAPDU(APDU));
		
		//Select Ndef
		byte[] NdefSelect = {0x00, (byte) 0xA4, 0x00, 0x0C, 0x02, (byte) 0xE1, 0x04};
		r = channel.transmit(new CommandAPDU(NdefSelect));
		
		
	}
	
	
	
	/**
	 * Setup the connection to the terminal
	 * @return
	 * @throws CardException
	 */
	private boolean setupTerminal() throws CardException{
		TerminalFactory factory = TerminalFactory.getDefault();
		terminal = factory.terminals().list().get(0);

		System.out.println("Terminals: " + terminal);
		return true;
	}

	
	/**
	 * Exclusive connection to the mobile
	 * @return
	 * @throws CardException
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidCardException - card type not supported
	 */
	public boolean connect() throws CardException, UnsupportedEncodingException{
		if (terminal == null)
			return false;
			
		if(terminal.waitForCardPresent(0)){
			card = terminal.connect("T=1");
			
			channel = card.getBasicChannel();
			byte[] tmpATR = card.getATR().getBytes(); 
			
			if (Arrays.equals(tmpATR, ATR_MIFARE_ULTRALIGHT)){
				typeCard = new MifareUltralight(card, channel);
				card.beginExclusive();
				byte[] data = readDataCard();
				System.out.println("Data:" + Arrays.toString(data));
				closeConnectionToCard();
				return contains(data, key.getBytes());
			}
			else{
				System.out.println("Mobile connected");
				selectAplet();
				card.beginExclusive();
				byte[] data = readDataMobile();
				System.out.println("Data:" + Arrays.toString(data));
				closeConnectionToCard();
				
				
				return contains(data,key.getBytes());
			}
		
		}
		
		else{
			System.out.println("No connection found");
			return false;
		}

		
	}

	/**
	 * Closes the exclusive connection to the card
	 * @throws CardException 
	 */
	public void closeConnectionToCard() throws CardException{
		card.endExclusive();
		card.disconnect(false);
		
		
	}


}
