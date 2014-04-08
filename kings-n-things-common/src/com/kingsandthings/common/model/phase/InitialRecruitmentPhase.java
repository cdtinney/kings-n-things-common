package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class InitialRecruitmentPhase extends Phase {
	
	public static final String PLACEMENT = "Placement";
	public static final String DRAW = "Draw";

	public InitialRecruitmentPhase () { }
	
	public InitialRecruitmentPhase(Game game) {
		super(game, "Initial Thing Recruitment", 2, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setStep(DRAW);
		setInstruction("please draw your initial Things");
		
	}
	
	@Override
	protected void nextStep() {
		super.nextStep();
		
		setStep(PLACEMENT);
		setInstruction("please place your Things");
		
	}

}
