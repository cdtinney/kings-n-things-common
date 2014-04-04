package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class SpecialPowersPhase extends Phase {
	
	public SpecialPowersPhase() { }

	public SpecialPowersPhase(Game game) {
		super(game, "Special Powers Phase (SKIP)", 1, false);
	}

}

