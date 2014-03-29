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
		setInstruction("please draw your initial Things (hardcoded)");
		
	}
	
	@Override
	protected void nextStep() {
		notify(Notification.STEP);
		
		currentStep = "Thing_Placement";
		setInstruction("please place your Things");
		
	}

}
