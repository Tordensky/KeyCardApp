package com.example.simcommunicationandroidtest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SimTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sim_test);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sim_test, menu);
		return true;
	}

}
