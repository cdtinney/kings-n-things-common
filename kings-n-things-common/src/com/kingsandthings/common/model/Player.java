package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.RandomEvent;
import com.kingsandthings.common.model.things.SpecialCharacter;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Treasure;

public class Player {
	
	private static Logger LOGGER = Logger.getLogger(Player.class.getName());
	
	private String name;
	
	private transient Image controlMarker;
	private String controlMarkerURL;

	private transient List<Tile> controlledTiles;
	private int numControlledTiles = 0;
	
	private int numGold = 0;
	
	private Rack rack;
	
	private transient List<Fort> forts;
	private transient List<SpecialIncome> specialIncomeCounters;
	private transient List<SpecialCharacter> specialCharacters;
	private transient List<Treasure> treasures;
	private transient List<RandomEvent> randomEvents;
	
	private transient List<Creature> creatures;
	
	private boolean hasCitadel = false;
	
	public Player() { }
	
	public Player(String name) {
		this.name = name;
		
		rack = new Rack(this);
		
		controlledTiles = new ArrayList<Tile>();
		
		forts = new ArrayList<Fort>();
		specialIncomeCounters = new ArrayList<SpecialIncome>();
		specialCharacters = new ArrayList<SpecialCharacter>();
		treasures = new ArrayList<Treasure>();
		randomEvents = new ArrayList<RandomEvent>();
		
		creatures = new ArrayList<Creature>();
		
	}
	
	public String getName() {
		return name;
	}
	
	public Image getControlMarker() {
		
		if (controlMarker == null) {
			controlMarker = new Image(controlMarkerURL);
		}
		
		return controlMarker;
	}
	
	public Rack getRack() {
		return rack;
	}
	
	public List<Tile> getControlledTiles() {
		return controlledTiles;
	}
	
	public int getNumControlledTiles() {
		return numControlledTiles;
	}
	
	public List<Fort> getForts() {
		return forts;
	}
	
	public List<SpecialIncome> getSpecialIncomeCounters() {
		return specialIncomeCounters;
	}
	
	public List<SpecialCharacter> getSpecialCharacters() {
		return specialCharacters;
	}
	
	public List<Treasure> getTreasures() {
		return treasures;
	}
	
	public List<RandomEvent> getRandomEvents() {
		return randomEvents;
	}
	
	public List<Creature> getCreatures() {
		return creatures;
	}
	
	public int getNumGold() {
		return numGold;
	}
	
	public boolean hasCitadel() {
		return hasCitadel;
	}
	
	public void setControlMarker(Image controlMarker) {
		this.controlMarker = controlMarker;
	}
	
	public void setControlMarkerPath(String path) {
		this.controlMarkerURL = path;
	}
	
	public void setNumControlledTiles(int num) {
		numControlledTiles = num;
	}
	
	public boolean placeFort(Fort fort, Tile tile) {
		
		boolean success = tile.setFort(fort);
		if (success && forts.contains(fort)) {
			forts.get(forts.indexOf(fort)).setPlaced(true);
			
			if (fort.getType() == Fort.Type.CITADEL) {
				hasCitadel = true;
			}
			
		}

		PropertyChangeDispatcher.getInstance().notify(this, "forts", null, forts);
		return success;
		
	}
	
	public void addFort(Fort fort) {
		
		if (forts.contains(fort)) {
			LOGGER.warning("Fort already contained in player list");
			return;
		}

		List<Fort> oldForts = forts;
		forts.add(fort);
		PropertyChangeDispatcher.getInstance().notify(this, "forts", oldForts, forts);
		
	}
	
	public void removeFort(Fort fort) {
		
		if (!forts.contains(fort)) {
			LOGGER.warning("Fort not contained in player list.");
			return;
		}
		
		List<Fort> oldForts = forts;
		forts.remove(fort);
		PropertyChangeDispatcher.getInstance().notify(this, "forts", oldForts, forts);
		
	}

	public boolean placeSpecialIncome(SpecialIncome specialIncome, Tile tile) {
		
		boolean success = tile.setSpecialIncome(specialIncome);
		if (success) {
			
			// Remove the counter from the rack and add to the list of placed counters
			rack.removeThing(specialIncome);
			
			specialIncomeCounters.add(specialIncome);
			specialIncome.setPlaced(true);
			
		}
		
		return success;
		
	}
	
	public void addGold(int num) {
		PropertyChangeDispatcher.getInstance().notify(this, "numGold", numGold, numGold = (numGold + num));
	}
	
	public void removeGold(int num) {
		PropertyChangeDispatcher.getInstance().notify(this, "numGold", numGold, numGold = (numGold - num));
	}
	
	@Override
	public boolean equals(Object that) {
		
		if (this == that) {
			return true;
		}
		
		if (!(that instanceof Player)) {
			return false;
		}
		
		return ((Player) that).name.equals(this.name);
		
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
