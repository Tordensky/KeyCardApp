package com.example.keycardapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardAdapter extends ArrayAdapter<CardData>{

	Context context;
	
	int layoutResourceId;
	
	CardData data[];
	
	public CardAdapter(Context context, int layoutResourceId, CardData[] data) {
		
		super(context, layoutResourceId, data);
		
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;			
		
		CardDataHolder cardDataHolder = null;
		
		if (row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			cardDataHolder = new CardDataHolder();
			
			cardDataHolder.txtCardName = (TextView)row.findViewById(R.id.cardName);
			
			row.setTag(cardDataHolder);
		
		}
		else
		{
			cardDataHolder = (CardDataHolder)row.getTag();
		}
		
		CardData cardData = data[position];
		
		cardDataHolder.txtCardName.setText(cardData.cardName);
		
		if (cardData.active) {
			cardDataHolder.txtCardName.setTextSize(30);
		}
		
		return row;
	}
	
	static class CardDataHolder
	{
		TextView txtCardName;
	}
	
}
