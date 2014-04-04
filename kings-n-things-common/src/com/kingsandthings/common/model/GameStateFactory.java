package com.kingsandthings.common.model;

import java.util.List;

import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.FortFactory;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.common.model.things.Fort.Type;

public class GameStateFactory {
	
	public static void setGameState(Game game, int state) {
		
		if (state == 1) {
			setMinimumGameState(game);
		}
		
	}
	
	private static void setMinimumGameState(Game game) {
		
		PhaseManager phaseManager = game.getPhaseManager();
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();
		Cup cup = game.getCup();

		// Skip initial game phases
		phaseManager.skipInitialPhases();
		
		// Give players 10 gold
		for (Player p : playerManager.getPlayers()) {
			p.addGold(10);
		}
		
		// Set control markers
		setControlMarkers(game);
		
		// Set forts
		setForts(game);

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
	
	private static void setControlMarkers(Game game) {
		
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();

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
	
	private static void setForts(Game game) {
		
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();
		
		Player p1 = playerManager.getPlayerInPosition(1);
		setFort(p1, board.getTiles()[3][3], FortFactory.getFort(Type.KEEP, true, true));
		setFort(p1, board.getTiles()[1][4], FortFactory.getFort(Type.CASTLE, true, false));
		setFort(p1, board.getTiles()[2][4], FortFactory.getFort(Type.TOWER, true, true));
		setFort(p1, board.getTiles()[1][5], FortFactory.getFort(Type.TOWER, true, false));
		setFort(p1, board.getTiles()[0][6], FortFactory.getFort(Type.CASTLE, true, true));
		
		Player p2 = playerManager.getPlayerInPosition(2);
		setFort(p2, board.getTiles()[3][4], FortFactory.getFort(Type.TOWER, true, false));
		setFort(p2, board.getTiles()[4][4], FortFactory.getFort(Type.CASTLE, true, false));
		setFort(p2, board.getTiles()[2][5], FortFactory.getFort(Type.KEEP, true, true));
		setFort(p2, board.getTiles()[3][5], FortFactory.getFort(Type.KEEP, true, true));
		setFort(p2, board.getTiles()[1][6], FortFactory.getFort(Type.KEEP, true, true));
		
		Player p3 = playerManager.getPlayerInPosition(3);
		if (p3 != null) {
			setFort(p3, board.getTiles()[5][2], FortFactory.getFort(Type.KEEP, true, false));
		}
		
		Player p4 = playerManager.getPlayerInPosition(4);
		if (p4 != null) {
			setFort(p4, board.getTiles()[0][0], FortFactory.getFort(Type.KEEP, true, false));
			setFort(p4, board.getTiles()[0][1], FortFactory.getFort(Type.CASTLE, true, true));
			setFort(p4, board.getTiles()[1][1], FortFactory.getFort(Type.TOWER, true, true));
		}
		
	}
	
	private static void setFort(Player player, Tile tile, Fort fort) {
		
		player.addFort(fort);
		tile.setFort(fort);
		
	}
	

}
