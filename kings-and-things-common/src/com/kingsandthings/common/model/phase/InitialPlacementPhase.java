package com.kingsandthings.common.model.phase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.kingsandthings.common.model.Game;

public class InitialPlacementPhase extends Phase {
	
	private final static BooleanProperty active = new SimpleBooleanProperty(false);

	public InitialPlacementPhase(Game game) {
		super(game, "Thing Placement", false, true, 2, true);
	}
	
	public static BooleanProperty getActive() {
		return active;
	}
	
	@Override
	public void begin() {
		currentStep = "Draw_Things";
		currentInstruction = "please draw 10 Things from the Cup";
		//BoardView.setInstructionText("please draw 10 Things from the cup");
		
		super.begin();
		active.set(true);
		
	}
	
	@Override
	protected void nextStep() {
		notify(Notification.STEP);
		
		currentStep = "Thing_Placement";
		currentInstruction = "please place your Things";
		//BoardView.setInstructionText("please place your Things");
		
	}
	
	@Override
	public void end() {
		super.end();
		active.set(false);
	}

}
