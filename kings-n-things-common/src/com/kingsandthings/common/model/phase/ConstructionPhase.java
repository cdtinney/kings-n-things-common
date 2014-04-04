package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.board.Tile;

public class ConstructionPhase extends Phase {
	
	public static final int BUILD_FORT_COST = 5;
	public static final int UPGRADE_FORT_COST = 5;
	
	public ConstructionPhase() { }
	
	public ConstructionPhase(Game game) {
		super(game, "Construction Phase (SKIP)", false, false, 1, false);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setInstruction("build or upgrade forts");
		
	}
	
	@Override
	public void end(){
		super.end();
		
		resetUpgradedForts();
		
	}
	
	public void resetUpgradedForts() {

		Tile[][] tiles = game.getBoard().getTiles();
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				if (tile != null && tile.getFort() != null) {
					tile.getFort().setUpgraded(false);
				}
			}
		}
		
	}

}
