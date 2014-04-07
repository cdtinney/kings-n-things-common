package com.kingsandthings.common.model;

import java.util.Map;

import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Thing;

public interface IGame {
	
	// Player management
	public Player getActivePlayer();
	public void endTurn(String playerName);
	
	// Thing recruitment
	public void recruitThingsInitial();
	public void recruitThings(int numPaidRecruits);
	
	// Combat
	public void resolveCombat(Tile tile);
	public void rollCombatDice(String playerName);
	public void applyHits(String playerName, Map<Thing, Integer> hits);

}
