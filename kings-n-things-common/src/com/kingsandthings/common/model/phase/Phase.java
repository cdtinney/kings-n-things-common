package com.kingsandthings.common.model.phase;

import java.util.logging.Logger;

import com.kingsandthings.common.events.NotificationDispatcher;
import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;

public abstract class Phase {
	
	private static Logger LOGGER = Logger.getLogger(Phase.class.getName());
	
	public enum Notification {
		BEGIN,
		NEXT,
		END,
		STEP
	}
	
	protected transient Game game;
	protected String currentStep = "NONE";
	
	private String name;
	
	private boolean initial;
	
	private int numPlayerTurns;
	private int currentNumberTurns = 0;
	
	public Phase () { }
	
	public Phase(Game game, String name, int numPlayerTurns, boolean initial) {
		this.game = game;
		this.name = name;
		this.numPlayerTurns = numPlayerTurns;
		this.initial = initial;
	}

	public String getName() {
		return name;
	}
	
	public String getStep() {
		return currentStep;
	}
	
	public boolean getInitial() {
		return initial;
	}
	
	public void nextTurn() {
		
		LOGGER.log(LogLevel.STATUS, "");
		
		currentNumberTurns++;
		
		if (isLastTurn()) {
			
			end();
			game.getPhaseManager().nextPhase();
			
		} else {
			
			game.getPlayerManager().setNextPlayerActive();
			
			if (allPlayersCompletedTurn()) {
				nextStep();
			} else {
				next();
			}
			
		}
		
	}
	
	protected void setInstruction(String instruction) {
		game.setInstruction(instruction);
	}
	
	protected void setStep(String text) {
		PropertyChangeDispatcher.getInstance().notify(this, "currentStep", currentStep, currentStep = text);
	}
	
	protected void begin() {
		currentNumberTurns = 0;
		setInstruction("no instruction");
		notify(Notification.BEGIN);
	}
	
	protected void next() {
		notify(Notification.NEXT);
	}
	
	protected void nextStep() {
		next();
	}
	
	protected void end() {
		notify(Notification.END);
	}
	
	protected void notify(Notification type) {
		NotificationDispatcher.getInstance().notify(getClass(), type);
	}
	
	private boolean allPlayersCompletedTurn() {
		
		if (game != null) {
			return currentNumberTurns % game.getPlayerManager().getNumPlayers() == 0;
		}
		
		return false;
	}
	
	private boolean isLastTurn() {
		
		if (game != null) {
			return currentNumberTurns == (numPlayerTurns * game.getPlayerManager().getNumPlayers());
		}
		
		return false;
	}

}
