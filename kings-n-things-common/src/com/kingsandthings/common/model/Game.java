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
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.game.events.PropertyChangeDispatcher;

public class Game implements IGame {

	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(Game.class.getName());

	private transient Cup cup;
	private PhaseManager phaseManager;
	
	private PlayerManager playerManager;
	private Board board;
	
	private String instruction;
	
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
	
	public void setInstruction(String instruction) {
		PropertyChangeDispatcher.getInstance().notifyListeners(this, "instruction", this.instruction, this.instruction = instruction);
	}
	
	public String getInstruction() {
		return instruction;
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
	
	@Override
	public Player getActivePlayer() {
		return playerManager.getActivePlayer();
	}

	@Override
	public void recruitThingsInitial() {
		
		final Player player = getActivePlayer();
		boolean result = cup.recruitThingsInitial(player, playerManager.getPosition(player));
		if (result) {
			phaseManager.endPlayerTurn();
		}
		
	}

	@Override
	public void endTurn(String playerName) {
		
		if (!getActivePlayer().getName().equals(playerName)) {
			return;
		}
		
		// TODO - only certain phases can be ended w/o interaction
		phaseManager.endPlayerTurn();
		
	}

}
