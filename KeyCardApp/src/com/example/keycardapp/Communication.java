package com.example.keycardapp;

import org.apache.http.auth.AuthScope;
import org.apache.http.cookie.Cookie;

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
		client.setBasicAuth("test", "test", new AuthScope("http://129.242.22.146", 80, AuthScope.ANY_REALM));
		
		userName = usrName;
		password = usrPswrd;
		
		context = contx;
		
		login();
	}
	
	public static void login() {
		RequestParams params = new RequestParams();
		
		AsyncHttpResponseHandler loginHandler = new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable exep, String msg) {
				printMsg("ERROR: Could not login: " + msg + " Exep: " + exep.getMessage());
			}
			
			@Override
		    public void onSuccess(String response) {
		        printMsg("Succesfull Login");
		    }
		};
		
		params.put("user", userName);
		params.put("password", password);
		
		client.get(getAbsoluteUrl(LOGIN_URL), params, loginHandler);
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {		
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
	private static void printMsg(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static Cookie getCookieByName(String name) {
		for (Cookie cookie : myCookieStore.getCookies()) {
			if (cookie.getName().equalsIgnoreCase(name)){
				return cookie;
			}
		}
		return null;
	}
	
	public static void printCookies() {
		for (Cookie cookie : myCookieStore.getCookies()) {
			printMsg("Name: "+ cookie.getName() + "DOMAIN: " + cookie.getDomain() + " PATH: " + cookie.getPath() + " EXP: " + cookie.getExpiryDate());
		}
	}
		
}
