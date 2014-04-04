package com.kingsandthings.common.model;

public interface IGame {
	
	public Player getActivePlayer();
	public void endTurn(String playerName);
	
	public void recruitThingsInitial();
	public void recruitThings(int numPaidRecruits);

}
