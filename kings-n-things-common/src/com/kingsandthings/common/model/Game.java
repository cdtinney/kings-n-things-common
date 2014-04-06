package com.kingsandthings.common.model;

import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.phase.PhaseManager;

public class Game implements IGame {

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
		} else {
			
			for (Player player : playerManager.getPlayers()) {
				int pos = playerManager.getPosition(player);
				board.setStartingTile(player, pos);
			}
			
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
	public void recruitThings(int numPaidRecruits) {
		
		// TASK - paid Things / non-hardcoded recruitment

		final Player player = getActivePlayer();
		cup.recruitHardcodedThings(player, playerManager.getPosition(player));
		phaseManager.endPlayerTurn();		
		
	}

	@Override
	public void endTurn(String playerName) {
		
		if (!getActivePlayer().getName().equals(playerName)) {
			return;
		}
		
		// TODO - only certain phases can be ended w/o interaction
		phaseManager.endPlayerTurn();
		
	}

	@Override
	public void resolveCombat(Tile tile) {
		
		CombatPhase combatPhase = (CombatPhase) phaseManager.getCurrentPhase();
		if (combatPhase == null) {
			LOGGER.warning("Can only resolve combat during combat phase.");
			return;
		}
		
		Tile modelTile = board.getTile(tile);
		combatPhase.setCurrentBattle(modelTile);
		
	}

	@Override
	public void rollCombatDice(String playerName) {

		CombatPhase combatPhase = (CombatPhase) phaseManager.getCurrentPhase();
		if (combatPhase == null) {
			LOGGER.warning("Can only resolve roll dice during combat phase.");
			return;
		}
		
		combatPhase.rollForHits(playerName);
		
	}

}
