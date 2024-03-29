package com.kingsandthings.common.model.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.kingsandthings.common.events.PropertyChangeDispatcher;
import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.combat.Battle;
import com.kingsandthings.common.model.combat.Battle.Step;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;
import com.kingsandthings.common.model.things.Thing;

public class CombatPhase extends Phase {

	private static Logger LOGGER = Logger.getLogger(CombatPhase.class.getName());
	
	private transient List<Tile> battleTiles;
	private Battle currentBattle;
	
	public CombatPhase() { }
	
	public CombatPhase(Game game) {
		super(game, "Combat", 1, false);
	}

	@Override
	public void begin() {
		super.begin();
		
		next();

	}
	
	@Override
	public void next() {
		
		battleTiles = findBattles();
		
		// Find battles involving the current player
		List<Tile> playerBattles = findPlayerBattles(game.getActivePlayer(), battleTiles);
		
		if (playerBattles.isEmpty()) {
			setInstruction("you have no battles to resolve - please end your turn");
		} else {
			setInstruction("please select a battle to resolve");
		}
		
	}
	
	public void setCurrentBattle(Tile tile) {
		
		if (tile == null) {
			currentBattle = null;
			return;
		}
		
		Player attacker = getAttacker(tile);
		Player defender = getDefender(tile);

		List<Creature> attackerCreatures = getCreatures(tile.getThings().get(attacker));
		List<Creature> defenderCreatures = getCreatures(tile.getThings().get(defender));
		
		currentBattle = new Battle(tile, attacker, defender, attackerCreatures, defenderCreatures);
		currentBattle.start();
		
		LOGGER.log(LogLevel.DEBUG, "Battle started");
		PropertyChangeDispatcher.getInstance().notify(this, "currentBattle", null, currentBattle);
		
		setInstruction("resolve the battle!");
		
	}
	
	public Battle getCurrentBattle() {
		return currentBattle;
	}
	
	public void rollForHits(String playerName) {
		
		if (!activePlayer(playerName)) {
			LOGGER.warning("Player can only roll dice during their turn.");
			return;
		}
		
		if (currentBattle.getCurrentStep() != Step.ROLL_DICE) {
			LOGGER.warning("Player can only roll dice during the roll dice step.");
			return;
		}
		
		int hits = computeHits(currentBattle.getCurrentPlayerCreatures());
		
		currentBattle.setRolledHits(hits);
		currentBattle.setNextPlayer();
		
		if (currentBattle.getAllPlayersRolled()) {
			currentBattle.setCurrentStep(Battle.Step.APPLY_HITS);
		}
		
	}
	
	public void applyHits(String playerName, Map<Thing, Integer> hitsToApply) {
		
		if (!activePlayer(playerName)) {
			LOGGER.warning("Player can only apply hits during their turn.");
			return;
		}

		if (currentBattle.getCurrentStep() != Step.APPLY_HITS) {
			LOGGER.warning("Player can only apply hits during the apply hits step.");
			return;
		}
		
		int numCreaturesRemaining = currentBattle.getCurrentPlayerCreatures().size();		
		
		int hitsNeeded = currentBattle.getHitsToApply(playerName);
		int hitsApplied = 0;
		for (int h : hitsToApply.values()) {
			hitsApplied += h;
		}
		
		if (numCreaturesRemaining > hitsApplied && hitsNeeded != hitsApplied) {
			LOGGER.log(LogLevel.STATUS, "Player must apply all hits (applied=" + hitsApplied + ", needed=" + hitsNeeded + ")");
			return;
		}
		
		currentBattle.setHitsToApply(playerName, hitsToApply);
		currentBattle.setNextPlayer();
		
		if (currentBattle.getAllHitsApplied()) {
			currentBattle.setCurrentStep(Battle.Step.RETREAT);
		}
		
	}
	
	public void retreat(String playerName, boolean skip) {
		
		if (!activePlayer(playerName)) {
			LOGGER.warning("Player can only retreat or skip retreat during their turn.");
			return;
		}

		if (currentBattle.getCurrentStep() != Step.RETREAT) {
			LOGGER.warning("Player cannot retreat at this time.");
			return;
		}
		
		if (!skip && currentBattle.getRetreat(playerName)) {
			currentBattle.end(true);
			return;
		}
		
		currentBattle.setSkipRetreat(currentBattle.getSkipRetreat() + 1);
		currentBattle.setNextPlayer();
		
		if (currentBattle.getAllPlayersSkipRetreat()) {
			
			currentBattle.setSkipRetreat(0);
			
			boolean resolved = currentBattle.checkResolved();
			if (resolved) {
				currentBattle.end(false);
				returnEliminatedCreatures(currentBattle);
			} else {
				currentBattle.setCurrentStep(Step.ROLL_DICE);
			}
			
		}
		
	}
	
	private void returnEliminatedCreatures(Battle battle) {
		
		game.getCup().returnThings(battle.getElimAttackerThings());
		game.getCup().returnThings(battle.getElimDefenderThings());
		
	}
	
	private boolean activePlayer(String name) {
		return currentBattle.isCurrentPlayer(name);
	}
	
	private Player getAttacker(Tile tile) {
		for (Player p : tile.getThings().keySet()) {
			if (!p.equals(tile.getOwner()) && !tile.getThings().get(p).isEmpty()) {
				return p;
			}
		}
		return null;
	}
	
	private Player getDefender(Tile tile) {
		return tile.getOwner();
	}
	
	private int computeHits(List<Creature> creatures) {

		int totalHits = 0;
		
		// Roll for magic creatures
		List<Creature> magicCreatures = getCreaturesByAbility(creatures, Ability.MAGIC);
		int magicHits = computeCreatureHits(magicCreatures);
		
		// Roll for range creatures
		List<Creature> rangedCreatures = getCreaturesByAbility(creatures, Ability.RANGE);
		int rangeHits = computeCreatureHits(rangedCreatures);
		
		// Roll for melee creatures
		List<Creature> meleeCreatures = getMeleeCreatures(creatures);
		int meleeHits = computeCreatureHits(meleeCreatures);
		
		totalHits = magicHits + rangeHits + meleeHits;
		
		String message = "Rolled for: ";
		message += magicCreatures.size() + " magic, "; 
		message += rangedCreatures.size() + " ranged, "; 
		message += meleeCreatures.size() + " melee "; 
		message += " = " + magicHits + " + " + rangeHits + " + " + meleeHits + " = " + totalHits;
		LOGGER.log(LogLevel.DEBUG, message);
		
		return totalHits;
		
	}
	
	private int computeCreatureHits(List<Creature> creatures) {
		
		int hits = 0;
		
		for (Creature creature : creatures) {
			
			int combatValue = creature.getCombatValue();
			
			// Roll twice for CHARGING creatures
			if (creature.getAbilities().contains(Ability.CHARGE)) {
				hits += computeHitsByCombatValue(2, combatValue);
				continue;
			}

			hits += computeHitsByCombatValue(1, combatValue);
		}
		
		return hits;
		
	}
	
	private int computeHitsByCombatValue(int numRolls, int combatValue) {
		int total = 0;
		for (int i=0; i<numRolls; i++) {
			total += rollDice() <= combatValue ? 1 : 0;
		}
		return total;
	}
	
	private int rollDice() {
		return (int) (6.0 * Math.random()) + 1; 
	}
	
	private List<Creature> getCreatures(List<Thing> things) {
		List<Creature> creatures = new ArrayList<Creature>();
		for (Thing t : things) {
			if (t instanceof Creature) {
				creatures.add((Creature)t);
			}
		}
		return creatures;
	}
	
	private List<Creature> getCreaturesByAbility(List<Creature> creatures, Ability ability) {
		List<Creature> result = new ArrayList<Creature>();
		for (Creature creature : creatures) {
			if (creature.getAbilities().contains(ability)) {
				result.add(creature);
			}
		}
		return result;
	}
	
	private List<Creature> getMeleeCreatures(List<Creature> creatures) {
		List<Creature> result = new ArrayList<Creature>();
		
		for (Creature creature : creatures) {
			if (!((creature.getAbilities().contains(Ability.RANGE)) || (creature.getAbilities().contains(Ability.MAGIC)))) {
				result.add(creature);
			}
		}
		
		return result;
	}
	
	private List<Tile> findPlayerBattles(Player player, List<Tile> battleTiles) {
		
		List<Tile> playerBattles = new ArrayList<Tile>();
		for (Tile tile : battleTiles) {
			
			if (tile.getOwner().equals(player)|| tile.getThings().get(player) != null) {
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
					battleTiles.add(tile);
				}
				
			}
		}
		
		return battleTiles;
		
	}
	
}