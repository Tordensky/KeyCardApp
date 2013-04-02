package no.uit.smartcard;

import java.util.Arrays;

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
	
	private byte[] AID = {(byte)0xD2,0x76, 0x00,0x00, (byte)0x85,0x01,0x01};
	
	public Communication() throws CardException{
		this.setupTerminal();
	}
	
	//TESTING
	public static void main(String[] args){
		try {
		    Communication cc = new Communication();
		    cc.connectToMobile();
		    cc.readData();
		    cc.closeConnectionToCard();
			
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public byte[] readData() throws CardException{
		 
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
	 * @throws InvalidCardException - card type not supported
	 */
	public boolean connectToMobile() throws CardException{
		if (terminal == null)
			return false;
			
		if(terminal.waitForCardPresent(0)){
			card = terminal.connect("T=1");
			
			channel = card.getBasicChannel();
			System.out.println(card.getATR());

			selectAplet();
			
			card.beginExclusive();
			return true;
		}
		
		else{
			System.out.println("No mobile found");
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
