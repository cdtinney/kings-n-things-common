package com.kingsandthings.common.model.board;

import java.util.List;

import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;

public interface IBoard {

	public boolean setTileControl(Tile tile, boolean initial);
	
	public boolean addThingsToTile(Tile tile, List<Thing> things);
	public boolean placeFort(Fort fort, Tile tile);
	public boolean placeSpecialIncome(SpecialIncome specialIncome, Tile tile);

	public boolean moveThingsToUnexploredTile(int roll, Tile initialMovementTile, Tile tile, List<Thing> selectedThings);
	public boolean moveThings(Tile initialMovementTile, Tile tile, List<Thing> selectedThings);
	
	public boolean buildFort(Tile tile);
	public boolean upgradeFort(Tile tile);
	
}
