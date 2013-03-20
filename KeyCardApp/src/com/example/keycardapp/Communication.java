package com.example.keycardapp;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.*;

public class Communication {

	private static final String BASE_URL = "http://129.242.22.146";
	private static final String LOGIN_URL = "/accounts/login";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static PersistentCookieStore myCookieStore = null;
	
	private static String userName = "";
	private static String password = "";
	
	private static Context context = null;
	
	public static void initCommunication(Context contx, String usrName, String usrPswrd) {
		myCookieStore = new PersistentCookieStore(contx);
		client.setCookieStore(myCookieStore);
		
		userName = usrName;
		password = usrPswrd;
		
		context = contx;
		
		initiateSession();
	}
	
	private static void initiateSession(){
		RequestParams params = new RequestParams();
		params.put("user", userName);
		params.put("password", password);
		
		AsyncHttpResponseHandler loginHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("ERROR IN LOGIN: " + msg);
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMsg("Succesfull Login");
		    }
		};
		
		get(LOGIN_URL, params, loginHandler);
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
	private static void printMsg(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
		
}
