package com.example.keycardapp;

public class CardData {
	
	public String cardName;
	public Boolean active = false;
	
	public String expireDate = "";
	public Boolean expired = false;
	
	public String type = "";
	
	public String data = "";
	
	public int id;
	public int rowImage = 0;
	
	public int role = 0;
	public Boolean shared = false;
		
	public CardData(int id, String cardName, Boolean active, String expire, String data, int rowImage, int role, Boolean shared, Boolean expired){
		super();
		this.id = id;
		this.cardName = cardName;
		this.active = active;
		this.expireDate = expire;
		this.data = data;
		this.rowImage= rowImage;
		this.role = role;
		this.shared  = shared;
		this.expired = expired;
	}
	
	
}
