package com.example.keycardapp;

import nfc.EasyNfcReaderActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class ScanCardActivity extends EasyNfcReaderActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_card);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		 super.onNewIntent(intent);
		 Intent resultIntent = new Intent();
		 // TODO Add extras or a data URI to this intent as appropriate.
		 resultIntent.putExtra("mTagText", mTagText);
		 setResult(Activity.RESULT_OK, resultIntent);
		 finish();
	}

}
