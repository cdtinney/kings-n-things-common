package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class ConstructionPhase extends Phase {
	
	public ConstructionPhase() { }
	
	public ConstructionPhase(Game game) {
		super(game, "Construction Phase (SKIP)", false, false, 1, false);
	}

}
