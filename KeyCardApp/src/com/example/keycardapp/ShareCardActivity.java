package com.example.keycardapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareCardActivity extends Activity {

	private int cardID = -1;
	private String cardName = null;
	
	private TextView cardNameText = null;
	
	private EditText ShareUsername = null;
	
	private DatePicker datePicker = null;
	private Calendar calender;
	
	private Date date;
	private SimpleDateFormat dateFormat;
	
	private int day;
	private int month;
	private int year;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_card);
		
		Bundle extra = getIntent().getExtras();
		
		cardID = extra.getInt("cardID");
		cardName = extra.getString("cardName");
		
		cardNameText = (TextView)findViewById(R.id.share_card_name);
		cardNameText.setText(cardName);
		
		ShareUsername = (EditText)findViewById(R.id.share_user_name);
		
		datePicker = (DatePicker)findViewById(R.id.share_date);
		calender = Calendar.getInstance();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share_card, menu);
		return true;
	}
	
	private String getDate() {
		day = datePicker.getDayOfMonth();
		month = datePicker.getMonth();
		year = datePicker.getYear();
		
		calender.set(year, month, day);
		date = calender.getTime();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
		return dateFormat.format(date);
	}
	
	public void shareCard(View v) {
		String dateString = getDate();
		
		String username = ShareUsername.getText().toString();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("username", username);
			jsonObject.put("exp_date", dateString);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Communication.postJson("/cards/"+cardID+"/share", jsonObject, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("SERVER ERROR: Could not share Card: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
				printMsg("Shared card succesfully");
				finish();
		    }
		});
		
		
	}
	
	private void printMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
