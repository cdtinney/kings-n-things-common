package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class ChangingPlayerOrderPhase extends Phase {
	
	public ChangingPlayerOrderPhase() { }

	public ChangingPlayerOrderPhase(Game game) {
		super(game, "Changing Player Order Phase", 1, false);
	}
	
	@Override 
	public void begin() {
		super.begin();
		
		setInstruction("click end turn");
	}
	
}
