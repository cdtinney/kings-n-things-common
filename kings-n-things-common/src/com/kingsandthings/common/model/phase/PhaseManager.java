package com.kingsandthings.common.model.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.logging.LogLevel;

public class PhaseManager {
	
	private static Logger LOGGER = Logger.getLogger(PhaseManager.class.getName());
	
	private transient Game game;
	
	private List<Phase> phases;
	private int currentPhaseNumber = 0;
	
	public PhaseManager() { }
	
	public PhaseManager(Game game) {
		this.game = game;

		phases = new ArrayList<Phase>();
		
		// Add the phases (in order)
		phases.add(new StartingKingdomsPhase(game));
		phases.add(new TowerPlacementPhase(game));
		phases.add(new InitialRecruitmentPhase(game));
		
		// Main sequence
		phases.add(new GoldCollectionPhase(game));
		phases.add(new RecruitCharactersPhase(game));
		phases.add(new ThingRecruitmentPhase(game));
		phases.add(new RandomEventsPhase(game));
		phases.add(new MovementPhase(game));
		phases.add(new ConstructionPhase(game));
		phases.add(new SpecialPowersPhase(game));
		phases.add(new ChangingPlayerOrderPhase(game));
		
	}
	
	public void endPlayerTurn() {
		
		new Thread() {
			
			@Override
			public void run() {
				getCurrentPhase().nextTurn();
			}
			
		}.start();
		
	}
	
	public Phase getCurrentPhase() {
		return phases.get(currentPhaseNumber);
	}
	
	public void beginPhases() {
		phases.get(0).begin();
		PropertyChangeDispatcher.getInstance().notify(this, "currentPhase", null, getCurrentPhase());
	}
	
	public void nextPhase() {
		
		Phase oldPhase = getCurrentPhase();
		
		currentPhaseNumber = (currentPhaseNumber + 1) % phases.size();
		
		Phase newPhase = phases.get(currentPhaseNumber);
		
		if (oldPhase.isInitial()) {
			phases.remove(oldPhase);
			currentPhaseNumber--;
		}
		
		LOGGER.log(LogLevel.DEBUG, "Beginning phase - " + newPhase.getName());
		
		newPhase.begin();
		notifyPhaseChange(oldPhase, newPhase);
		
	}
	
	private void notifyPhaseChange(Phase oldPhase, Phase newPhase) {
		PropertyChangeDispatcher.getInstance().notify(this, "currentPhase", oldPhase, newPhase);
	}

}
