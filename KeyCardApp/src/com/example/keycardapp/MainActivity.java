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
	private AsyncHttpClient client;
	
	
	
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
		
		Communication.initCommunication(this, "tom", "tom");
		
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
		//printMSG("Pressed: " + id);
		
		
		getMyCards();
		
		
		//testServer();
	}
	
	
	private void getMyCards(){
		
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMSG("Error in get cards: " + msg + "Exep: " + exep.getCause());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMSG(response);
		    }
		};
		
		
		Communication.get("/cards", new RequestParams(), handler);
	}
	
	
	private void printMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	
	private void testGet(){
		client.get("http://129.242.22.146/cards", new AsyncHttpResponseHandler() {
		    @Override
			public void onFailure(Throwable arg0, String arg1) {
		    	printMSG("ERROR " + arg1);
			}

			@Override
		    public void onSuccess(String response) {
		        printMSG(response);
		    }
		});
	}
	
	public void testServer() {
		printMSG("gets to test");
		
		RequestParams params = new RequestParams();
		
		params.put("user", "test");
		params.put("password", "test");
		
		
		client = new AsyncHttpClient();
		
		//client.setBasicAuth("test", "test", new AuthScope("http://129.242.22.146/accounts/login", 80, AuthScope.ANY_REALM));
		
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		client.setCookieStore(myCookieStore);
		
		client.get("http://129.242.22.146/accounts/login", params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
		    	printMSG("ERROR in login" + arg1);
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMSG("Login "+ response);
		        testGet();
		    }
		});
		
	}
}
