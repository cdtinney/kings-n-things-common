package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class InitialRecruitmentPhase extends Phase {

	public InitialRecruitmentPhase () { }
	
	public InitialRecruitmentPhase(Game game) {
		super(game, "Initial Thing Recruitment", false, true, 2, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		currentStep = "Draw_Things";
		currentInstruction = "please draw 10 Things from the Cup";
		
	}
	
	@Override
	protected void nextStep() {
		notify(Notification.STEP);
		
		currentStep = "Thing_Placement";
		currentInstruction = "please place your Things";
		
	}

}
