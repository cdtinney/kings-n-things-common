package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class RandomEventsPhase extends Phase {
	
	public RandomEventsPhase() { }

	public RandomEventsPhase(Game game) {
		super(game, "Random Events (SKIP)", false, false, 1, false);
	}
	
}
