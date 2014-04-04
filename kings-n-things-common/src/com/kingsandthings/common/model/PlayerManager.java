package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.events.PropertyChangeDispatcher;

public class PlayerManager {
	
	private static Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
	
	private Integer numPlayers;
	
	private Map<String, Player> players;
	private Map<Player, Integer> positions;
	
	private Player activePlayer;
	
	public PlayerManager() {
		players = new LinkedHashMap<String, Player>();
		positions = new HashMap<Player, Integer>();
	}
	
	public List<Player> getPlayers() {
		return new ArrayList<Player>(players.values());
	}
	
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	public Player getPlayer(String name) {
		return players.get(name);
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public int getPosition(Player player) { 
		
		if (positions.containsKey(player)) {
			return positions.get(player);
		}
		
		return 0;
		
	}
	
	public Player getPlayerInPosition(int pos) {
		
		if (pos > numPlayers) {
			return null;
		}
		
		for (Player player: players.values()) {
			
			if (getPosition(player) == pos) {
				return player;
			}
			
		}
		
		return null;
		
	}
	
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = (this.numPlayers == null) ? numPlayers : this.numPlayers;
	}
	
	public void setActivePlayer(Player player) {
		PropertyChangeDispatcher.getInstance().notify(this, "activePlayer", activePlayer, activePlayer = player);
	}
	
	public void setFirstPlayerActive() {
		
		for (Player player: getPlayers()) {
			
			if (getPosition(player) == 1) {
				setActivePlayer(player);
			}
			
		}
		
	}
	
	public void setNextPlayerActive() {
		
		int activePosition = positions.get(activePlayer);
		
		for (Player player : positions.keySet()) {
			
			int position = positions.get(player);
			if (position == activePosition + 1 || (activePosition == numPlayers && position == 1)) {
				setActivePlayer(player);
				return;
			}
			
		}
		
	}
	
	public boolean addAllPlayers(List<String> names) {
		
		boolean modified = false;
		
		for (String name : names) {
			modified = addPlayer(name) == true ? true : modified;
		}
		
		return modified;
	}

	public boolean addPlayer(String name) {
		
		if (name == null || name.trim().length() == 0) {
			LOGGER.warning("Cannot add player with with null or empty name.");
			return false;
		}
		
		if (numPlayers == null || numPlayers == 0) {
			LOGGER.warning("Cannot add players before maximum number of players is set.");
			return false;
		}

		if (players.size() > numPlayers) {
			LOGGER.warning("Cannot add additional players - maximum number of players reached.");
			return false;
		}
		
		if (players.containsKey(name)) {
			LOGGER.warning("Player '" + name + "' has already been added.");
			return false;
		}
		
		Player player = new Player(name);
		players.put(name, player);

		// TODO - set control marker images elsewhere (in Game, probably)
		setControlMarkerImage(player, players.size());
		
		setInitialPosition(player, players.size());
		
		return true;
	}
	
	public boolean removePlayer(String name) {
		return players.remove(name) != null;
	}
	
	private void setControlMarkerImage(Player player, int position) {
		String path = "/images/other/control_marker_" + position + ".png";
		player.setControlMarker(new Image(path));
		player.setControlMarkerPath(path);
	}
	
	private boolean setInitialPosition(Player player, int position) {
		
		if (players.containsKey(player.getName())) {
			
			if (positions.containsKey(player)) {
				LOGGER.warning("Player is already assigned an initial position.");
				return false;
			}
			
			if (positions.containsValue(position)) {
				LOGGER.warning("Initial position " + position + " already assigned to player.");
				return false;
			}
			
			positions.put(player, position);
			
		}
		
		return false;		
	}

}
