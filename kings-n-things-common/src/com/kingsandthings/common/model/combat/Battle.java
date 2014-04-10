package com.kingsandthings.common.model.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;

public class Battle {

	private static Logger LOGGER = Logger.getLogger(Battle.class.getName());
	
	public enum Step {
		ROLL_DICE,
		APPLY_HITS,
		RETREAT,
		RESOLVED,
		NONE
	}
	
	private transient Tile tile;
	
	private String winner;
	private String currentPlayer;
	
	private Step currentStep;
	
	private Player attacker;
	private Player defender;
	
	private List<Creature> attackerCreatures;
	private List<Creature> defenderCreatures;
	
	private List<Thing> eliminatedAttackerThings;
	private List<Thing> eliminatedDefenderThings;
	
	private Integer attackerHits = null;
	private Integer defenderHits = null;
	
	private boolean resolved = false;
	private int numSkipRetreat = 0;
	
	public Battle() { }
	
	public Battle(Tile tile, Player attacker, Player defender, List<Creature> attackerCreatures, List<Creature> defenderCreatures) {
		this.tile = tile;
		
		this.attacker = attacker;
		this.defender = defender;
		
		this.attackerCreatures = attackerCreatures;
		this.defenderCreatures = defenderCreatures;
		
		currentStep = Step.NONE;
		
		eliminatedAttackerThings = new ArrayList<Thing>();	
		eliminatedDefenderThings = new ArrayList<Thing>();
	}
	
	public void start() {
		currentPlayer = attacker.getName();
		setCurrentStep(Step.ROLL_DICE);
	}
	
	public void end(boolean retreat) {
		
		// Set this battle to resolved
		resolved = true;
		
		// Transfer the fort
		Player prevOwner = tile.getOwner();
		Player newOwner = getPlayer(winner);
		if (tile.getFort() != null) {
			prevOwner.removeFort(tile.getFort());
			newOwner.addFort(tile.getFort());
		}
		
		// Set the tile owner, and indicate the battle has been resolved
		tile.setOwner(newOwner);
		tile.setBattleToResolve(false);
		
		// Clear eliminated creatures
		tile.removeThings(attacker, eliminatedAttackerThings);
		tile.removeThings(defender, eliminatedDefenderThings);
		
		// Set the step to resolved
		setCurrentStep(Step.RESOLVED);
	
	}

	public boolean checkResolved() {
		
		// Attacking = eliminated, hex owner = defender 
		if (attackerCreatures.isEmpty()) {
			LOGGER.warning("All attacking counters eliminated, owner is still defender.");
			winner = defender.getName();
			return true;
		}
		
		// Defending = eliminated, attacking > = 1, hex owner = attacker
		if (defenderCreatures.isEmpty() && !attackerCreatures.isEmpty()) {
			LOGGER.warning("All defending counters eliminated, owner is now attacker.");
			winner = attacker.getName();
			return true;
		}
		
		return false;

	}

	public boolean isCurrentPlayer(String name) {
		return currentPlayer.equals(name);
	}
	
	public boolean isCurrentPlayer(Player player) {
		return currentPlayer.equals(player.getName());
	}
	
	public Step getCurrentStep() {
		return currentStep;
	}
	
	public boolean getResolved() {
		return resolved;
	}
	
	public Player getAttacker() {
		return attacker;
	}
	
	public Player getDefender() {
		return defender;
	}
	
	public List<Creature> getAttackerCreatures() { 
		return attackerCreatures;
	}
	
	public List<Creature> getDefenderCreatures() {
		return defenderCreatures;
	}
	
	public List<Thing> getElimAttackerThings() { 
		return eliminatedAttackerThings;
	}
	
	public List<Thing> getElimDefenderThings() {
		return eliminatedDefenderThings;
	}
	
	public List<Creature> getCurrentPlayerCreatures() {
		
		if (isCurrentPlayer(attacker)) return attackerCreatures;
		if (isCurrentPlayer(defender)) return defenderCreatures;
		
		return null;
		
	}
	
	public boolean getAllPlayersRolled() {
		return attackerHits != null && defenderHits != null;
	}
	
	public boolean getAllHitsApplied() {
		return attackerHits == null && defenderHits == null;
	}
	
	public boolean getAllPlayersSkipRetreat() {
		return numSkipRetreat == 2;
	}
	
	public int getSkipRetreat() {
		return numSkipRetreat;
	}
	
	public int getHitsToApply(String playerName) {
		
		if (attacker.getName().equals(playerName)) {
			return defenderHits;
		} else if (defender.getName().equals(playerName)) {
			return attackerHits;
		}
		
		return -1;
		
	}
	
	public void setSkipRetreat(int num) {
		numSkipRetreat = num;
	}
	
	public void setHitsToApply(String playerName, Map<Thing, Integer> hitsToApply) {
		
		List<Creature> creatures = null;
		
		if (attacker.getName().equals(playerName)) {
			creatures = attackerCreatures;
		} else if (defender.getName().equals(playerName)) {
			creatures = defenderCreatures;
		}
		
		if (creatures == null) {
			return;
		}
		
		Set<Thing> eliminated = hitsToApply.keySet();
		
		for (Thing thing : eliminated) {
			if (!creatures.contains(thing)) {
				LOGGER.warning("Cannot apply hits to a creature not in battle.");
				return;
			}
		}
		
		for (Thing thing : eliminated) {
			creatures.remove(thing);
		}
		
		if (creatures == attackerCreatures) {
			eliminatedAttackerThings.addAll(eliminated);
			defenderHits = null;
		} else if (creatures == defenderCreatures) {
			eliminatedDefenderThings.addAll(eliminated);			
			attackerHits = null;
		}
		
		PropertyChangeDispatcher.getInstance().notify(this, "creatures", null, creatures);
		
	}
	
	public void setRolledHits(int hits) {
		
		if (isCurrentPlayer(attacker)) { 
			LOGGER.log(LogLevel.DEBUG, "Setting attacker hits: " + hits);
			attackerHits = hits;
		} else if (isCurrentPlayer(defender)) {
			LOGGER.log(LogLevel.DEBUG, "Setting defender hits: " + hits);
			defenderHits = hits;
		}
		
	}
	
	public void setNextPlayer() {
		
		if (isCurrentPlayer(attacker))  {
			currentPlayer = defender.getName();
		} else if (isCurrentPlayer(defender)) {
			currentPlayer = attacker.getName();
		}
		
		PropertyChangeDispatcher.getInstance().notify(this, "currentPlayer", null, currentPlayer);
		
	}
	
	public void setCurrentStep(Step step) {
		PropertyChangeDispatcher.getInstance().notify(this, "currentStep", currentStep, currentStep = step);
	}

	public boolean getRetreat(String playerName) {
		
		Player player = getPlayer(playerName);
		
		// Player must control an adjacent hex TASK - without enemy counters
		List<Tile> tiles = tile.getNeighbours();
		Tile controlled = null;
		for (Tile neighbour : tiles) {
			if (neighbour.getOwner().equals(player)) {
				controlled = neighbour;
				break;
			}
		}
		
		if (controlled == null) {
			LOGGER.warning("Player cannot retreat - no adjacent hexes controlled.");
			return false;
		}
		
		List<? extends Thing> creatures = getThings(player);
		
		List<? extends Thing> friendlyThings = controlled.getThings().get(player);
		if (friendlyThings != null && (friendlyThings.size() + creatures.size() > 10)) {
			LOGGER.warning("All Things cannot be moved due to hex limits.");
			
			int diff = 10 - friendlyThings.size();
			controlled.addThings(player, creatures.subList(0, diff));
			
			LOGGER.warning(diff + " Things retreated.");
			
			// TODO - return excess to Cup rather than just "lose" them
			tile.removeThings(player, creatures.subList(diff, creatures.size() - 1));
			
		} else {
			
			controlled.addThings(player, creatures);
			tile.removeThings(player, creatures);
			
		}
		
		return true;
		
	}
	
	private Player getPlayer(String playerName) {
		
		if (attacker.getName().equals(playerName)) {
			return attacker;
		} else if (defender.getName().equals(playerName)) {
			return defender;
		}
		
		return null;
		
	}
	
	private List<? extends Thing> getThings(Player player) {
		
		if (player == attacker) {
			return attackerCreatures;
		}
		
		if (player == defender) {
			return defenderCreatures;
		}
		
		return null;
		
	}
	
}
