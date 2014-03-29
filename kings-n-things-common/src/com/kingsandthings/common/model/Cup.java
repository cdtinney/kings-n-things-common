package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.model.things.ThingImport;
import com.kingsandthings.logging.LogLevel;

public class Cup {

	private static Logger LOGGER = Logger.getLogger(Cup.class.getName());
	
	private List<Thing> things = new ArrayList<Thing>();
	
	public Cup() { }
	
	public void importThings() {
		
		// TODO - Import all Things
		addThings(ThingImport.importCreatures());
		
	}
	
	public boolean recruitThings(Player player, int numPaid) {
		
		int goldRequired = numPaid * 5;
		if (player.getNumGold() < goldRequired) {
			LOGGER.log(LogLevel.STATUS, goldRequired + " gold is required to buy " + numPaid + " recruits.");
			return false;
		}
		
		int numFree = player.getNumControlledTiles() * 2;
		
		List<Thing> drawn = drawThings(numFree + numPaid);
		
		boolean success = player.getRack().addThings(drawn);
		
		if (success) {
			LOGGER.log(LogLevel.STATUS, player.getName() + " received: " + numFree  + " free, " + numPaid + " paid.");
			
			// Remove the Things from the Cup
			removeThingsFromCup(drawn);
			
			// Take away player's gold
			player.removeGold(goldRequired);
			
		} else {
			
			int available = Rack.MAX_THINGS - player.getRack().getThings().size();
			drawn = drawn.subList(0, available);
			
			if (numFree >= available) {
				String ignored = " (" + (numFree - available) + " free, " + numPaid + " paid ignored due to rack limit).";
				LOGGER.log(LogLevel.STATUS, player.getName() + " received " + available + " free recruits " + ignored);
				
				player.getRack().addThings(drawn);
				removeThingsFromCup(drawn);
				
				return true;
				
			} else {
				String message = player.getName() + " rack cannot hold " + numFree + " free, " + numPaid + " paid recruits.";
				message += "Please select a lower number of paid recruits.";
				LOGGER.log(LogLevel.STATUS, message);
				
				return false;
					
			}
			
		}
		
		return success;
		
	}
	
	public boolean recruitThingsInitial(Player player, int pos) {
		
		List<Thing> things = null;
		if (pos == 1) {
			things = getPlayerOneStack();
		} else if (pos == 2) {
			things = getPlayerTwoStack();
		}
		
		if (things != null && player.getRack().addThings(things)) {
			removeThingsFromCup(things);
			return true;
		}
		
		return false;
		
	}
	
	public List<Thing> drawThings(int num) {
		
		List<Thing> copy = new ArrayList<Thing>(things);
		Collections.shuffle(copy);
		return copy.subList(0, num);
		
	}
	
	public List<Thing> getThings() {
		return things;
	}
	
	public void removeThingsFromCup(List<Thing> list) {
		things.removeAll(list);
	}
	
	private void addThings(List<? extends Thing> list) {
		things.addAll(list);
	}
	
	private Thing getCreatureThing(String name, Terrain terrain, int combatValue) {
		
		for (Thing thing : things) {
			
			if (!(thing instanceof Creature)) {
				continue;
			}
			
			Creature creature = (Creature) thing;
			
			if (creature.getName().equals(name) && creature.getTerrainType().equals(terrain) && 
					creature.getCombatValue() == combatValue) {
				return thing;
			}
			
		}
		
		return null;
		
	}
	
	private List<Thing> getPlayerOneStack() {

		List<Thing> things = new ArrayList<Thing>();
		
		things.add(getCreatureThing("Crocodiles", Terrain.SWAMP, 2));
		things.add(getCreatureThing("Mountain Men", Terrain.MOUNTAIN, 1));
		things.add(getCreatureThing("Giant Lizard", Terrain.SWAMP, 2));
		things.add(getCreatureThing("Swamp Beast", Terrain.SWAMP, 3));
		things.add(getCreatureThing("Killer Racoon", Terrain.FOREST, 2));
		things.add(getCreatureThing("Farmers", Terrain.PLAINS, 1));
		things.add(getCreatureThing("Wild Cat", Terrain.FOREST, 2));
		
		if (things.contains(null)) {
			LOGGER.warning("Error creating stack 1 for Player 1.");
			return null;
		}
		
		return things;
		
	}
	
	private List<Thing> getPlayerTwoStack() {

		List<Thing> things = new ArrayList<Thing>();
		
		things.add(getCreatureThing("Thing", Terrain.SWAMP, 2));
		things.add(getCreatureThing("Giant Lizard", Terrain.SWAMP, 2));
		things.add(getCreatureThing("Swamp Rat", Terrain.SWAMP, 1));
		things.add(getCreatureThing("Unicorn", Terrain.FOREST, 4));
		things.add(getCreatureThing("Bears", Terrain.FOREST, 2));
		things.add(getCreatureThing("Giant Spider", Terrain.DESERT, 1));
		things.add(getCreatureThing("Camel Corps", Terrain.DESERT, 3));
		things.add(getCreatureThing("Sandworm", Terrain.DESERT, 3));
		
		if (things.contains(null)) {
			LOGGER.warning("Error creating stack 2 for Player 2.");
			return null;
		}
		
		return things;
		
	}

}
