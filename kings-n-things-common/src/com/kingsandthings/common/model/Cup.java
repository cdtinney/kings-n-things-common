package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.model.things.ThingImport;
import com.kingsandthings.common.model.things.Treasure;

public class Cup {

	private static Logger LOGGER = Logger.getLogger(Cup.class.getName());
	
	private static final int INITIAL_NUM_THINGS = 10;
	
	private List<Thing> things = new ArrayList<Thing>();
	
	public Cup() { }
	
	public void importThings() {
		
		// TODO - Import all Things
		things.addAll(ThingImport.importCreatures());
		things.addAll(ThingImport.importSpecialIncomeCounters());
		things.addAll(ThingImport.importTreasures());
		
	}
	
	public List<Thing> getThings() {
		return things;
	}
	
	public List<Thing> drawThings(int num) {
		
		List<Thing> copy = new ArrayList<Thing>(things);
		Collections.shuffle(copy);
		
		if (copy.size() < num ) {
			LOGGER.warning("Not enough things to draw: " + num + "(size = " + copy.size());
			return new ArrayList<Thing>();
		}
		
		return copy.subList(0, num);
		
	}
	
	public void returnThings(List<? extends Thing> things) {
		LOGGER.log(LogLevel.DEBUG, things.size() + " Things returned to Cup.");
		this.things.addAll(things);
	}
	
	public void returnThing(Thing thing) {
		LOGGER.log(LogLevel.DEBUG, "Thing - " + thing.toString() + " returned to Cup.");
		things.add(thing);
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
			removeThings(drawn);
			
			// Take away player's gold
			player.removeGold(goldRequired);
			
		} else {
			
			int available = Rack.MAX_THINGS - player.getRack().getThings().size();
			drawn = drawn.subList(0, available);
			
			if (numFree >= available) {
				String ignored = " (" + (numFree - available) + " free, " + numPaid + " paid ignored due to rack limit).";
				LOGGER.log(LogLevel.STATUS, player.getName() + " received " + available + " free recruits " + ignored);
				
				player.getRack().addThings(drawn);
				removeThings(drawn);
				
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
		
		List<Thing> recruitedThings = drawThings(INITIAL_NUM_THINGS);
		
		boolean result = player.getRack().addThings(recruitedThings);
		if (result) {
			removeThings(recruitedThings);
		}
		
		return result;
		
	}
	
	public void removeThings(List<Thing> list) {
		things.removeAll(list);
	}
	
	private Thing findCreature(String name, Terrain terrain, int combatValue) {
		
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
	
	private Thing findSpecialIncome(String name, Terrain terrain, int goldValue) {

		for (Thing thing : things) {
			
			if (!(thing instanceof SpecialIncome)) {
				continue;
			}
			
			SpecialIncome specialIncome = (SpecialIncome) thing;
			
			if (specialIncome.getName().equals(name) && specialIncome.getTerrainType() == terrain &&
					specialIncome.getGoldValue() == goldValue) {
				return thing;
			}
			
		}
		
		return null;
		
	}
	
	private Treasure findTreasure(String name, int goldValue) {

		for (Thing thing : things) {
			
			if (!(thing instanceof Treasure)) {
				continue;
			}
			
			Treasure treasure = (Treasure) thing;
			
			if (treasure.getName().equals(name) && treasure.getGoldValue() == goldValue) {
				return treasure;
			}
			
		}
		
		return null;
		
	}
	
	public List<Thing> getPlayer1Stack1Min() {

		List<Thing> things = new ArrayList<Thing>();
		
		things.add(findCreature("Crocodiles", Terrain.SWAMP, 2));
		things.add(findCreature("Mountain Men", Terrain.MOUNTAIN, 1));
		things.add(findCreature("Giant Lizard", Terrain.SWAMP, 2));
		things.add(findCreature("Swamp Beast", Terrain.SWAMP, 3));
		things.add(findCreature("Killer Racoon", Terrain.FOREST, 2));
		things.add(findCreature("Farmers", Terrain.PLAINS, 1));
		things.add(findCreature("Wild Cat", Terrain.FOREST, 2));
		
		if (things.contains(null)) {
			LOGGER.warning("Error creating stack 1 for Player 1.");
			return null;
		}
		
		return things;
		
	}
	
	public List<Thing> getPlayer2Stack2Min() {

		List<Thing> things = new ArrayList<Thing>();
		
		things.add(findCreature("Thing", Terrain.SWAMP, 2));
		things.add(findCreature("Giant Lizard", Terrain.SWAMP, 2));
		things.add(findCreature("Swamp Rat", Terrain.SWAMP, 1));
		things.add(findCreature("Unicorn", Terrain.FOREST, 4));
		things.add(findCreature("Bears", Terrain.FOREST, 2));
		things.add(findCreature("Giant Spider", Terrain.DESERT, 1));
		things.add(findCreature("Camel Corps", Terrain.DESERT, 3));
		things.add(findCreature("Sandworm", Terrain.DESERT, 3));
		
		if (things.contains(null)) {
			LOGGER.warning("Error creating stack 2 for Player 2.");
			return null;
		}
		
		return things;
		
	}
	
	public List<Thing> getRackThingsAverage(int pos) {

		List<Thing> things = new ArrayList<Thing>();
		
		if (pos == 1) {
			things.add(findSpecialIncome("Diamond Field", Terrain.DESERT, 1));
			things.add(findSpecialIncome("Peat Bog", Terrain.SWAMP, 1));
			
		} else if (pos == 2) {
			things.add(findSpecialIncome("Copper Mine", Terrain.MOUNTAIN, 1));
			things.add(findSpecialIncome("Gold Mine", Terrain.MOUNTAIN, 3));
			things.add(findTreasure("Diamond", 5));
		}
		
		return things;
		
	}

}
