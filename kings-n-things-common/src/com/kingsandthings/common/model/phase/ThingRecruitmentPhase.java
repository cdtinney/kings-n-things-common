package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;

public class ThingRecruitmentPhase extends Phase {
	
	public static final String PLACEMENT = "Placement";
	public static final String DRAW = "Draw";
	
	public ThingRecruitmentPhase() { }

	public ThingRecruitmentPhase(Game game) {
		super(game, "Thing Recruitment", 2, false);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setStep(DRAW);
		setInstruction("please recruit Things");
		
	}
	
	@Override
	public void nextStep() {
		super.nextStep();
		
		setStep(PLACEMENT);
		setInstruction("please place your Things");
		
	}
	
	@Override
	public void end() {
		super.end();
		
	}
	
}
