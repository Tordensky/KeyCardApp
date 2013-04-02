package com.example.keycardapp;

import java.util.GregorianCalendar;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class CreateNewCardActivity extends Activity {

	private Spinner spinner = null;
	private ImageSpinnerAdapter adapter;
	
	private EditText cardNameField = null;
	private String cardName  = null;
	
	private DatePicker datePicker;
	private GregorianCalendar calender;
	
	private int day;
	private int month;
	private int year;
	
	private boolean hasScannedCard = false;
	
	private JSONObject jsonObject = null;
	
	@SuppressWarnings("unused")
	private byte[] cardData = null;
	
	
	private Integer[] data = {
			R.drawable.row_icon_block,
			R.drawable.row_icon_buss_big,
			R.drawable.row_icon_factory,
			R.drawable.row_icon_block,
			R.drawable.row_icon_buss_big,
			R.drawable.row_icon_factory,
			R.drawable.row_icon_house
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_card);
		
		adapter = new ImageSpinnerAdapter(this, R.layout.card_row, data);
		
		spinner = (Spinner)findViewById(R.id.image_spinner);
		spinner.setAdapter(adapter);
		
		datePicker = (DatePicker)findViewById(R.id.date_picker);
		calender = new GregorianCalendar();
		
		cardNameField = (EditText)findViewById(R.id.new_card_name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_card, menu);
		return true;
	}
	
	public void saveCard(View v) {
		if (!hasScannedCard) {
			printMsg("Note, you must scann a nfc card before you can save it");
		} 
		else {
			// Gets expire date
			day = datePicker.getDayOfMonth();
			month = datePicker.getMonth();
			year = datePicker.getYear();
			
			calender.set(year, month, day);
			
			// Gets card name
			cardName = cardNameField.getText().toString();
			
			
			
			printMsg("Tryes to create new card: " + cardName + " Expire " + calender.getTime().toString());
		
			
			jsonObject = new JSONObject();
			try {
				jsonObject.put("name", cardName);
				jsonObject.put("cardIcon", 1);
				jsonObject.put("exp_date", calender.getTime().toString());
				jsonObject.put("value", "test card data value");
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			CreateOnServer();
		}
	}
	
	private void CreateOnServer() {
		Communication.postJson("/cards/new", jsonObject, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("ERROR: Could not create: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMsg("Created card + " + response);
		    }
		});
	}
	
	public void readCard(View v) {
		// TODO add function for reading in card data into byte[] cardData, if success set hasScannedCard to true
		hasScannedCard = true;
		printMsg("Tryes to read new card");
	}
	
	private void printMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	
	public class ImageSpinnerAdapter extends ArrayAdapter<Integer> implements SpinnerAdapter{

		Context context;
		int layoutResourceId;
		Integer[] resourceId;
		
		
		public ImageSpinnerAdapter(Context context, int layoutResourceId, Integer[] data) {
			super(context, layoutResourceId, data);
			// TODO Auto-generated constructor stub
			
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.resourceId = data;
		}


		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		};
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
		
		
		public View getCustomView(int position, View convertView, ViewGroup parent) {
				View row = convertView;	
				ImageView image = null;
				
				if (row == null)
				{
					LayoutInflater inflater = ((Activity)context).getLayoutInflater();
					row = inflater.inflate(layoutResourceId, parent, false);
					
					image = (ImageView)row.findViewById(R.id.rowIcon);
					
					row.setTag(image);
				}
				else
				{
					image = (ImageView)row.getTag();
				}
				
				image.setImageResource(resourceId[position]);
				
				
				return row;
		}
		
	}
}
