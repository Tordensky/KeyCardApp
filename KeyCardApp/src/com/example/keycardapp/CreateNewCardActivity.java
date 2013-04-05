package com.example.keycardapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class CreateNewCardActivity extends Activity {

	private Spinner spinner = null;
	private ImageSpinnerAdapter adapter;
	
	private int iconID = 0;
	
	private EditText cardNameField = null;
	private String cardName  = null;
	
	private DatePicker datePicker;
	private Calendar calender;
	private Date date;
	private SimpleDateFormat dateFormat;
	
	private int day;
	private int month;
	private int year;
	
	private boolean hasScannedCard = false;
	
	private JSONObject jsonObject = null;
	
	@SuppressWarnings("unused")
	private String cardData = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_card);
		
		adapter = new ImageSpinnerAdapter(this, R.layout.card_row, IconHandler.getIconsID());
		
		spinner = (Spinner)findViewById(R.id.image_spinner);
		spinner.setAdapter(adapter);
		initSpinner(spinner);
		
		
		datePicker = (DatePicker)findViewById(R.id.date_picker);
		calender = Calendar.getInstance();
		
		cardNameField = (EditText)findViewById(R.id.new_card_name);
	}
	
	private void initSpinner(Spinner spinner){
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        iconID = pos;
		        printMsg("Pos:" +pos);
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_card, menu);
		return true;
	}
	
	public void saveCard(View v) {
		if (!hasScannedCard) {
			printMsg("Note, you must scann a NFC card before you can save it");
		} 
		else {
			// Gets Card Name TODO check length
			cardName = cardNameField.getText().toString();
			
			// 
			
			// Gets expire date
			day = datePicker.getDayOfMonth();
			month = datePicker.getMonth();
			year = datePicker.getYear();
			
			calender.set(year, month, day);
			date = calender.getTime();
			dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
			String dateString = dateFormat.format(date);
			
			//Get card icon
			
						
			jsonObject = new JSONObject();
			try {
				jsonObject.put("name", cardName);
				jsonObject.put("cardIcon", iconID);
				jsonObject.put("exp_date", dateString);
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
		        printMsg("Created card succesfully");
		        finish();
		    }
		});
	}
	
	// TODO This is the method to read data from card
	public void readCard(View v) {
		// TODO ALEX! add function / action for reading in card data into byte[] cardData, if success set hasScannedCard to true
		
		// TODO Set this to TRUE when reading has completed successfully
		hasScannedCard = true;
		
		// TODO SET DATA read from card here!
		cardData = "";
		
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
					
					TextView name = (TextView)row.findViewById(R.id.cardName);
					name.setText("");
					
					TextView expire = (TextView)row.findViewById(R.id.expire_date);
					expire.setText("");
					
					row.setTag(image);
				}
				else
				{
					image = (ImageView)row.getTag();
				}
				
				
				image.setImageResource(IconHandler.getLayoutResourceIDfromIconID(resourceId[position]));
				
				
				return row;
		}
		
	}
}
