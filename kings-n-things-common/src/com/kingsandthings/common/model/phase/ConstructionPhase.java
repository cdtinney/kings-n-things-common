package com.kingsandthings.common.model.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;

public class ConstructionPhase extends Phase {
	
	private static Logger LOGGER = Logger.getLogger(ConstructionPhase.class.getName());
	
	public static final int BUILD_FORT_COST = 5;
	public static final int UPGRADE_FORT_COST = 5;
	
	private transient boolean conquestOnly = false;
	private transient List<Player> playersWithCitadels;
	
	public ConstructionPhase() { }
	
	public ConstructionPhase(Game game) {
		super(game, "Construction Phase", 1, false);

		playersWithCitadels = new ArrayList<Player>();
		
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setInstruction("build or upgrade forts");
		
	}
	
	@Override
	public void end(){
		super.end();
		
		resetUpgradedForts();
		
		if (!conquestOnly) {
			
			List<Player> current = findPlayersWithCitadels();
			boolean winner = checkWinner(current);
			if (winner) {
				game.setWinner(getWinner());
			}
			
		}
		
	}
	
	private Player getWinner() {
		return playersWithCitadels.get(0);
	}
	
	private boolean checkWinner(List<Player> currentPlayersWithCitadels) {
		
		int prevNum = playersWithCitadels.size();
		int currNum = currentPlayersWithCitadels.size();
		
		// Case #1 - previous.size = 1, current.size > 1
		if (prevNum == 1 && currNum > 1) {
			conquestOnly = true;
		}
		
		// Case #2 - prev.size > 1, current > 1
		if (prevNum > 1 && currNum > 1) {
			conquestOnly = true;
		}
		
		// Case #3 - prev.size = 1, curr.size = 1
		if (prevNum == 1 && currNum == 1) {
			
			Player prevPlayer = playersWithCitadels.get(0);
			Player currPlayer = currentPlayersWithCitadels.get(0);
			
			if (prevPlayer.equals(currPlayer)) {
				LOGGER.info("Winner found due to no other players with citadels: " + prevPlayer.getName());
				return true;
			}
			
		}
		
		playersWithCitadels = new ArrayList<Player>(currentPlayersWithCitadels);
		return false;
		
	}
	
	private List<Player> findPlayersWithCitadels() {

		List<Player> players = game.getPlayerManager().getPlayers();
		List<Player> result = new ArrayList<Player>();
		
		for (Player player : players) {
			
			if (player.getHasCitadel()) {
				result.add(player);
			}
			
		}
		
		return result;
		
	}
	
	private void resetUpgradedForts() {

		Tile[][] tiles = game.getBoard().getTiles();
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				if (tile != null && tile.getFort() != null) {
					tile.getFort().setUpgraded(false);
				}
			}
		}
		
	}

}
