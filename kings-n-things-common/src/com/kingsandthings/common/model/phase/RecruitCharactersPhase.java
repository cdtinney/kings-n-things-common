package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class RecruitCharactersPhase extends Phase {
	
	public RecruitCharactersPhase() { }

	public RecruitCharactersPhase(Game game) {
		super(game, "Recruit Characters (SKIP)", false, false, 1, false);
	}

}
