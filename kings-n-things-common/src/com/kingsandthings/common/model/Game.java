package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Board.TileLocation;
import com.kingsandthings.common.model.board.IBoard;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.Thing;

public class Game implements IGame {

	private static Logger LOGGER = Logger.getLogger(Game.class.getName());
	
	private final int NUM_INITIAL_THINGS = 10;

	private transient Cup cup;
	private PhaseManager phaseManager;
	
	private PlayerManager playerManager;
	private Board board;
	
	public Game() { 
		playerManager = new PlayerManager();
	}
	
	public void initalize() {
		
		cup = new Cup();
		cup.importThings();
		
		board = new Board(this);
		board.generateBoard(playerManager.getNumPlayers());
		
		phaseManager = new PhaseManager(this);
		
	}
	
	public void start() {
		
		// Set starting tiles
		for (Player player : playerManager.getPlayers()) {
			int pos = playerManager.getPosition(player);
			board.setStartingTile(player, pos);
		}
		
		// Set the first player to active
		playerManager.setFirstPlayerActive();
		
		// Begin the phases
		phaseManager.beginPhases();
		
	}
	
	public Player getActivePlayer() {
		return playerManager.getActivePlayer();
	}
	
	public PhaseManager getPhaseManager() {
		return phaseManager;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Cup getCup() {
		return cup;
	}
	
	public void setNumPlayers(int num) {
		playerManager.setNumPlayers(num);
	}
	
	public void addPlayer(String name) {
		playerManager.addPlayer(name);
	}
	
	public void removePlayer(String name) {
		playerManager.removePlayer(name);
	}
	
	public void addInitialThingsToPlayer(List<Thing> things, Player player) {

		boolean success = player.getRack().addThings(things);
		
		if (success) {
			cup.removeThingsFromCup(things);
			
			if (player.getRack().getThings().size() == NUM_INITIAL_THINGS) {
				phaseManager.endPlayerTurn();
			}
			
		}
		
	}
	
	public void addThingIndicesToPlayer(List<Integer> indices, Player player) {
		
		if (player == null) {
			LOGGER.warning("Cannot add Things to null player (is there an active player?)");
			return;
		}
		
		List<Thing> things = new ArrayList<Thing>();

		for (Integer i : indices) {
			things.add(cup.getThings().get(i));
		}
		
		addInitialThingsToPlayer(things, player);
		
	}
	
	public static List<Class<?>> getMemberClasses() {
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		classes.add(IGame.class);
		classes.add(Game.class);
		
		classes.add(PlayerManager.class);
		classes.add(Player.class);
		classes.add(Rack.class);
		
		classes.add(PhaseManager.class);
		
		classes.add(Cup.class);
		classes.add(Creature.class);
		classes.add(Ability.class);
		classes.add(Terrain.class);

		classes.add(IBoard.class);
		classes.add(Board.class);
		
		classes.add(Tile.class);
		classes.add(Tile[][].class);
		classes.add(Tile[].class);
		classes.add(TileLocation.class);
		
		classes.add(Fort.class);
		classes.add(Fort.Type.class);
	    
		// JDK classes
		classes.add(ArrayList.class);
		classes.add(HashMap.class);
		classes.add(LinkedHashMap.class);
	    
		// Java FX
		classes.add(Image.class);
		
		return classes;
		
	}

	@Override
	public void recruitThingsInitial() {
		
		final Player player = getActivePlayer();
		cup.recruitThingsInitial(player, playerManager.getPosition(player));
		
	}

}
