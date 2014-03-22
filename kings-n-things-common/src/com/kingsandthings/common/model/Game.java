package com.kingsandthings.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Thing;

public class Game {

	private static Logger LOGGER = Logger.getLogger(Game.class.getName());

	private PlayerManager playerManager;
	private PhaseManager phaseManager;
	
	private final int NUM_INITIAL_THINGS = 10;
	
	private Cup cup;
	private Board board;
	
	public Game() {
		cup = new Cup();
		board = new Board(this);

		playerManager = new PlayerManager();
		phaseManager = new PhaseManager(this);
	}
	
	public void begin() {
		
		// Generate the board
		board.generateBoard(playerManager.getNumPlayers());
		
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
	
	public void addPlayers(List<String> playerNames) {
		playerManager.setNumPlayers(playerNames.size());
		playerManager.addAllPlayers(playerNames);
	}	
	
	public void addInitialThingsToPlayer(List<Thing> things, Player player) {

		boolean success = player.getRack().addThings(things);
		
		if (success) {
			cup.removeThings(things);
			
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

}
