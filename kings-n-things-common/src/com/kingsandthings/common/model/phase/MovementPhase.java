package com.kingsandthings.common.model.phase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.kingsandthings.common.model.Game;

public class MovementPhase extends Phase {
	
	private final static BooleanProperty active = new SimpleBooleanProperty(false);
	
	public MovementPhase() { }
	
	public MovementPhase(Game game) {
		super(game, "Movement", false, true, 1, false);
	}
	
	public static BooleanProperty getActive() {
		return active;
	}
	
	@Override
	public void begin() {
		super.begin();
		active.set(true);
		
		setText();
		
	}
	
	@Override
	public void next() {
		super.next();
		
		setText();
		
	}
	
	@Override
	public void end() {
		super.end();
		active.set(false);
		
	}
	
	private void setText() {

		setInstruction("do some movement");
		
		// Skip the player if they have no movement possible
		if (!game.getBoard().movementPossible(game.getActivePlayer())) {
			setInstruction("no movement possible! please end turn");
		}
		
	}

}
