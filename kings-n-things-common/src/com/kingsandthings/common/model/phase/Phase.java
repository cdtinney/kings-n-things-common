package com.kingsandthings.common.model.phase;

import java.util.logging.Logger;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.game.events.NotificationDispatcher;

public abstract class Phase {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(Phase.class.getName());
	
	public enum Notification {
		BEGIN,
		NEXT,
		END,
		STEP
	}
	
	
	protected String currentStep = "none";
	protected String currentInstruction = "Phase Instruction Placeholder";
	
	protected Game game;
	
	private String name;
	
	private boolean mandatory; 
	private boolean playerInteraction;
	private boolean initial;
	
	private int numPlayerTurns;
	private int currentNumberTurns = 0;
	
	public Phase () { }
	
	public Phase(Game game, String name, boolean mandatory, boolean playerInteraction, int numPlayerTurns, boolean initial) {
		this.game = game;
		this.name = name;
		this.mandatory = mandatory;
		this.numPlayerTurns = numPlayerTurns;
		this.playerInteraction = playerInteraction;
		this.initial = initial;
	}

	public String getName() {
		return name;
	}
	
	public boolean playerInteractionRequired() {
		return playerInteraction;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public boolean isInitial() {
		return initial;
	}
	
	public void nextTurn() {
		
		game.getPlayerManager().nextPlayer();
		currentNumberTurns++;
		
		if (isLastTurn()) {
			
			// End the phase
			end();
			
			// Move to next phase
			game.getPhaseManager().nextPhase();
			
		} else {
			
			if (allPlayersCompletedTurn()) {
				nextStep();
			} else {
				next();
			}
			
		}
		
	}
	
	public String getStep() {
		return currentStep;
	}
	
	protected void begin() {
		currentNumberTurns = 0;
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
