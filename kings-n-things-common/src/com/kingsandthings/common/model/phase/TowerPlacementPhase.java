package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.things.Fort;

public class TowerPlacementPhase extends Phase {
	
	public TowerPlacementPhase () { }

	public TowerPlacementPhase(Game game) {
		super(game, "Tower Placement", true, true, 1, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		//BoardView.setInstructionText("please place a tower");
		currentInstruction = "please place a tower";
		
		for (Player player: game.getPlayerManager().getPlayers()) {
			
			// Give each player a tower
			player.addFort(Fort.getTower());
		}
		
	}

}
