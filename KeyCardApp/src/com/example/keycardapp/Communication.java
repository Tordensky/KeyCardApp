package com.example.keycardapp;

import java.io.UnsupportedEncodingException;

import org.apache.http.auth.AuthScope;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.*;

public class Communication {

	//private static final String BASE_URL = "http://129.242.22.146";

	private static final String BASE_URL = "http://129.242.22.146";
	private static final String LOGIN_URL = "/accounts/login/";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static PersistentCookieStore myCookieStore = null;
	
	private static String userName = "";
	private static String password = "";
	
	private static SharedData sharedData = null;
	
	private static Context context = null;
	
	public static void initCommunication(Context contx, String usrName, String usrPswrd) {
		if (myCookieStore == null){
			myCookieStore = new PersistentCookieStore(contx);
			client.setCookieStore(myCookieStore);
		}
		
		client.setBasicAuth(usrName, usrPswrd, new AuthScope(BASE_URL, 80, AuthScope.ANY_REALM));
		
		userName = usrName;
		password = usrPswrd;
		
		context = contx;
		
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
		
		
		
		login(loginHandler);
	}
	
	public static void initCommunication(Context contx, String usrName, String usrPswrd, AsyncHttpResponseHandler responseHandler) {
		if (myCookieStore == null){
			myCookieStore = new PersistentCookieStore(contx);
			client.setCookieStore(myCookieStore);
			
			client.setBasicAuth(usrName, usrPswrd, new AuthScope(BASE_URL, 80, AuthScope.ANY_REALM));
		}
		
		userName = usrName;
		password = usrPswrd;
		
		context = contx;
		
		login(responseHandler);
	}
	
	public static void login(AsyncHttpResponseHandler loginHandler) {
		RequestParams params = new RequestParams();
		
		params.put("user", userName);
		params.put("password", password);
		
		sharedData = new SharedData(context);
		
		client.get(getAbsoluteUrl(LOGIN_URL), params, loginHandler);
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {		
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
		client.delete(getAbsoluteUrl(url), responseHandler);
	}
	
	public static void postJson(String url, JSONObject object, AsyncHttpResponseHandler responseHandler) {
		// params is a JSONObject
		StringEntity se = null;
		try {
		  se = new StringEntity(object.toString());
		} catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
		  return;
		}
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

		client.post(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
		
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
	
	public static void logout(AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl("/accounts/logout"), responseHandler);
		
		myCookieStore.clear();
		sharedData.clearCredentialsData();
		password = "";
		userName = "";
	}
		
}
