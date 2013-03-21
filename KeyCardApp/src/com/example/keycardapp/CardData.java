package com.example.keycardapp;

public class CardData {
	
	public String cardName;
	public Boolean active = false;
	
	public String expire = "";
	public String type = "";
	
	
	public CardData(){
		super();
	}
	
	public CardData(String cardName){
		super();
		this.cardName = cardName;
	}
	
	public CardData(String cardName, Boolean active){
		super();
		this.cardName = cardName;
		this.active = active;
	}
	
	public CardData(String cardName, Boolean active, String expire, String type){
		super();
		this.cardName = cardName;
		this.active = active;
		this.expire = expire;
		this.type = type;
	}
}
