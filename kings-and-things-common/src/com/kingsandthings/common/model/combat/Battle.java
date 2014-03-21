package com.kingsandthings.common.model.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;

public class Battle {
	
	public enum Step {
		MAGIC,
		RANGED,
		MELEE,
		RETREAT
	}
	
	private Tile tile;
	
	private Player attacker;
	private Player defender;
	
	private List<Creature> attackerCreatures;
	private List<Creature> defenderCreatures;
	
	private Player currentPlayer;
	private Step currentStep;
	
	private Map<Player, Integer> playerRolls;
	
	private List<Creature> eliminated;
	
	public Battle(Tile tile, Player attacker, Player defender, List<Creature> attackerCreatures, List<Creature> defenderCreatures) {
		this.tile = tile;
		
		this.attacker = attacker;
		this.defender = defender;
		
		this.attackerCreatures = attackerCreatures;
		this.defenderCreatures = defenderCreatures;
		
		currentPlayer = attacker;
		currentStep = Step.MAGIC;
		
		playerRolls = new HashMap<Player, Integer>();
		eliminated = new ArrayList<Creature>();
	}
	
	public boolean selectThingForHits(Player player, Thing thing) {
		
		if (thing instanceof Creature) {
			eliminated.add((Creature) thing);
			return true;
		} else {
			// subtract from the combat value of forts etc.
		}
		
		return false;
		
	}
	
	public boolean allRolled() {
		return playerRolls.get(attacker) != null && playerRolls.get(defender) != null;
	}
	
	public List<Creature> currentPlayerCreatures() {
		
		if (currentPlayer == attacker) {
			return attackerCreatures;
		} else {
			return defenderCreatures;
		}
		
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void setPlayerRolled(Player player, Integer roll) {
		playerRolls.put(player, roll);
	}
	
	public void nextPlayer() {
		
		if (currentPlayer == attacker) {
			currentPlayer = defender;
		} else {
			currentPlayer = attacker;
		}
		
	}
	
	public Step getStep() {
		return currentStep;
	}
	
	
}
