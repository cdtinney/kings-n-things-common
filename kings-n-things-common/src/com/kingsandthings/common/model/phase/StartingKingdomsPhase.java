package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;

public class StartingKingdomsPhase extends Phase {
	
	private final int initialNumGold = 10;
	
	public StartingKingdomsPhase() { }
	
	public StartingKingdomsPhase(Game game) {
		super(game, "Starting Kingdoms", 2, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setInstruction("please select a starting position and place a control marker");
		
		for (Player player: game.getPlayerManager().getPlayers()) {
			
			// Give each player 10 gold 
			player.addGold(initialNumGold);
			
		}
		
	}
	
	@Override
	public void next() {
		super.next();
		
		if (game.getActivePlayer().isStartingPositionSelected()) {
			setInstruction("please place a control marker");
		} else {
			setInstruction("please select a starting position and place a control marker");
		}
		
	}

}
