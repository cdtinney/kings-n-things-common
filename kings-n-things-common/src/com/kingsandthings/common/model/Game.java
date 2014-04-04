package com.kingsandthings.common.model;

import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.Fort.Type;
import com.kingsandthings.common.model.things.FortFactory;
import com.kingsandthings.common.model.things.Thing;

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
	
	public void initalize(int state) {
		
		cup = new Cup();
		cup.importThings();
		
		board = new Board(this);
		board.generateBoard(playerManager.getNumPlayers());
		
		phaseManager = new PhaseManager(this);
		
		if (state == 1) {
			GameStateFactory.setGameState(this, 1);
		}
		
	}
	
	public void start() {
		
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
