package com.kingsandthings.common.model;

import com.kingsandthings.common.model.board.Board;

public interface IGame {
	
	public Player getActivePlayer();
	
	public Board getBoard();
	
	public boolean setTileControl(int r, int c, boolean initial);

}
