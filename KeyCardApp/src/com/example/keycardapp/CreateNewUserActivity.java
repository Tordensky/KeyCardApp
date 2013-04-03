package com.example.keycardapp;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewUserActivity extends Activity {

	private EditText userNameField = null;
	private EditText passwordField = null;
	private EditText passwordConfirmField = null;
	
	private JSONObject jsonObject = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_user);
		
		
		userNameField = (EditText)findViewById(R.id.new_user_name);
		passwordField = (EditText)findViewById(R.id.new_user_password);
		passwordConfirmField = (EditText)findViewById(R.id.new_user_confirm);
	}
	
	// NOTE Called from XML layout file
	public void createNewUser(View v) {
		String userName = userNameField.getText().toString();
		String password = passwordField.getText().toString();
		String confirmPassword = passwordConfirmField.getText().toString();
		
		if (password.equalsIgnoreCase(confirmPassword)) {
			printMsg("Password ok");
			
			jsonObject = new JSONObject();
			try {
				jsonObject.put("username", userName);
				jsonObject.put("password", password);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			CreateOnServer();
		}
		else {
			printMsg("Error, passwords does not match");
		}
	}
	
	private void CreateOnServer() {
		Communication.postJson("/accounts/new", jsonObject, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("ERROR: Could not create new user: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMsg("Created card + " + response);
		        finish();
		    }
		});
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_user, menu);
		return true;
	}
	
	private void printMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
