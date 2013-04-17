package com.example.keycardapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedData {
	private static final String APP_SHARED_PREFS = SharedData.class.getSimpleName();
	private SharedPreferences _sharedPrefs;
    private Editor _prefsEditor;
	
	public SharedData(Context context) {
		this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
	}

	public void setCredentials(String userName, String password){
		setUserName(userName);
		setPassword(password);
		
		_prefsEditor.putBoolean("hasCreds", true);
		_prefsEditor.commit();
	} 
	
	public String getUserName() {
		return _sharedPrefs.getString("username", "");
	}

	private void setUserName(String userName) {
		_prefsEditor.putString("username", userName);
		_prefsEditor.commit();
				
	}

	public String getPassword() {
		return _sharedPrefs.getString("password", "");
	}

	private void setPassword(String password) {
		_prefsEditor.putString("password", password);
		_prefsEditor.commit();
	}
	
	public boolean hasCredentials() {
		return _sharedPrefs.getBoolean("hasCreds", false);
	}
	
	public void clearCredentialsData() {
		setCredentials("", "");
		_prefsEditor.putBoolean("hasCreds", false);
		_prefsEditor.commit();
	}
	
	public void setActiveCard(int id) {
		_prefsEditor.putInt("cardActiveID", id);
		_prefsEditor.commit();
	}
	
	public int getActiveCard() {
		return _sharedPrefs.getInt("cardActiveID", -1);
	}
	
	public void setNonCardActive() {
		setActiveCard(-1);
	}
	
}
