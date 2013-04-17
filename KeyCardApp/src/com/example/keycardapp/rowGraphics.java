package com.example.keycardapp;

import android.content.Context;
import android.graphics.drawable.Drawable;

class rowGraphics
{

	public static Drawable rowDisable = null;
	public static Drawable rowActive = null;
	public static Drawable rowExpired = null;
	
	public static Drawable stateShared = null;
	public static Drawable statePrivate = null;
	public static Drawable stateReceived = null;
	
	
	public static void initGraphics(Context context) {
		
		rowActive = context.getResources().getDrawable( R.drawable.background_active );
		rowDisable = context.getResources().getDrawable( R.drawable.background_disable );
		rowExpired = context.getResources().getDrawable( R.drawable.background_expired);
		
		statePrivate = context.getResources().getDrawable( R.drawable.row_state_private );
		stateReceived = context.getResources().getDrawable( R.drawable.row_state_received );
		stateShared = context.getResources().getDrawable( R.drawable.row_state_shared);
	}
	
}
