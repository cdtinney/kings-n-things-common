package com.kingsandthings.common.model.things;

import java.util.logging.Logger;

import javafx.scene.image.Image;

@SuppressWarnings("serial")
public class Fort extends Thing {
	
	private static Logger LOGGER = Logger.getLogger(Fort.class.getName());
	
	public enum Type {
		TOWER,
		KEEP,
		CASTLE,
		CITADEL
	}
	
	private Type type;
	private int combatValue;

	private boolean neutralized = false;
	private boolean isPlaced = false;
	
	public Fort() {}

	public Fort(String type, int combatValue, Image image) {
        super(type, image);
        
        this.combatValue = combatValue;
        
        try {
        	this.type = Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
        	LOGGER.warning("Invalid fort type - " + type);
        }
        
    }
	
	public int getCombatValue() {
		return combatValue;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isPlaced() {
		return isPlaced;
	}
	
	public boolean isNeutralized() {
		return neutralized;
	}
	
	public void setPlaced(boolean placed) {
		isPlaced = placed;
	}

	@Override
	public String toString() {
		return name + " " +  type.toString() + " " + combatValue;
	}
	
	public static Fort getTower() {
		
		String imagePath = "/images/things/fort/-n Tower -a 1.jpg";
		Fort f = new Fort("Tower", 1, new Image(imagePath));
		f.setImagePath(imagePath);
		
		return f;
		
	}
	
}
