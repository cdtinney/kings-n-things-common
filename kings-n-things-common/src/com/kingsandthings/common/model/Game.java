package com.kingsandthings.common.model;

import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Thing;
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
	
	public void initalize(int state) {
		
		cup = new Cup();
		cup.importThings();
		
		board = new Board(this);
		board.generateBoard(playerManager.getNumPlayers());
		
		phaseManager = new PhaseManager(this);
		
		if (state == 1) {
			setMinimumGameState();
		}
		
	}
	
	public void start() {
		
		// Set the first player to active
		playerManager.setFirstPlayerActive();
		
		// Begin the phases
		phaseManager.beginPhases();
		
	}
	
	public void setMinimumGameState() {

		// Skip initial game phases
		phaseManager.skipInitialPhases();
		
		// Give players 10 gold
		for (Player p : playerManager.getPlayers()) {
			p.addGold(10);
		}
		
		// Set control markers
		setPredefinedControlMarkers();

		// Player 1 Stack 1
		Player p1 = playerManager.getPlayerInPosition(1);
		List<Thing> p1Things = cup.getPlayer1Stack1Min();
		board.getTiles()[2][4].addThings(p1, p1Things);
		cup.removeThingsFromCup(p1Things);
		
		// Player 2 Stack 2
		Player p2 = playerManager.getPlayerInPosition(2);
		List<Thing> p2Things = cup.getPlayer2Stack2Min();
		board.getTiles()[3][4].addThings(p2, p2Things);
		cup.removeThingsFromCup(p2Things);
		
	}
	
	public void setPredefinedControlMarkers() {

		Player p1 = playerManager.getPlayerInPosition(1);
		board.getTiles()[1][3].setOwner(p1);
		board.getTiles()[3][3].setOwner(p1);
		board.getTiles()[0][4].setOwner(p1);
		board.getTiles()[1][4].setOwner(p1);
		board.getTiles()[2][4].setOwner(p1);
		board.getTiles()[0][5].setOwner(p1);
		board.getTiles()[1][5].setOwner(p1);
		board.getTiles()[0][6].setOwner(p1);
		
		Player p2 = playerManager.getPlayerInPosition(2);
		board.getTiles()[4][3].setOwner(p2);
		board.getTiles()[3][4].setOwner(p2);
		board.getTiles()[4][4].setOwner(p2);
		board.getTiles()[5][4].setOwner(p2);
		board.getTiles()[2][5].setOwner(p2);
		board.getTiles()[3][5].setOwner(p2);
		board.getTiles()[4][5].setOwner(p2);
		board.getTiles()[1][6].setOwner(p2);
		board.getTiles()[2][6].setOwner(p2);
		board.getTiles()[3][6].setOwner(p2);
		
		Player p3 = playerManager.getPlayerInPosition(3);
		if (p3 != null) {
			board.getTiles()[4][1].setOwner(p3);
			board.getTiles()[3][2].setOwner(p3);
			board.getTiles()[4][2].setOwner(p3);
			board.getTiles()[5][2].setOwner(p3);
			board.getTiles()[5][3].setOwner(p3);
			board.getTiles()[6][3].setOwner(p3);
		}
		
		Player p4 = playerManager.getPlayerInPosition(4);
		if (p4 != null) {
			board.getTiles()[0][0].setOwner(p4);
			board.getTiles()[1][0].setOwner(p4);
			board.getTiles()[2][0].setOwner(p4);
			board.getTiles()[0][1].setOwner(p4);
			board.getTiles()[1][1].setOwner(p4);
			board.getTiles()[0][2].setOwner(p4);
			board.getTiles()[1][2].setOwner(p4);
		}
		
		
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
