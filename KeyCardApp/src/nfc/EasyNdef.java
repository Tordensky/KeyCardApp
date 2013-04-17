package nfc;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.util.Log;

/**
 * An easy to use interface for creating NdefMessages, 
 * getting payloads from NdefMessages, andgetting NdefMessages 
 * from intents received from tags.
 * 
 * @author Tor Kreutzer
 *
 */
public class EasyNdef {
	
	/* Public interface */
	
	/**
	 * Creates a NdefMessafe from a String.
	 * 
	 * @param text The string containing the text to be put
	 * into the NdefMessage.
	 * 
	 * @return A NdefMessage containing the text message.
	 * 
	 * NOTE: Creates exactly 1 message. Still useful since MIFARE 
	 * Ultralight tags only have room for 1 message. But keep in 
	 * mind if other tags are used.
	 */
	public static NdefMessage ndefFromString(String text) {
		NdefMessage msg;
		NdefRecord record;
		
		record = recordFromString(text);
		msg = new NdefMessage (new NdefRecord[] { record } );
		
		return msg;
	}
	
	/**
	 * <pre>
	 * Collects a NdefMessage Array from an intent created when a NFC-tag
	 * is near.
	 * 
	 * NOTE: Only supports exactly 1 message. Still useful since 
	 * MIFARE Ultralight tags only have room for 1 message. But keep in mind 
	 * if other tags are used.
	 * </pre>
	 * @param intent The intent created by a NFC-tag being near.
	 * 
	 * @return the NdefMessage contained in the Intent.
	 */
	public static NdefMessage getNdefMessageFromIntent(Intent intent) {
        NdefMessage msg;
		
		Parcelable[] msgs = 
			intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		
        /* supports only 1 message and 1 record */
        msg = (NdefMessage) msgs[0];
		
		return msg;
	}
	
	public static Tag getTagFromIntent(Intent intent) {
		Tag tag;
		tag = (Tag)intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		return tag;
	}
	
	/**
	 * Gets the payload from the NdefMessage.
	 * @param msg The NdefMessage.
	 * @return The payload as a String.
	 */
	public static String getPayload(NdefMessage msg) {
		
		byte type;
        String data = "";
        NdefRecord record;
      		
        /* supports only 1 message and 1 record */
        record = msg.getRecords()[0];
        
        type = record.getType()[0];
        Log.d("NDEF_TYPE", "Got message of type: " + type);
        
        switch (type) {
        case 0x53: /* SMART POSTER */
        	// NOT SUPPORTED
        	break;
        case 0x54: /*TEXT*/
        	data = getText(record.getPayload());
        	break;
        case 0x55: /*URI*/
        	data = getURI(record.getPayload());
        	break;
        default:
        	Log.e("NDEF", "Got something crazy");
        	break;
        }
        
		return data;
	}
	
	/* Private methods */
	
	/**
	 * Creates a new Ndef Record containig plain text.
	 * @param txt The mesage to put in the record.
	 * @return A new record with the input text.
	 * @throws UnsupportedEncodingException
	 */
	private static NdefRecord recordFromString(String txt) {
	    String text       = txt;
	    String lang       = "en";
	    byte[] textBytes  = text.getBytes();
	    byte[] langBytes, payload;
	    int langLength, textLength;
	    NdefRecord record = null;
	    
	    try {
			langBytes = lang.getBytes("US-ASCII");
		
		    langLength = langBytes.length;
		    textLength = textBytes.length;
		    payload    = new byte[1 + langLength + textLength];

		    // set status byte (see NDEF spec for actual bits)
		    payload[0] = (byte) langLength;
	
		    // copy langbytes and textbytes into payload
		    System.arraycopy(langBytes, 0, payload, 1,              langLength);
		    System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);
	
		    record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, 
		                                       NdefRecord.RTD_TEXT, 
		                                       new byte[0], 
		                                       payload);

	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return record;
	}
	
	
	

	/**
	 * Get the text from the pay-load.
	 * @param payload
	 * @return text
	 */
	private static String getText(final byte[] payload) {
        
		String textEncoding, text = null;
		int languageCodeLength;
		
		if(payload == null) 
        	return text;
		
        try {
            textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";          
            languageCodeLength = payload[0] & 0077;
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
	
	/**
	 * Get the uri from the pay-load
	 * @param payload
	 * @return uri
	 */
	private static  String getURI(final byte[] payload) {
		String prefix, uri;

		if(payload == null) 
        	return null;
		
		prefix = uriIdentifierCode(payload[0]);
		uri = prefix + new String(payload, 1, payload.length -1);
		
		return uri;
	}
	
	/**
	 * Returns the URI prefix. 
	 * @param b
	 * @return the URI prefix for the URI identifier code given.
	 */
	private static String uriIdentifierCode(byte b)
	{
	    /*
	     * Section 3.2.2 "URI Identifier Code" of "URI Record Type Definition
	     * Technical Specification"
	     */
	    switch (b) {
	    case 0x00:
	        return "";
	    case 0x01:
	        return "http://www.";
	    case 0x02:
	        return "https://www.";
	    case 0x03:
	        return "http://";
	    case 0x04:
	        return "https://";
	    case 0x05:
	        return "tel:";
	    case 0x06:
	        return "mailto:";
	    case 0x07:
	        return "ftp://anonymous:anonymous@";
	    case 0x08:
	        return "ftp://ftp.";
	    case 0x09:
	        return "ftps://";
	    case 0x0A:
	        return "sftp://";
	    case 0x0B:
	        return "smb://";
	    case 0x0C:
	        return "nfs://";
	    case 0x0D:
	        return "ftp://";
	    case 0x0E:
	        return "dav://";
	    case 0x0F:
	        return "news:";
	    case 0x10:
	        return "telnet://";
	    case 0x11:
	        return "imap:";
	    case 0x12:
	        return "rtsp://";
	    case 0x13:
	        return "urn:";
	    case 0x14:
	        return "pop:";
	    case 0x15:
	        return "sip:";
	    case 0x16:
	        return "sips:";
	    case 0x17:
	        return "tftp:";
	    case 0x18:
	        return "btspp://";
	    case 0x19:
	        return "btl2cap://";
	    case 0x1A:
	        return "btgoep://";
	    case 0x1B:
	        return "tcpobex://";
	    case 0x1C:
	        return "irdaobex://";
	    case 0x1D:
	        return "file://";
	    case 0x1E:
	        return "urn:epc:id:";
	    case 0x1F:
	        return "urn:epc:tag:";
	    case 0x20:
	        return "urn:epc:pat:";
	    case 0x21:
	        return "urn:epc:raw:";
	    case 0x22:
	        return "urn:epc:";
	    case 0x23:
	        return "urn:nfc:";
	    default:
	        return "RFU";
	    }
	}

}
