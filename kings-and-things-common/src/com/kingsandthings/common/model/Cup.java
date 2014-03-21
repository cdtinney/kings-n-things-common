package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.model.things.ThingImport;
import com.kingsandthings.logging.LogLevel;

public class Cup {

	private static Logger LOGGER = Logger.getLogger(Cup.class.getName());
	
	private List<Thing> things;
	
	private boolean playerOneDemoRecruit = false;
	
	public Cup() {
		things = new ArrayList<Thing>();
		
		// TODO - Import all Things
		addThings(ThingImport.importCreatures());
	}
	
	public boolean recruitThings(Player player, int numPaid) {
		
		// TASK - Demo only (Hardcoded recruitment for player 1)
		if (player.getName().equals("Player 1") && !playerOneDemoRecruit) {
			
			List<Thing> things = new ArrayList<Thing>();
			
			String path = "images/things/defenders/mountain";
			things.add(new Creature("Cyclops", "MOUNTAIN", 5, new Image(path + "/-n Cyclops -t Mountain -a 5.jpg")));
			things.add(new Creature("Mountain Men", "MOUNTAIN", 1, new Image(path + "/-n Mountain_Men -c 2 -t Mountain -a 1.jpg")));
			things.add(new Creature("Goblins", "MOUNTAIN", 1, new Image(path + "/-n Goblins -c 4 -t Mountain -a 1.jpg")));
			
			player.getRack().addThings(things);
			
			if (things.contains(null)) {
				LOGGER.warning("Error when creating hardcoded Things for Player 1 recruitment");
			}
			
			removeThings(things);
			
			playerOneDemoRecruit = true;
			
			return true;
			
		}
		
		int goldRequired = numPaid * 5;
		
		if (player.getNumGold() < goldRequired) {
			LOGGER.log(LogLevel.STATUS, goldRequired + " gold is required to buy " + numPaid + " recruits.");
			return false;
		}
		
		int numFree = numFreeRecruits(player);
		
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
	
	public List<Thing> drawThings(int num) {
		
		List<Thing> copy = new ArrayList<Thing>(things);
		Collections.shuffle(copy);
		return copy.subList(0, num);
		
	}
	
	public Thing drawThing() {
		Random r = new Random();
		return things.remove(r.nextInt(things.size()));
	}
	
	public List<Thing> getThings() {
		return things;
	}
	
	public List<String> getThingNames() {
		
		List<String> names = new ArrayList<String>();
		
		for (Thing thing : things) {
			names.add(thing.toString());
		}
		
		return names;
		
	}
	
	public void removeThings(List<Thing> list) {
		things.removeAll(list);
	}
	
	private int numFreeRecruits(Player player) {
		return player.getControlledTiles().size() * 2;
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

}
