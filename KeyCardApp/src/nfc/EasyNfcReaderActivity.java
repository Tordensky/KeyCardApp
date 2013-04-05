package nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

/**
 * <pre>
 * An extension of the Activity class.
 * Used to create a simplified environment for using NFC 
 * reading capabilities for Android.
 * 
 * (DEPRICATED)
 * This class i meant to be overridden.
 * Classes extending this class MUST implement onNewIntent,
 * and to get access to the tag text read, the method MUST
 * be as in the example below.
 * 
 * example:
 * 
 * 	public class ExampleActivity extends EasyNfcReaderActivity {
 * 			.
 * 			. 
 *			protected void onNewIntent(Intent intent) {
 *				super.onNewIntent(intent);
 *
 *				// mTagText now contains the NFC-tag text 
 *			} 
 * 	}
 * 
 * New Interface:
 * The following new interface should give the same features as the old one
 * 
 * Override the method private 'void onReadTag(String tagText)'. This method is 
 * called when a new tag is read, and the text read from the tag is found in the
 * input string 'tagText'.
 * To read tags, this method is to be used.
 * 
 * More detail under onNewIntent.
 * </pre>
 * @author Tor Kreutzer
 *
 */
public class EasyNfcReaderActivity extends Activity {
	
	/** 
	 * Used to prepare for NFC intents.
	 */
	private PendingIntent mPendingIntent;
	
	/** 
	 * Local NfcAdapter.
	 * Needed this to enable foreground dispatch which makes the
	 * activity prioritized for receiving intents created when
	 * the NFC-device discovers an NFC-tag or NFC-technology.
	 */
	protected NfcAdapter mNfcAdapter;
	
	/** 
	 * The location NFC-tag-messages that are read are placed.
	 * When onNewIntent is called, this String will contain 
	 * the text from the tag.
	 */
	protected String mTagText;
	
	/** 
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        
        // Check if Activity was started by an NFC intent
        Intent i = getIntent();
        String action = i.getAction();
    	NdefMessage msg;
    	
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
    		msg = EasyNdef.getNdefMessageFromIntent(i);
    		mTagText = EasyNdef.getPayload(msg);
    	} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
    		mTagText = "";
    	}
    }

    /**
     * <pre>
     * When a NFC-tag or NFC-techology is near, a new intent is
	 * created and sent here.
	 * 
	 * (DEPRICATED)
     * This method MUST be overridden when extending this class.
     * The text located on the NFC-tag is located in mTagText
     * after this method is called.
     * 
     * example:
     * 
     * 	public class ExampleActivity extends EasyNfcReaderActivity {
     * 			.
     * 			. 
     *			protected void onNewIntent(Intent intent) {
	 *				super.onNewIntent(intent);
	 *				// mTagText now contains the NFC-tag text
	 *				Toast.makeText(this, mTagText, Toast.LENGTH_LONG).show();
	 *			} 
     * 	}
     * </pre>
     */
    @Override
	protected void onNewIntent(Intent intent) {
    	String action;
    	NdefMessage msg;
    	
    	action = intent.getAction();
    	Log.d("ACTION", action);
    	if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
    		msg = EasyNdef.getNdefMessageFromIntent(intent);
    		mTagText = EasyNdef.getPayload(msg);
    	} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
    		mTagText = "";
    	}
    	onReadTag(mTagText);
	}
	
    /**
     * Called when a NFC tag is touched by the handset. The text read from the
     * tag is input to this callback.
     * 
     * To be overridden.
     * 
     * @param tagText - The tag text read from the NFC tag.
     */
    public void onReadTag(String tagText) {
	}
    
    /**
     * Overriden to enable foreground dispatch.
     * This will make this activity receive NFC intents without 
     * having to choose it in the TechListChooserActivity. 
     */
	@Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) { 
        	mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }
	
	/**
	 * Foreground dispatch must be disabled in onPause.
	 * If not, no other activity will ever be alerted by new NFC intents.
	 */
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
        	mNfcAdapter.disableForegroundDispatch(this);
        }
    }
}