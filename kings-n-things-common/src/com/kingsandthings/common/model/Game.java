package com.kingsandthings.common.model;

import java.util.Map;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.phase.CombatPhase;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.model.things.Treasure;

public class Game implements IGame {

	private static Logger LOGGER = Logger.getLogger(Game.class.getName());

	private transient Cup cup;
	private PhaseManager phaseManager;
	
	private PlayerManager playerManager;
	private Board board;
	
	private String instruction;
	
	private transient Player winner;
	
	public Game() { 
		playerManager = new PlayerManager();
	}
	
	public void initalize(String state) {
		
		cup = new Cup();
		cup.importThings();
		
		board = new Board(this);
		board.generateBoard(playerManager.getNumPlayers());
		
		phaseManager = new PhaseManager(this);
		
		switch(state) {
		
			case "Minimal":
				GameStateFactory.setGameState(this, state);
				break;
			default:
				GameStateFactory.setGameState(this, state);
				break;
		
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
	
	public void setWinner(Player player) {
		PropertyChangeDispatcher.getInstance().notifyListeners(this, "winner", this.winner, this.winner = player);
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

		final Player player = getActivePlayer();
		boolean result = cup.recruitThings(player, numPaidRecruits);
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
			LOGGER.warning("Can only roll dice during combat phase.");
			return;
		}
		
		combatPhase.rollForHits(playerName);
		
	}

	@Override
	public void applyHits(String playerName, Map<Thing, Integer> hits) {
		
		CombatPhase combatPhase = (CombatPhase) phaseManager.getCurrentPhase();
		if (combatPhase == null) {
			LOGGER.warning("Can only apply hits during combat phase.");
			return;
		}
		
		combatPhase.applyHits(playerName, hits);
		
	}

	@Override
	public boolean redeemTreasure(String playerName, Treasure treasure) {
		
		Player p = playerManager.getPlayer(playerName);
		if (p == null) {
			return false;
		}
		
		p.addGold(treasure.getGoldValue());
		p.getRack().removeThing(treasure);
		
		cup.returnThing(treasure);
		
		return true;

	}

}
