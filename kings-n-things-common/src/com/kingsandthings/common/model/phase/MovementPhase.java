package com.kingsandthings.common.model.phase;

import java.util.List;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Thing;

public class MovementPhase extends Phase {
	
	public MovementPhase() { }
	
	public MovementPhase(Game game) {
		super(game, "Movement", 1, false);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		setText();
		
	}
	
	@Override
	public void next() {
		super.next();
		
		setText();
		
	}
	
	@Override
	public void end() {
		super.end();
		
		resetMovement();
		
	}
	
	private void resetMovement() {

		Tile[][] tiles = game.getBoard().getTiles();
		for (Tile[] row : tiles) {
			for (Tile tile : row) {

				if (tile == null) continue;
				
				List<Thing> things = tile.getAllThings();
				for (Thing thing : things) {
					
					if (thing instanceof Creature) {
						((Creature) thing).setMovementEnded(false);
					}
					
				}
				

			}
		}
		
	}
	
	private void setText() {

		setInstruction("do some movement");
		
		// Skip the player if they have no movement possible
		if (!game.getBoard().movementPossible(game.getActivePlayer())) {
			setInstruction("no movement possible! please end turn");
		}
		
	}

}
