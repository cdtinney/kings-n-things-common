package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.things.Thing;

public class Rack {
	
	public static final int MAX_THINGS = 10;
	
	private static Logger LOGGER = Logger.getLogger(Rack.class.getName());
	
	private ArrayList<Thing> things;
	
	private Player owner;
	
	public Rack() { }
	
	public Rack(Player owner) {
		this.owner = owner;
		
		things = new ArrayList<Thing>();
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public ArrayList<Thing> getThings() {
		return things;
	}
	
	public boolean addThings(List<Thing> list) {
		
		if (things.size() + list.size() > MAX_THINGS) {
			LOGGER.log(LogLevel.STATUS, "Cannot add " + list.size() + " Things due to limit (current: " + things.size() + " maximum: " + MAX_THINGS + ")");
			return false;
		}

		List<Thing> oldThings = new ArrayList<Thing>(things);
		
		things.addAll(list);
		PropertyChangeDispatcher.getInstance().notify(this, "things", oldThings, things);
		return true;
		
	}
	
	public boolean addThing(Thing thing) {
		
		if (things.size() == MAX_THINGS) {
			LOGGER.log(LogLevel.STATUS, "Cannot add Thing due to limit (current: " + things.size() + " maximum: " + MAX_THINGS + ")");
			return false;
		}

		List<Thing> oldThings = new ArrayList<Thing>(things);
		
		things.add(thing);
		PropertyChangeDispatcher.getInstance().notify(this, "things", oldThings, things);
		return true;
	}

	public void removeThing(Thing thing) {
		
		List<Thing> oldThings = new ArrayList<Thing>(things);
		
		things.remove(thing);
		PropertyChangeDispatcher.getInstance().notify(this, "things", oldThings, things);
	}
	
	public void removeThings(List<Thing> list) {
		
		List<Thing> oldThings = new ArrayList<Thing>(things);
		
		things.removeAll(list);
		PropertyChangeDispatcher.getInstance().notify(this, "things", oldThings, things);
		
	}

}
