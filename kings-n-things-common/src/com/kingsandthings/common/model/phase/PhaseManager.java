package com.kingsandthings.common.model.phase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;

public class PhaseManager {
	
	private static Logger LOGGER = Logger.getLogger(PhaseManager.class.getName());
	
	private transient Game game;
	private transient List<Phase> phases;
	
	private Phase currentPhase;
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
		//phases.add(new RecruitCharactersPhase(game));
		//phases.add(new ThingRecruitmentPhase(game));
		//phases.add(new RandomEventsPhase(game));
		phases.add(new MovementPhase(game));
		phases.add(new CombatPhase(game));
		phases.add(new ConstructionPhase(game));
		//phases.add(new SpecialPowersPhase(game));
		phases.add(new ChangingPlayerOrderPhase(game));
		
		// Set current phase
		currentPhase = phases.get(0);
		
	}
	
	public Phase getCurrentPhase() {
		return currentPhase;
	}
	
	public void endPlayerTurn() {
		
		new Thread() {
			
			@Override
			public void run() {
				getCurrentPhase().nextTurn();
			}
			
		}.start();
		
	}
	
	public void beginPhases() {
		phases.get(0).begin();
		notifyPhaseChange(null, currentPhase);
	}
	
	public void nextPhase() {
		
		Phase oldPhase = getCurrentPhase();
		
		currentPhaseNumber = (currentPhaseNumber + 1) % phases.size();
		
		// Change player order on every turn
		if (currentPhaseNumber == 0) {
			game.getPlayerManager().changePlayerOrder();
		}
		
		Phase newPhase = phases.get(currentPhaseNumber);
		
		if (oldPhase.getInitial()) {
			phases.remove(oldPhase);
			currentPhaseNumber--;
		}
		
		LOGGER.log(LogLevel.DEBUG, "Beginning phase - " + newPhase.getName());

		currentPhase = newPhase;
		newPhase.begin();
		notifyPhaseChange(oldPhase, newPhase);
		
	}
	
	public void skipInitialPhases() {
		
		Iterator<Phase> iter = phases.iterator();
		while (iter.hasNext()) {
			if (iter.next().getInitial()) {
				iter.remove();
			}
		}
		
		currentPhase = phases.get(0);
		
	}
	
	private void notifyPhaseChange(Phase oldPhase, Phase newPhase) {
		PropertyChangeDispatcher.getInstance().notify(this, "currentPhase", oldPhase, newPhase);
	}

}
