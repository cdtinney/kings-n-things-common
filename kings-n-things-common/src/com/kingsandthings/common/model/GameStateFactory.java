package com.kingsandthings.common.model;

import java.util.List;

import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.Fort.Type;
import com.kingsandthings.common.model.things.FortFactory;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;

public class GameStateFactory {
	
	public static void setGameState(Game game, String state) {
		
		if (state.equals("Minimal")) {
			setMinimumGameState(game);
		} else if (state.equals("Average")) {
			setAverageGameState(game);
		} else if (state.equals("None")) {
			// Do nothing
		}
		
	}
	
	private static void setAverageGameState(Game game) {

		PhaseManager phaseManager = game.getPhaseManager();
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();
		Cup cup = game.getCup();

		phaseManager.skipInitialPhases();
		setGold(game, 10);
		setControlMarkers(game);
		setForts(game, true);

		// Player 1 rack
		Player p1 = playerManager.getPlayerInPosition(1);
		List<Thing> rackThings1 = cup.getRackThingsAverage(1);
		p1.getRack().addThings(rackThings1);
		cup.removeThings(rackThings1);

		// Player 2 rack
		Player p2 = playerManager.getPlayerInPosition(2);
		List<Thing> rackThings2 = cup.getRackThingsAverage(2);
		p2.getRack().addThings(rackThings2);
		cup.removeThings(rackThings2);
		
		// Player 1 Stack 1
		List<Thing> p1Things = cup.getPlayer1StackAverage();
		board.getTiles()[2][4].addThings(p1, p1Things);
		cup.removeThings(p1Things);
		
		// Player 2 Stack 2
		List<Thing> p2Things = cup.getPlayer2StackAverage();
		board.getTiles()[3][4].addThings(p2, p2Things);
		cup.removeThings(p2Things);
		
		// Special income counters
		SpecialIncome s1 = cup.getSpecialIncomeCounter("Village"); 
		p1.placeSpecialIncome(s1, board.getTiles()[0][4]);
		
		SpecialIncome s2 = cup.getSpecialIncomeCounter("Village");
		p2.placeSpecialIncome(s2, board.getTiles()[2][6]);
		
		Player p3 = playerManager.getPlayerInPosition(3);
		if (p3 != null) {
			SpecialIncome s3 = cup.getSpecialIncomeCounter("City");  
			p3.placeSpecialIncome(s3, board.getTiles()[4][1]);
		}
		
		Player p4 = playerManager.getPlayerInPosition(4);
		if (p4 != null) {
			SpecialIncome s4 = cup.getSpecialIncomeCounter("Village"); 
			p4.placeSpecialIncome(s4, board.getTiles()[1][0]);
		}
		
	}
	
	private static void setMinimumGameState(Game game) {
		
		PhaseManager phaseManager = game.getPhaseManager();
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();
		Cup cup = game.getCup();

		// Skip initial game phases
		phaseManager.skipInitialPhases();
		
		setGold(game, 10);
		setControlMarkers(game);
		setForts(game, false);

		// Player 1 Stack 1
		Player p1 = playerManager.getPlayerInPosition(1);
		List<Thing> p1Things = cup.getPlayer1Stack1Min();
		board.getTiles()[2][4].addThings(p1, p1Things);
		cup.removeThings(p1Things);
		
		// Player 2 Stack 2
		Player p2 = playerManager.getPlayerInPosition(2);
		List<Thing> p2Things = cup.getPlayer2Stack2Min();
		board.getTiles()[3][4].addThings(p2, p2Things);
		cup.removeThings(p2Things);
		
	}
	
	private static void setGold(Game game, int num) {

		PlayerManager playerManager = game.getPlayerManager();
		
		for (Player p : playerManager.getPlayers()) {
			p.addGold(num);
		}
		
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
	
	private static void setForts(Game game, boolean average) {
		
		PlayerManager playerManager = game.getPlayerManager();
		Board board = game.getBoard();
		
		Player p1 = playerManager.getPlayerInPosition(1);
		setFort(p1, board.getTiles()[3][3], FortFactory.getFort(Type.KEEP, true, false));
		
		if (!average) {
			setFort(p1, board.getTiles()[0][4], FortFactory.getFort(Type.KEEP, true, false)); // village in average
		}
		
		setFort(p1, board.getTiles()[1][4], FortFactory.getFort(Type.CASTLE, true, false));
		setFort(p1, board.getTiles()[2][4], FortFactory.getFort(Type.TOWER, true, false));
		setFort(p1, board.getTiles()[1][5], FortFactory.getFort(Type.TOWER, true, false));
		setFort(p1, board.getTiles()[0][6], FortFactory.getFort(Type.CASTLE, true, false));
		
		Player p2 = playerManager.getPlayerInPosition(2);
		setFort(p2, board.getTiles()[3][4], FortFactory.getFort(Type.TOWER, true, false));
		setFort(p2, board.getTiles()[4][4], FortFactory.getFort(Type.CASTLE, true, false));
		setFort(p2, board.getTiles()[2][5], FortFactory.getFort(Type.KEEP, true, false));
		setFort(p2, board.getTiles()[3][5], FortFactory.getFort(Type.KEEP, true, false));
		setFort(p2, board.getTiles()[1][6], FortFactory.getFort(Type.KEEP, true, false));
		
		if (!average) {
			setFort(p2, board.getTiles()[2][6], FortFactory.getFort(Type.TOWER, true, false)); // village in average
		}
		
		Player p3 = playerManager.getPlayerInPosition(3);
		if (p3 != null) {
			
			if (!average) {
				setFort(p3, board.getTiles()[4][1], FortFactory.getFort(Type.TOWER, true, false)); // city in average
			}
			
			setFort(p3, board.getTiles()[5][2], FortFactory.getFort(Type.KEEP, true, false));
		}
		
		Player p4 = playerManager.getPlayerInPosition(4);
		if (p4 != null) {
			setFort(p4, board.getTiles()[0][0], FortFactory.getFort(Type.KEEP, true, false));
			
			if (!average) {
				setFort(p4, board.getTiles()[1][0], FortFactory.getFort(Type.KEEP, true, false));	// village in average
			}
			
			setFort(p4, board.getTiles()[0][1], FortFactory.getFort(Type.CASTLE, true, false));
			setFort(p4, board.getTiles()[1][1], FortFactory.getFort(Type.TOWER, true, false));
		}
		
	}
	
	private static void setFort(Player player, Tile tile, Fort fort) {
		
		player.addFort(fort);
		tile.setFort(fort);
		
		if (fort.getType() == Type.CITADEL) {
			player.setHasCitadel(true);
		}
		
	}

}
