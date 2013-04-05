package com.example.simcommunicationandroidtest;

import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.SEService.CallBack;
import com.library.UICCCommunication.SimCommunication;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class SimTest extends Activity {
	private SEService seService; 
	private SimCommunication communication;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
      final String LOG_TAG = "HelloSmartcard";
	  communication = new SimCommunication();
	  super.onCreate(savedInstanceState);
	  
	  try {
		    Log.i(LOG_TAG, "creating SEService object");
		    seService = new SEService(this, null);
		  } catch (SecurityException e) {
		    Log.e(LOG_TAG, "Binding not allowed, uses-permission org.simalliance.openmobileapi.SMARTCARD?");
		  } catch (Exception e) {
		    Log.e(LOG_TAG, "Exception: " + e.getMessage());
		  }
		 
	  
	  
	  LinearLayout layout = new LinearLayout(this);
	  layout.setLayoutParams(new LayoutParams(
	          LayoutParams.WRAP_CONTENT,
	          LayoutParams.WRAP_CONTENT));

	  Button button = new Button(this);
	  button.setLayoutParams(new LayoutParams(
	          LayoutParams.WRAP_CONTENT,
	          LayoutParams.WRAP_CONTENT));
	  button.setText("Click Me");
	  button.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
	    	byte[] empty = new byte[] {(byte)0xD0, 0x00, 0x00};
	     	//communication.writeData(seService, empty);
	     	//Log.d(LOG_TAG ,"data on card " + communication.readData(seService));
	    	
	    }
	  });

	  layout.addView(button);
	  setContentView(layout);
	  
	  

	}


	@Override
	protected void onDestroy() {
	   if (seService != null && seService.isConnected()) {
	      seService.shutdown();
	   }
	   super.onDestroy();
	} 
}
