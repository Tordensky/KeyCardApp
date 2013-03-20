package com.example.keycardapp;

import com.loopj.android.http.*;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private CardAdapter adapter;
	private CardData[] values = new CardData[] 
	{
			new CardData("House Key"), 
			new CardData("Job Card"),
			new CardData("School Card"),
			new CardData("Buss Card"),
			new CardData("Girlfriend Key"),
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		adapter = new CardAdapter(this, R.layout.card_row, values);
		
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Toast.makeText(this, "Pressed: " + id, Toast.LENGTH_SHORT).show();
		printMSG("Pressed: " + id);
		testServer();
	}
	
	private void printMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	private void testServer() {
		printMSG("gets to test");
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://www.google.com", new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		        printMSG(response);
		    }
		});
	}
}
