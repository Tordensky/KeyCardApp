package com.example.keycardapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simalliance.openmobileapi.SEService;

import com.library.UICCCommunication.SimCommunication;
import com.loopj.android.http.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends ListActivity {

	private CardAdapter adapter;
	private CardData[] values = null;

	private SharedData sharedData = null;
	
	private TextView backgroundText = null;
	
	private static final int DELETE = Menu.FIRST;
	private static final int SHARE = Menu.FIRST + 3;
	
	private static final int LOGOUT = Menu.FIRST + 1;
	private static final int REFRESH = Menu.FIRST + 2; 
	
	private SimCommunication sim; 
	private SEService seService; 
	final String LOG_TAG = "HelloSmartcard";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedData = new SharedData(this);
		
		rowGraphics.initGraphics(this);
				
		//getCardDataForTesting();
		
		// THIS IS FOR SIM COMMUNICATION
		sim = new SimCommunication();
		try {
		    Log.i(LOG_TAG, "creating SEService object");
		    seService = new SEService(this, null);
		} catch (SecurityException e) {
		    Log.e(LOG_TAG, "Binding not allowed, uses-permission org.simalliance.openmobileapi.SMARTCARD?");
		} catch (Exception e) {
		    Log.e(LOG_TAG, "Exception: " + e.getMessage());
		}
			 
		backgroundText = (TextView)findViewById(android.R.id.empty);
		
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
		
		menu.add(0, SHARE, 0, "Share Card");
		menu.add(0, DELETE, 0, "Delete Card");	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	CardAdapter adapter = (CardAdapter)this.getListAdapter();
		CardData rowData = (CardData)adapter.getItem(info.position);
		
    	
    	switch (item.getItemId()){
		case DELETE:
			deleteCard(rowData.id);
			return true;
			
		case SHARE:
			if (rowData.expired == false){
				printMSG("SHARE CARD");
				Intent intent = new Intent(this, ShareCardActivity.class);
				intent.putExtra("cardID", rowData.id);
				intent.putExtra("cardName", rowData.cardName);
				startActivity(intent);
			} else {
				printMSG("Can't share expired card");
			}
    	}
    		
    	return super.onContextItemSelected(item);
	}


	private void deleteCard(int cardID) {
		Communication.delete("/cards/"+cardID, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMSG("SERVER ERROR: Could not delete Card: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
				printMSG("Deleted card");
		        getMyCards();
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, LOGOUT, 0, "Log out");
		menu.add(0, REFRESH, 0, "Refresh");
		
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
	    {
	    case LOGOUT:
	        logout();
	        break;
	    case REFRESH:
	    	getMyCards();
	        break;
	    default:
	    	printMSG("NOT IMPLEMENTED");
	        break;
	    }
	    return true;
	}
	
	private void logout() {
		Communication.logout(new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMSG("ERROR: Could not logout: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMSG("Loged out from server");
		        gotoLogin();
		    }
		});
	}
	
	private void gotoLogin(){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		CardAdapter adapter = (CardAdapter)l.getAdapter();
		
		CardData rowData = (CardData)adapter.getItem(position);
		
		changeState(rowData, adapter);
		
		adapter.notifyDataSetChanged();
	}
	
	private void setListData(){
		adapter = new CardAdapter(this, R.layout.card_row, values);
		
		
		setListAdapter(adapter);
	}
	
	private void changeState(CardData rowData, CardAdapter adapter) {
		if (!rowData.expired) {
			if (rowData.active) {
				rowData.active = false;
				
				sharedData.setNonCardActive();
				
				final byte[] empty = new byte[] {(byte)0xD0, 0x00, 0x00};
		     	
				new Thread(new Runnable() {
					    public void run() {
					    	sim.writeData(seService, empty);
					    }
					  }).start();
				
				
			} else {
				setAllActiveToFalse(adapter);
				rowData.active = true;
				
				sharedData.setActiveCard(rowData.id);
				
				final String dataToPutOnCard = rowData.data;

				new Thread(new Runnable() {
				    public void run() {
				    	sim.writeData(seService, dataToPutOnCard.getBytes());
				    }
				  }).start();
				
			}
		}
		else {
			printMSG("Cant activate expired card");
		}
	}
	
	private void setAllActiveToFalse(CardAdapter adapter) {
		for (int i = 0; i < adapter.getCount(); i++) {
			adapter.getItem(i).active = false;
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
			new CardData(0, "Husnøkkel", false, "24.05.2014 18:45:23", "Dette er et flykort", 0, 0, false, false),
			new CardData(1, "Busskort", false, "22.03.2013 19:45:23", "Dette er et busskort", 1, 1, false, false),
			new CardData(2, "Flybillet", false, "24.05.2014 18:45:23", "Dette er et flykort", 4, 0, true, true),
			new CardData(3, "Flybillet", false, "24.05.2014 18:45:23", "Dette er et flykort", 4, 0, false, true)
		};
		
		for (CardData value : values) {
			if (value.id == sharedData.getActiveCard()) {
				value.active = true;
			}
		}
			
		setListData();
	}

	private void getMyCards(){
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONObject cardData) {
				try {
					JSONArray cards = cardData.getJSONArray("cards");
					
					int numberOfCardsFromServer = cards.length();
					
					if (numberOfCardsFromServer == 0) {
						backgroundText.setText("No cards, press \n\"new card\"\n to add a new card");
					}
					
					values = new CardData[numberOfCardsFromServer];
					
					int activeID = sharedData.getActiveCard();
					boolean cardActive = false;
					
					// Traverse Cards
					for (int i = 0; i < cards.length(); i++) {
						JSONObject card = (JSONObject) cards.get(i);
						
						
						int id = card.getInt("id");
						
						cardActive = false;
						if (id == activeID) {
							cardActive = true;
						}
						
						values[i] = new CardData(
								id,
								card.getString("name").substring(0, 1).toUpperCase() +
								card.getString("name").substring(1).toLowerCase(),
								cardActive,
								card.getString("exp_date"),
								card.getString("value"),
								card.getInt("cardIcon"), 
								card.getInt("role"),
								card.getBoolean("shared"),
								card.getBoolean("expired")
								);
					}
					
					setListData();
					
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
				printMSG("Cards synced with server");
			}

			@Override
			public void onFailure(Throwable exep, String eror) {
				printMSG("Error in get cards! Exep: " + exep.getMessage());
				
				// Try to login again
				if (exep.getMessage().equalsIgnoreCase("Unauthorized")){
					printMSG("Error, not loged in");
					logout(); 
					
				} else {
					backgroundText.setText("Error, server unavailable");
				}
			}
		};
		Communication.get("/cards/", new RequestParams(), handler);
	}


	
	@Override
	protected void onDestroy() {
	   if (seService != null && seService.isConnected()) {
	      seService.shutdown();
	   }
	   super.onDestroy();
	} 
	
	
	private void printMSG(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
