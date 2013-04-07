package com.example.keycardapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
			cardDataHolder.expireDate = (TextView)row.findViewById(R.id.expire_date);
			
			// Active image
			cardDataHolder.activeImage = (ImageView)row.findViewById(R.id.activeImage);
			
			// Left side icon
			cardDataHolder.iconImage = (ImageView)row.findViewById(R.id.rowIcon);
			
			cardDataHolder.rowStatus = (ImageView)row.findViewById(R.id.rowStatusIcon);
			
			row.setTag(cardDataHolder);
		
		}
		else
		{
			cardDataHolder = (CardDataHolder)row.getTag();
		}
		
		CardData cardData = data[position];
		
		cardDataHolder.txtCardName.setText(cardData.cardName);
		cardDataHolder.expireDate.setText(cardData.expire);
		
		if (cardData.role == 1) {
			cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_received);
		} else {
			if (cardData.shared) {
				cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_shared);
			} else {
				cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_private);
			}
		}
		
		if (cardData.active) {
			cardDataHolder.activeImage.setImageResource(R.drawable.background_active);
		} else {
			cardDataHolder.activeImage.setImageResource(R.drawable.background_disable);
		}
		
		cardDataHolder.iconImage.setImageResource(IconHandler.getLayoutResourceIDfromIconID(cardData.rowImage));
		
		return row;
	}
		
	static class CardDataHolder
	{
		TextView txtCardName;
		TextView expireDate;
		
		ImageView activeImage;
		ImageView iconImage;
		ImageView rowStatus;
		
	}
	
}
