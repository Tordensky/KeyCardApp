package com.example.keycardapp;

public class CardData {
	
	public String cardName;
	public Boolean active = false;
	
	public String expire = "";
	public String type = "";
	
	public String data = "";
	
	public int id;
	public int rowImage = 0;
	
	public int role = 0;
	public Boolean shared = false;
	
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
	
	public CardData(String cardName, Boolean active, int rowImage){
		super();
		this.cardName = cardName;
		this.active = active;
		this.rowImage = rowImage;
	}
	
	public CardData(int id, String cardName,  String expire, String data, int rowImage){
		super();
		
		this.id = id;
		this.cardName = cardName;
		this.expire = expire;
		this.data = data;
		this.rowImage= rowImage;
	}
	
	public CardData(String cardName, Boolean active, String expire, String type, int rowImage){
		super();
		this.cardName = cardName;
		this.active = active;
		this.expire = expire;
		this.type = type;
		this.rowImage= rowImage;
	}
	
	public CardData(int id, String cardName, Boolean active, String expire, String data, int rowImage, int role, Boolean shared){
		super();
		this.id = id;
		this.cardName = cardName;
		this.active = active;
		this.expire = expire;
		this.data = data;
		this.rowImage= rowImage;
		this.role = role;
		this.shared  = shared;
	}
	
	
}
