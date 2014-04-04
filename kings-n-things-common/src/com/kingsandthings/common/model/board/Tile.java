package com.kingsandthings.common.model.board;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;

public class Tile {
	
	private static Logger LOGGER = Logger.getLogger(Tile.class.getName());

	public static final int MAXIMUM_THINGS = 16;
	
	private static final Image defaultImg = new Image("/images/tiles/back.png");
	private transient Image image;
	private String imagePath;
	
	private int id;
	
	private transient List<Tile> neighbours;

	private Player owner;
	private Map<Player, List<Thing>> things;
	private Terrain terrainType;
	private SpecialIncome specialIncome;
	
	private Fort fort;
	
	private boolean discovered = false;
	private boolean battleToResolve = false;
	
	public Tile() { }
	
	public Tile(Terrain type) {
		
		this.terrainType = type;
		
		neighbours = new ArrayList<Tile>();
		things = new HashMap<Player, List<Thing>>();

		imagePath ="/images/tiles/" + type.toString().toLowerCase() + ".png";
		image = new Image(imagePath);
    	
    	id = System.identityHashCode(this);
		
	}
	
	public int getId() {
		return id;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public Terrain getTerrainType() {
		return terrainType;
	}
	
	public Image getImage() {
		
		if (!discovered) {
			return defaultImg;
		}
		
		if (image == null) {
			image = new Image(imagePath);
		}
		
		return image;		
	}
	
	public List<Tile> getNeighbours() {
		return neighbours;
	}
	
	public int getMovementCost() {
		EnumSet<Terrain> doubleCost = EnumSet.of(Terrain.SWAMP, Terrain.MOUNTAIN, Terrain.FOREST, Terrain.JUNGLE);
		return doubleCost.contains(terrainType) ? 2 : 1;
	}
	
	public Fort getFort() {
		return fort;
	}
	
	public SpecialIncome getSpecialIncome() {
		return specialIncome;
	}
	
	public Map<Player, List<Thing>> getThings() {
		return things;
	}
	
	public boolean isDiscovered() {
		return discovered;
	}
	
	public boolean hasThings() {
		
		for (List<Thing> playerThings : things.values()) {
			if (!playerThings.isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasBattleToResolve() {
		return battleToResolve;
	}
	
	public void setOwner(Player player) {
		
		player.setNumControlledTiles(player.getNumControlledTiles() + 1);
		discovered = true;
		
		PropertyChangeDispatcher.getInstance().notify(this, "owner", owner, owner = player);
		
	}
	
	public boolean setFort(Fort fort) {
		PropertyChangeDispatcher.getInstance().notify(this, "fort", this.fort, this.fort = fort);
		return true;
	}
	
	public boolean setSpecialIncome(SpecialIncome specialIncome) {
		
		if (this.specialIncome != null) {
			LOGGER.log(LogLevel.STATUS, "This tile already contains a special income counter.");
			return false;
		}
		
		PropertyChangeDispatcher.getInstance().notify(this, "specialIncome", this.specialIncome, this.specialIncome = specialIncome);
		return true;
		
	}
	
	public void setNeighbours(List<Tile> neighbours) {
		this.neighbours = neighbours;
	}
	
	public void setBattleToResolve(boolean battleToResolve) {
		PropertyChangeDispatcher.getInstance().notify(this, "battleToResolve", this.battleToResolve, this.battleToResolve = battleToResolve);
	}
	
	public boolean addThings(Player player, List<Thing> list) {
		
		if (things.get(player) == null) {
			things.put(player, new ArrayList<Thing>());
		}
		
		List<Thing> oldPlayerThings = new ArrayList<Thing>(things.get(player));
		
		if (thingsContained(oldPlayerThings, list)) {
			LOGGER.warning("One or more of the specified Things have already been added to this Tile.");
			return false;
		}
		
		things.get(player).addAll(list);
		PropertyChangeDispatcher.getInstance().notify(this, "things", oldPlayerThings, things.get(player));
		return true;
	}
	
	public boolean removeThings(Player player, List<Thing> thingsToRemove) {
		
		for (Thing thing : thingsToRemove) {
			if (!removeThing(player, thing)) {
				return false;
			}
		}
		
		List<Thing> oldPlayerThings = new ArrayList<Thing>(things.get(player));
		
		// Remove the player from the map if they have no Things on the tile
		if (oldPlayerThings.isEmpty()) {
			things.remove(player);
		}

		PropertyChangeDispatcher.getInstance().notify(this, "things", oldPlayerThings, things.get(player));
		return true;
		
	}
	
	public boolean removeThing(Player player, Thing thing) {
		
		List<Thing> playerThings = things.get(player);
		
		if (playerThings == null) {
			LOGGER.warning("Player does not own any Things on this tile.");
			return false;
		}
		
		if (!playerThings.contains(thing)) {
			LOGGER.warning("The specified Thing is not placed on this tile.");
			return false;
		}
		
		return playerThings.remove(thing);
		
	}
    
    @Override
    public boolean equals(Object that) {
    	
    	if (this == that) {
    		return true;
    	}
    	
    	if (!(that instanceof Tile)) {
    		return false;
    	}
    	
    	Tile tile = (Tile) that;
    	return tile.id == this.id;   
    	
    }
	
	private boolean thingsContained(List<Thing> playerThings, List<Thing> list) {
		
		for (Thing thing : list) {
			if (playerThings.contains(thing)) {
				return true;
			}
		}
		
		return false;
	}
	
}
