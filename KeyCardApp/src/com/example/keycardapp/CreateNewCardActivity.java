package com.example.keycardapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class CreateNewCardActivity extends Activity {

	private Spinner spinner = null;
	private ImageSpinnerAdapter adapter;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_card, menu);
		return true;
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
