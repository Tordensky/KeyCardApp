package com.example.keycardapp;

public class CardData {
	
	public String cardName;
	public Boolean active = false;
	
	
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
}
