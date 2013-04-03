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
			
			// Active image
			cardDataHolder.activeImage = (ImageView)row.findViewById(R.id.activeImage);
			
			// Left side icon
			cardDataHolder.iconImage = (ImageView)row.findViewById(R.id.rowIcon);
			
			row.setTag(cardDataHolder);
		
		}
		else
		{
			cardDataHolder = (CardDataHolder)row.getTag();
		}
		
		CardData cardData = data[position];
		
		cardDataHolder.txtCardName.setText(cardData.cardName);
		
		if (cardData.active) {
			cardDataHolder.activeImage.setImageResource(R.drawable.background_active);
		} else {
			cardDataHolder.activeImage.setImageResource(R.drawable.background_disable);
		}
		
		cardDataHolder.iconImage.setImageResource(getRowImageFromNum(cardData.rowImage));
		
		return row;
	}
	
	private int getRowImageFromNum(int imageNum){
		 switch (imageNum) {
         case 1:
        	 return R.drawable.row_icon_buss_big;
         case 2:
        	 return R.drawable.row_icon_block;
         case 3:
        	 return R.drawable.row_icon_house;
         case 4:
        	 return R.drawable.row_icon_factory;
    	 default:
    		 return R.drawable.icon_house;
		 }
	}
	
	static class CardDataHolder
	{
		TextView txtCardName;
		ImageView activeImage;
		ImageView iconImage;
		
	}
	
}
