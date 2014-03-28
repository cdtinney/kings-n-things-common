package com.kingsandthings.common.model.phase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;

public class StartingKingdomsPhase extends Phase {
	
	private final static BooleanProperty active = new SimpleBooleanProperty(false);
	
	private final int initialNumGold = 10;
	
	public StartingKingdomsPhase() { }
	
	public StartingKingdomsPhase(Game game) {
		super(game, "Starting Kingdoms", true, true, 2, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		active.set(true);
		
		currentInstruction = "please place a control marker";
		
		for (Player player: game.getPlayerManager().getPlayers()) {
			
			// Give each player 10 gold 
			player.addGold(initialNumGold);
			
		}
		
	}
	
	@Override 
	public void end() {
		super.end();
		active.set(false);
	}
	
	public static BooleanProperty getActive() {
		return active;
	}
	

}
