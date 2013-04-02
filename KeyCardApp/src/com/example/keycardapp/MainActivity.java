package com.example.keycardapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private CardAdapter adapter;
	private CardData[] values = null;

	private SharedData sharedData = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Communication.initCommunication(this, "tom", "tom");
		
		sharedData = new SharedData(this);
		printMSG("TEST: " + sharedData.getUserName());
		printMSG("TEST: " + sharedData.getPassword());
		
		getMyCardsNew();
		
		//getCardDataForTesting();
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
		
		CardAdapter adapter = (CardAdapter)l.getAdapter();
		CardData rowData = (CardData)adapter.getItem(position);
		
		changeState(rowData);
		
		adapter.notifyDataSetChanged();
		
		//Toast.makeText(this, "Pressed: " + id + "Active: " + rowData.active, Toast.LENGTH_SHORT).show();
		
	}
	
	private void setListData(){
		adapter = new CardAdapter(this, R.layout.card_row, values);
		
		setListAdapter(adapter);
	}
	
	private void changeState(CardData rowData) {
		if (rowData.active) {
			rowData.active = false;
			
			// TODO Add code for deactivating card here
		} else {
			rowData.active = true;
			
			// TODO Add code for Activating card here
		}
	}
	
	// Binded in XML layout to button
	public void newCard(View v){
		Intent intent = new Intent(this, CreateNewCardActivity.class);
		startActivity(intent);
	}
	
	@SuppressWarnings("unused")
	private void getCardDataForTesting() {
		values = new CardData[] {
			new CardData("Busskort", true, 1),
			new CardData("Busskort", true, 2),
			new CardData("Busskort", true, 3),
			new CardData("Busskort", true, 4),
			new CardData("Busskort", true, 5),
			new CardData("Busskort", true),
			new CardData("Busskort", true)
			//new CardData("Jobbkort", false, 1),
			//new CardData("Studentkort", false, 1)
			//new CardData("Expertkort", false, 4),
			//new CardData("Busskort", true)
		};
		
		setListData();
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
					printMSG("Error login in");
					//Communication.login();
				}
			}
		};

		Communication.get("/cards/", new RequestParams(), handler);
	}


	private void printMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
