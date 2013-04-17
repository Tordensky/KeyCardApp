package nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;

/**
 * 
 * @author Tor Kreutzer
 *
 */
public class EasyNfcP2PActivity extends EasyNfcReaderActivity {
	
	/**
	 * Identifier for the message to be pushed to the 
	 * other device found in the intents extras.
	 */
	public static final String MESSAGE = "msg";
	
	/** 
	 * This message is filled with the message to be 
	 * exchanged with the other device.
	 */
    protected NdefMessage mMessage;

	
	/** 
	 * Called when the activity is first created.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get message to exchange from intent
        String msg = getIntent().getStringExtra(MESSAGE);
        
        // Generate message containing local address and uuid
        if (msg != null)
        	mMessage = EasyNdef.ndefFromString(msg);
    }
    
    /**
     * When a NFC-tag or NFC-techology is near, a new intent is
	 * created and sent here.
     */
    @Override
	protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
	}
    
    /**
     * Overriden to enable foreground Ndef push.
     * This will make the tag of this device act as it contains
     * the message found in the given NdefMessage mMessage.
     */
	@Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) { 
        	mNfcAdapter.setNdefPushMessage(mMessage, this);
        }
    }
	
	/**
	 * Disable foreground Ndef push on Pause to make the 
	 * devices tag visible when the Activity is inactive.
	 */
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
        	mNfcAdapter.setNdefPushMessage(null, this);
        }
    }

	
}
