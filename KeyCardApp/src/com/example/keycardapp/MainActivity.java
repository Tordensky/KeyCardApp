package com.example.keycardapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private ArrayAdapter<?> adapter;
	private String[] values = {"Funker", "Fett", "Nu"};
	
	
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
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "Pressed: " + id, Toast.LENGTH_SHORT).show();
	}
	
	
	public class CardAdapter extends ArrayAdapter<String>{

		Context context;
		int layoutResourceId;
		String data[];
		
		public CardAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			
			this.layoutResourceId = textViewResourceId;
			this.context = context;
			this.data = objects;
			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//return super.getView(position, convertView, parent);
			View row = convertView;
			TextView text;
			
			if (row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);
				
				text = (TextView)row.findViewById(R.id.cardName);
				
				row.setTag(text);
			
			}
			else
			{
				text = (TextView)row.getTag();
			}
			
			String rowText = data[position] + "HORE";
			
			text.setText(rowText);
			
			return row;
		}
		
	}
	
}
