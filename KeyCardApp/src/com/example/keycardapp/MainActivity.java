package com.example.keycardapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private CardAdapter adapter;
	private CardData[] values = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Communication.initCommunication(this, "tom", "tom");
		
		getMyCardsNew();	
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
		Toast.makeText(this, "Pressed: " + id, Toast.LENGTH_SHORT).show();
		printMSG("Pressed: " + id);
	}
	
	private void setListData(){
		adapter = new CardAdapter(this, R.layout.card_row, values);
		
		setListAdapter(adapter);
	}

	private void getMyCardsNew(){
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			

			@Override
			public void onSuccess(JSONObject cardData) {
				printMSG("GOT SUCCES JSON");
				try {
					JSONArray cards = cardData.getJSONArray("cards");
					
					values = new CardData[cards.length()];
					
					// Traverse Cards
					for (int i = 0; i < cards.length(); i++) {
						JSONObject card = (JSONObject) cards.get(i);
						
						values[i] = new CardData(card.getString("name"));
					}
					
					setListData();
				
				} catch (JSONException e) {
					e.printStackTrace();
					printMSG("PARSE ERROR");
				}
			}

			@Override
			public void onFailure(Throwable exep, String eror) {
				printMSG("Error in get cards! Exep: " + exep.getMessage());
				
				// Try to login again
				if (exep.getMessage().equalsIgnoreCase("Unauthorized")){
					printMSG("Tries to login");
					Communication.login();
				}
			}
		};

		Communication.get("/cards/", new RequestParams(), handler);
	}


	private void printMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
