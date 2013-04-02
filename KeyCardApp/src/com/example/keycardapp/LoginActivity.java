package com.example.keycardapp;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText userNameField = null;
	private EditText passwordField = null;
	private String userName = null;
	private String password = null;
	private SharedData sharedData = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		userNameField = (EditText)findViewById(R.id.login_username);
		passwordField = (EditText)findViewById(R.id.login_password);
		
		sharedData = new SharedData(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (sharedData.hasCredentials()) {
			// TODO use cookie to check if the user has a session
			
			userName = sharedData.getUserName();
			password = sharedData.getPassword();
			
			userNameField.setText(userName);
			passwordField.setText(password);
			login();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	// Called from XML file
	public void loginButtonAction(View v) {
		userName = userNameField.getText().toString();
		password = passwordField.getText().toString();
		
		login();
	}
	
	private void login() {
		
		Communication.initCommunication(this, userName, password, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("ERROR: Could not login: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMsg("Succesfull Login");
		        sharedData.setCredentials(userName, password);
		        startMainActivity();
		    }
		});
	}
	
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void newButtonAction(View v) {
		Toast.makeText(this, "NOTE: Not implemented", Toast.LENGTH_LONG).show();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		//overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	private void printMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
