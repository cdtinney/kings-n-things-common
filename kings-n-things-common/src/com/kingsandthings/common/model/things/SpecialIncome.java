package com.kingsandthings.common.model.things;

import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.model.enums.Terrain;

@SuppressWarnings("serial")
public class SpecialIncome extends Thing {
	
	public enum Type {
		CITY,
		VILLAGE,
		OTHER
	}
	
	private static Logger LOGGER = Logger.getLogger(SpecialIncome.class.getName());
	
	private Terrain terrainType;
	private int goldValue;
	private Type type;
	
	private boolean isPlaced = false;
	
	public SpecialIncome() { }

	public SpecialIncome(String name, int value, String terrainType, Type type, Image image) {
		super(name, image);
		
		this.type = type;
		
		goldValue = value;
		
		try {
			this.terrainType = Terrain.valueOf(terrainType.toUpperCase());
		} catch (IllegalArgumentException e) {
			LOGGER.warning(e.getMessage());
		}
		
	}
	
	public int getGoldValue() {
		return goldValue;
	}
	
	public Terrain getTerrainType() {
		return terrainType;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isPlaced() {
		return isPlaced;
	}
	
	public void setPlaced(boolean placed) {
		isPlaced = placed;
	}
	
}
