package com.kingsandthings.common.model.phase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.combat.Battle;
import com.kingsandthings.common.model.combat.Battle.Step;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;
import com.kingsandthings.common.model.things.Thing;
import com.kingsandthings.logging.LogLevel;

public class CombatPhase extends Phase {

	private static Logger LOGGER = Logger.getLogger(CombatPhase.class.getName());

	private Game game;
	private List<Tile> battleTiles;
	
	private SimpleStringProperty statusProperty = new SimpleStringProperty("combat phase");
	
	private Battle currentBattle;
	
	public CombatPhase(Game game) {
		super(game, "Combat", true, true, 1, false);
		
		this.game = game;
	}
	
	public SimpleStringProperty getStatus() {
		return statusProperty;
	}

	@Override
	public void begin() {
		super.begin();
		
		// Find all tiles that have battles to resolve
		battleTiles = findBattles();
		
		// Find battles involving the current player
		List<Tile> playerBattles = findPlayerBattles(game.getActivePlayer(), battleTiles);
		
		// Skip the player if no battles were found
		if (playerBattles.isEmpty()) {
			game.getPhaseManager().endPlayerTurn();
		}
		
		currentInstruction = "please select a battle to resolve";

	}
	
	public void beginBattle(Tile tile) {
		
		Player defender = tile.getOwner();
		
		List<Player> attackers = new ArrayList<Player>();
		for (Player player : tile.getThings().keySet()) {
			if (player != defender) {
				attackers.add(player);
			}
		}
		
		List<Creature> attackerCreatures = getCreatures(tile.getThings().get(attackers.get(0)));
		List<Creature> defenderCreatures = getCreatures(tile.getThings().get(defender));
		
		currentBattle = new Battle(tile, attackers.get(0), defender, attackerCreatures, defenderCreatures);

		statusProperty.set("beginning round. " + currentBattle.getCurrentPlayer().getName() + ", please roll.");
		
	}
	
	public Battle getBattle() {
		return currentBattle;
	}
	
	public List<Creature> getCreatures(List<Thing> things) {
		List<Creature> creatures = new ArrayList<Creature>();
		for (Thing t : things) {
			if (t instanceof Creature) {
				creatures.add((Creature)t);
			}
		}
		return creatures;
	}
	
	public boolean addSelected(Player player, Thing thing) {
		
		if (currentBattle.getCurrentPlayer() != player) {
			return false;
		}
		
		return currentBattle.selectThingForHits(player, thing);
	
	}
	
	public void diceRolled() {
		
		Step step = currentBattle.getStep();
		Player player = currentBattle.getCurrentPlayer();
		List<Creature> creatures = currentBattle.currentPlayerCreatures();
		
		if (step.equals(Step.MAGIC)) {
			
			String status = "";
			
			creatures = getCreaturesByAbility(creatures, Ability.MAGIC);
			int hitsRolled = computeHits(creatures);
			
			status = player.getName() + " rolled for " + creatures.size() + " magic creatures for " + hitsRolled + " hits.\n";
			
			currentBattle.setPlayerRolled(player, hitsRolled);
			currentBattle.nextPlayer();
			
			status += currentBattle.getCurrentPlayer().getName() + ", please roll.";
			
		} else if (step.equals(Step.RANGED)) {
			
		} else if (step.equals(Step.MELEE)) {
			
		}
		
		if (currentBattle.allRolled()) {
			statusProperty.set(currentBattle.getCurrentPlayer().getName() + " please select creatures to take hits from");
			// disable roll dice button
			return;
		}
		
	}
	
	private int computeHits(List<Creature> creatures) {

		int hits = 0;
		
		for (Creature creature : creatures) {
			
			int combatValue = creature.getCombatValue();
			
			if (creature.getAbilities().contains(Ability.CHARGE)) {
				hits += rollForHits(2, combatValue);
				continue;
			}

			hits += rollForHits(1, combatValue);
		}
		
		return hits;
		
	}
	
	public int rollForHits(int numRolls, int combatValue) {
		int total = 0;
		for (int i=0; i<numRolls; i++) {
			total += rollDice() <= combatValue ? 1 : 0;
		}
		return total;
	}
	
	public List<Creature> getCreaturesByAbility(List<Creature> creatures, Ability ability) {
		List<Creature> result = new ArrayList<Creature>();
		for (Creature creature : creatures) {
			if (creature.getAbilities().contains(ability)) {
				result.add(creature);
			}
		}
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	private List<Tile> findPlayerBattles(Player player, List<Tile> battleTiles) {
		
		List<Tile> playerBattles = new ArrayList<Tile>();
		for (Tile tile : battleTiles) {
			
			if (tile.getOwner() == player || tile.getThings().get(player) != null) {
				tile.setBattleToResolve(true);
				playerBattles.add(tile);
			}
			
		}
		
		return playerBattles;
		
	}
	
	private List<Tile> findBattles() {
		
		Board board = game.getBoard();
		Tile[][] tiles = board.getTiles();
		
		List<Tile> battleTiles = new ArrayList<Tile>();
		
		for (int i=0; i<tiles.length; ++i) {
			for (int j=0; j<tiles[i].length;  ++j) {
				
				Tile tile = tiles[i][j];
				if (tile == null) {
					continue;
				}
				
				if (tile.getThings().keySet().size() > 1) {
					LOGGER.warning("Battle found");
					battleTiles.add(tile);
				}
				
			}
		}
		
		return battleTiles;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void initiateCombat(Player playerDefender, Player playerAttacker, List<Creature> defenders, List<Creature> attackers) {
		
		int damageToAttacker = 0;
		int damageToDefender = 0;
		
		//Order of operations MAGIC-->RANGE-->Melee/anythingelse respectively
		//MAGIC
		damageToAttacker = computeCombat(playerDefender, getMagicCreatures(defenders));
		damageToDefender = computeCombat(playerAttacker, getMagicCreatures(attackers));
		playerRemoveCreatures(playerAttacker, attackers, damageToAttacker);
		playerRemoveCreatures(playerDefender, defenders, damageToDefender);
		
		//RANGED
		damageToAttacker = computeCombat(playerDefender, getRangedCreatures(defenders));
		damageToDefender = computeCombat(playerAttacker, getRangedCreatures(attackers));
		playerRemoveCreatures(playerAttacker, attackers, damageToAttacker);
		playerRemoveCreatures(playerDefender, defenders, damageToDefender);
		
		//MELEE = !RANGED && !MAGIC
		damageToAttacker = computeCombat(playerDefender, getMeleeCreatures(defenders));
		damageToDefender = computeCombat(playerAttacker, getMeleeCreatures(attackers));
		playerRemoveCreatures(playerAttacker, attackers, damageToAttacker);
		playerRemoveCreatures(playerDefender, defenders, damageToDefender);
		
		//Check if battle is over
		if (!(defenders.isEmpty() || attackers.isEmpty())) {
			initiateCombat(playerDefender,playerAttacker,defenders,attackers);
			LOGGER.log(LogLevel.STATUS, "Battle is over between" + playerDefender + " and " + playerAttacker);
		}
	}

	// Player chooses set of creatures that receive damage hits, call this function to remove them
	private void playerRemoveCreatures(Player player, List<Creature> creatures, int damage) {
		// TODO player chooses creatures to remove/kill = damage dealt
		// player.remove([all chosen creatures]);
	}

	// Returns amount of damage a set of creatures deal based on die rolls
	private int computeCombat(Player player, List<Creature> creatures) {

		int damageDealt = 0;
		int diceRoll;
		// get attack damages from defending player's creatures
		for (Creature creature : creatures) {
			int combatValue = creature.getCombatValue();
			
			if (creature.getAbilities().contains("CHARGE")) {
				diceRoll = rollDice();
				if (diceRoll >= combatValue) {
					damageDealt += 1;
				}
			}

			diceRoll = rollDice();
			if (diceRoll >= combatValue) {
				damageDealt += 1;
			}
		}
		return damageDealt;
	}
	
	private List<Creature> getMagicCreatures(List<Creature> creatures) {
		List<Creature> magicCreatures = Collections.<Creature>emptyList();
		for (Creature creature : creatures) {
			if (creature.getAbilities().contains(Ability.MAGIC)) {
				magicCreatures.add(creature);
			}
		}
		return magicCreatures;
	}
	
	private List<Creature> getRangedCreatures(List<Creature> creatures) {
		List<Creature> rangedCreatures = Collections.<Creature>emptyList();
		for (Creature creature : creatures) {
			if (creature.getAbilities().contains(Ability.RANGE)) {
				rangedCreatures.add(creature);
			}
		}
		return rangedCreatures;
	}
	
	private List<Creature> getMeleeCreatures(List<Creature> creatures) {
		List<Creature> meleeCreatures = Collections.<Creature>emptyList();
		
		for (Creature creature : creatures) {
			if (!((creature.getAbilities().contains(Ability.RANGE)) || (creature.getAbilities().contains(Ability.MAGIC)))) {
				meleeCreatures.add(creature);
			}
		}
		
		return meleeCreatures;
	}
	
	private int rollDice() {
		return (int) (6.0 * Math.random()) + 1; 
	}
	
}