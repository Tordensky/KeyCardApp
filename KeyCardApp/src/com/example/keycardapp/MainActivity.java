package com.example.keycardapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends ListActivity {

	private CardAdapter adapter;
	private CardData[] values = null;

	private SharedData sharedData = null;
	
	private static final int DELETE = Menu.FIRST;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedData = new SharedData(this);
		
		//getMyCards();
		
		//getCardDataForTesting();
		
		registerForContextMenu(getListView());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getMyCards();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE, 0, "Delete Card");		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	
    	switch (item.getItemId()){
		case DELETE:
			CardAdapter adapter = (CardAdapter)this.getListAdapter();
			
			CardData rowData = (CardData)adapter.getItem(info.position);
			deleteCard(rowData.id);
			printMSG("Should delete card: " + rowData.cardName);
			return true;
    	}
    	return super.onContextItemSelected(item);
	}


	private void deleteCard(int cardID) {
		Communication.delete("/cards/"+cardID, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMSG("ERROR: Could not delete Card: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMSG("Deleted + " + response);
		        getMyCards();
		    }
		});
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
		};
		setListData();
	}

	private void getMyCards(){
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
						
						values[i] = new CardData(
								card.getInt("id"), 
								card.getString("name"),
								card.getString("exp_date"),
								card.getString("value"),
								card.getInt("cardIcon")
								);
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
