package com.kingsandthings.common.model.things;

import javafx.scene.image.Image;

@SuppressWarnings("serial")
public class Treasure extends Thing {
	
	private int goldValue;
	
	public Treasure() { }
	
	public Treasure(String name, int goldValue, Image image) {
		super(name, image);
		
		this.goldValue = goldValue;
	}
	
	public int getGoldValue() {
		return goldValue;
	}
	
}
