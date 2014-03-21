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

	public Fort(String type, int combatValue, Image image) {
        super(type, image);
        
        this.combatValue = combatValue;
        
        try {
        	this.type = Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
        	LOGGER.warning("Invalid fort type - " + type);
        }
        
    }
	
	public static Fort getTower() {
		return new Fort("Tower", 1, new Image("/images/things/fort/-n Tower -a 1.jpg"));
	}
	
	public int getCombatValue() {
		return combatValue;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setPlaced(boolean placed) {
		isPlaced = placed;
	}
	
	public boolean isPlaced() {
		return isPlaced;
	}
	
	public boolean isNeutralized() {
		return neutralized;
	}

	@Override
	public String toString() {
		return name + " " +  type.toString() + " " + combatValue;
	}
	
}
