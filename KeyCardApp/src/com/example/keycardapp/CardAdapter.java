package com.example.keycardapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
	
	//private rowGraphics graphics = null;
	
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
		CardData cardData = data[position];
		
		
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
			
			
			cardDataHolder.iconImage.setImageResource(IconHandler.getLayoutResourceIDfromIconID(cardData.rowImage));
			
			row.setTag(cardDataHolder);
		
		}
		else
		{
			cardDataHolder = (CardDataHolder)row.getTag();
		}
			
		cardDataHolder.txtCardName.setText(cardData.cardName);
		cardDataHolder.expireDate.setText(cardData.expireDate);
		
		if (cardData.role == 1) {
			//cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_received);
			if (!cardDataHolder.rowStatus.getDrawable().getConstantState().equals(
					rowGraphics.stateReceived.getConstantState())) {
				cardDataHolder.rowStatus.setImageDrawable(rowGraphics.stateReceived);
			}
		
		} else {
			if (cardData.shared) {
				//cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_shared);
				if (!cardDataHolder.rowStatus.getDrawable().getConstantState().equals(
						rowGraphics.stateShared.getConstantState())) {
					cardDataHolder.rowStatus.setImageDrawable(rowGraphics.stateShared);
				}
			} else {
				//cardDataHolder.rowStatus.setImageResource(R.drawable.row_state_private);
				if (!cardDataHolder.rowStatus.getDrawable().getConstantState().equals(
						rowGraphics.statePrivate.getConstantState())) {
					cardDataHolder.rowStatus.setImageDrawable(rowGraphics.statePrivate);
				}
			}
		}
		
		if (cardData.expired){
			//cardDataHolder.activeImage.setImageResource(R.drawable.background_expired);
			if (!cardDataHolder.activeImage.getDrawable().getConstantState().equals(
					rowGraphics.rowExpired.getConstantState())) {
			
				cardDataHolder.activeImage.setImageDrawable(rowGraphics.rowExpired);
			}
		}
		
		else {
			if (cardData.active) {
				//cardDataHolder.activeImage.setImageResource(R.drawable.background_active);
				
				if (!cardDataHolder.activeImage.getDrawable().getConstantState().equals(
						rowGraphics.rowActive.getConstantState())) {
				
					cardDataHolder.activeImage.setImageDrawable(rowGraphics.rowActive);
				}
			} else {
				//cardDataHolder.activeImage.setImageResource(R.drawable.background_disable);
				if (!cardDataHolder.activeImage.getDrawable().getConstantState().equals(
						rowGraphics.rowDisable.getConstantState())) {
					cardDataHolder.activeImage.setImageDrawable(rowGraphics.rowDisable);
				}
			}
		}
		
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
