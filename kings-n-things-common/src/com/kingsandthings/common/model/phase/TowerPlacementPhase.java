package com.kingsandthings.common.model.phase;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.FortFactory;

public class TowerPlacementPhase extends Phase {
	
	public TowerPlacementPhase () { }

	public TowerPlacementPhase(Game game) {
		super(game, "Tower Placement", true, true, 1, true);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setInstruction("please place a tower");
		
		for (Player player: game.getPlayerManager().getPlayers()) {
			
			// Give each player a tower
			player.addFort(FortFactory.getFort(Fort.Type.TOWER, false, false));
			
		}
		
	}

}
