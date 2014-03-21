package com.kingsandthings.common.model.things;

import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.model.enums.Terrain;

@SuppressWarnings("serial")
public class SpecialIncome extends Thing {
	
	private static Logger LOGGER = Logger.getLogger(SpecialIncome.class.getName());
	
	private Terrain terrainType;
	private int goldValue;
	
	private boolean placedOnBoard = false;

	public SpecialIncome(String name, int value, String terrainType, Image image) {
		super(name, image);
		
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
	
	public boolean placedOnBoard() {
		return placedOnBoard;
	}
	
}
