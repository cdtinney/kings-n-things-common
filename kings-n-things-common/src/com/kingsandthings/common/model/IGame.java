package com.kingsandthings.common.model;

import com.kingsandthings.common.model.board.Tile;

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

}
