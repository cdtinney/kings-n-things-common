package com.kingsandthings.common.model.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;

public class Battle {

	private static Logger LOGGER = Logger.getLogger(Battle.class.getName());
	
	private transient Tile tile;
	
	private Player attacker;
	private Player defender;
	
	private List<Creature> attackerCreatures;
	private List<Creature> defenderCreatures;
	
	private transient Player currentPlayer;
	
	private List<Creature> eliminated;
	
	private int attackerHits;
	private int defenderHits;
	
	public Battle() { }
	
	public Battle(Tile tile, Player attacker, Player defender, List<Creature> attackerCreatures, List<Creature> defenderCreatures) {
		this.tile = tile;
		
		this.attacker = attacker;
		this.defender = defender;
		
		this.attackerCreatures = attackerCreatures;
		this.defenderCreatures = defenderCreatures;
		
		eliminated = new ArrayList<Creature>();
	}
	
	public void start() {
		currentPlayer = attacker;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
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
	
	public List<Creature> getCurrentPlayerCreatures() {
		
		if (currentPlayer == attacker) return attackerCreatures;
		if (currentPlayer == defender) return defenderCreatures;
		
		return null;
		
	}
	
	public void setHits(int hits) {
		
		if (currentPlayer == attacker) { 
			LOGGER.log(LogLevel.DEBUG, "Setting attacker hits: " + hits);
			attackerHits = hits;
		}
		
		if (currentPlayer == defender) {
			LOGGER.log(LogLevel.DEBUG, "Setting defender hits: " + hits);
			defenderHits = hits;
		}
		
	}
	
	public void setNextPlayer() {
		
		Player prevPlayer = currentPlayer;
		
		if (currentPlayer == attacker)  {
			LOGGER.log(LogLevel.DEBUG, "Setting current player to defender");
			currentPlayer = defender;
		} else if (currentPlayer == defender) {
			LOGGER.log(LogLevel.DEBUG, "Setting current player to attacker");
			currentPlayer = attacker;
		}
		
		PropertyChangeDispatcher.getInstance().notify(this, "currentPlayer", prevPlayer, currentPlayer);
		
	}
	
}
