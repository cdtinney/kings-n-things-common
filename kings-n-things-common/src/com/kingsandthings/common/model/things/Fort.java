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

	private boolean neutralised = false;
	private boolean isPlaced = false;
	
	private boolean upgraded = false;
	
	public Fort() { }

	public Fort(String type, int combatValue, Image image) {
        super(type, image);
        
        this.combatValue = combatValue;
        
        try {
        	this.type = Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
        	LOGGER.warning("Invalid fort type - " + type);
        }
        
    }
	
	public Fort(Type type, int combatValue, Image image) {
		super(type.toString(), image);
        
        this.combatValue = combatValue;
        this.type = type;
		
	}
	
	public int getCombatValue() {
		return combatValue;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean getUpgraded() {
		return upgraded;
	}
	
	public boolean getPlaced() {
		return isPlaced;
	}
	
	public boolean getNeutralised() {
		return neutralised;
	}
	
	public void setPlaced(boolean placed) {
		isPlaced = placed;
	}
	
	public void setNeutralised(boolean neutralised) {
		this.neutralised = neutralised;
	}
	
	public void setUpgraded(boolean upgraded) {
		this.upgraded = upgraded;
	}

	@Override
	public String toString() {
		return name + " " +  type.toString() + " " + combatValue;
	}
	
}
