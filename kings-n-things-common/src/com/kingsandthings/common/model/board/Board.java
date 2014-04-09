package com.kingsandthings.common.model.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.phase.ConstructionPhase;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.FortFactory;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.common.model.things.Thing;

public class Board implements IBoard {
	
	private static Logger LOGGER = Logger.getLogger(Board.class.getName());
	
	private final int NUM_INITIAL_TILES = 3;
	
	private transient Game game;
	private Tile[][] tiles;
	
	public Board() { }
	
	public Board(Game game) {
		this.game = game;
	}
	
	public void generateBoard(int numPlayers) {
		tiles = generateTiles(10);
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public List<Tile> getNeighbours(Tile[][] tiles, Tile tile) {
		
		List<Tile> neighbours = new ArrayList<Tile>();
		
		int r = -1;
		int c = -1;
		
		for (int i=0; i<tiles.length; ++i) {
			for (int j=0; j<tiles[i].length; ++j) {
				
				if (tiles[i][j] != null && tiles[i][j].equals(tile)) {
					r = i;
					c = j;
				}
			}
		}
		
		if (r == -1 || c == -1) {
			return null;
		}
		
		// Right above
		if (c < 3) {
			Tile rightAbove = tiles[r][c+1];
			neighbours.add(rightAbove);
			
		} else if (r > 0 && c >= 3) {
			Tile rightAbove = tiles[r-1][c+1];
			neighbours.add(rightAbove);
		} 
		
		// Right below
		if (c < 6) {
			int rBelowOffset = c < 3 ? 0 : 1;
			Tile rightBelow = tiles[r+1-rBelowOffset][c+1];
			neighbours.add(rightBelow);
		}
		
		// Columns from left -> center
		if (c > 0 && c <= 3) {
			
			if (r > 0) {
				Tile leftAbove = tiles[r-1][c-1];
				neighbours.add(leftAbove);
			}
			
			Tile leftBelow = tiles[r][c-1];
			neighbours.add(leftBelow);
		}
		
		// Columns from center+1 -> right
		if (c > 3) {
			
			Tile leftAbove = tiles[r][c-1];
			Tile leftBelow = tiles[r+1][c-1];
			
			neighbours.add(leftAbove);
			neighbours.add(leftBelow);
		}
		
		if (r < 6) {
			Tile below = tiles[r+1][c];
			neighbours.add(below);
		}
		
		if (r > 0) {
			Tile above = tiles[r-1][c];
			neighbours.add(above);
		}
		
		neighbours.removeAll(Collections.singleton(null));
		
		return neighbours;
		
	}
	
	public boolean movementPossible(Player player) {
		
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				
				if (tile == null) {
					continue;
				}
				
				List<Thing> playerThings = tile.getThings().get(player);
				
				if (playerThings == null) {
					continue;
				}
					
				for (Thing t : playerThings) {
					if (!((Creature) t).getMovementEnded()) {
						return true;
					}
				}
				
			}
		}
		
		return false;
		
	}
	
	public boolean moveThingsToUnexploredTile(int roll, Tile beginTile, Tile endTile, List<Thing> things) {
		
		Tile boardBeginTile = getTile(beginTile);
		Tile boardEndTile = getTile(endTile);
		
		if (roll != 1 && roll != 6) {
			LOGGER.log(LogLevel.STATUS, "Movement to an unexplored tile has resulted in combat! (TODO)");
			return false;
		}
		
		Player player = game.getActivePlayer();
		
		boolean success = boardEndTile.addThings(player, things);
		if (success) {

			boardBeginTile.removeThings(player, things);
			boardEndTile.setOwner(player);
			endMovement(things);
			
		}
		
		return success;
		
	}
	
	public boolean moveThings(Tile beginTile, Tile endTile, List<Thing> things) {
		
		boolean success = false;
	
		Tile boardBeginTile = getTile(beginTile);
		Tile boardEndTile = getTile(endTile);
		
		Player player = game.getActivePlayer();
		
		if (!boardEndTile.getOwner().equals(player)) {
			
			String message = "You are moving things to an enemy tile. ";
			
			// If the tile has enemy things with combat values, movement is stopped
			if (boardEndTile.hasThingsWithCombatValue()) {
				
				message += " The enemy tile contains Things with combat values. Your Things are pinned.";
				
				success =  boardEndTile.addThings(player, things);
				if (success) {
					
					boardBeginTile.removeThings(player, things);
					endMovement(things);
					
				}
			
			// Otherwise, the hex is conquered 
			} else {
				
				message += "The tile is conquered.";
				
				success =  boardEndTile.addThings(player, things);
				if (success) {
					
					boardBeginTile.removeThings(player, things);
					endMovement(things);
					
				}
				
				boardEndTile.setOwner(player);
				
			}
			
			LOGGER.log(LogLevel.STATUS, message);
			
			return success;
			
		}
		
		success =  boardEndTile.addThings(player, things);
		if (success) {
			
			boardBeginTile.removeThings(player, things);
			decrementMovement(things);
			
		}
		
		return success;		
		
	}
	
	@Override
	public boolean placeSpecialIncome(SpecialIncome specialIncome, Tile tile) {

		Tile boardTile = getTile(tile);
		Player player = game.getActivePlayer();
		
		if (boardTile.getTerrainType() != specialIncome.getTerrainType()) {
			LOGGER.log(LogLevel.STATUS, "Cannot place special income counter in a tile with different terrain type.");
			return false;
		}
		
		return player.placeSpecialIncome(specialIncome, boardTile);
		
	}
	
	@Override
	public boolean addThingsToTile(Tile tile, List<Thing> things) {

		Tile boardTile = getTile(tile);
		Player player = game.getActivePlayer();
		
		if (!boardTile.getOwner().equals(player)) {
			LOGGER.warning("Cannot add Things to a tile the player does not own.");
			return false;
		}
		
		boolean success = boardTile.addThings(player, things);
		if (success) {
			player.getRack().removeThings(things);
		}
		
		return success;	
		
	}
	
	public boolean setTileControl(Tile tile, boolean initial) {
		
		Player player = game.getActivePlayer();
		Tile modelTile = getTile(tile);
		
		if (modelTile == null) {
			LOGGER.warning("Board tile not found");
			return false;
		}
		
		if (initial) {
			return setInitialControlTile(modelTile, player);
		}
		
		modelTile.setOwner(player);
		return true;
		
	}
	
	@Override
	public boolean placeFort(Fort fort, Tile tile) {
		
		Tile boardTile = getTile(tile);
		Player player = game.getActivePlayer();

		boolean success = player.placeFort(fort, boardTile);
		if (success) {
			game.getPhaseManager().endPlayerTurn();
		}
		
		return success;
		
	}
	
	@Override
	public boolean buildFort(Tile tile) {
		
		Player player = game.getActivePlayer();
		Tile modelTile = getTile(tile);
		
		if (!modelTile.getOwner().equals(player)) {
			LOGGER.log(LogLevel.STATUS, "Cannot build a fort in a tile the player does not own.");
			return false;
		}
		
		if (modelTile.getFort() != null) {
			LOGGER.log(LogLevel.STATUS, "Tile already contains a fort.");
			return false;
		}
		
		int gold = player.getNumGold();
		if (gold < 5) {
			LOGGER.log(LogLevel.STATUS, "5 gold is required to build a fort.");
			return false;
		}
		
		Fort tower = FortFactory.getFort(Fort.Type.TOWER, true, false);
		tower.setUpgraded(true);
		
		player.addFort(tower);
		player.removeGold(ConstructionPhase.BUILD_FORT_COST);
		
		modelTile.setFort(tower);
		
		return true;
		
	}

	@Override
	public boolean upgradeFort(Tile tile) {

		Player player = game.getActivePlayer();
		Tile modelTile = getTile(tile);
		
		if (!modelTile.getOwner().equals(player)) {
			LOGGER.log(LogLevel.STATUS, "Cannot upgrade a fort in a tile the player does not own.");
			return false;
		}
		
		if (modelTile.getFort() == null) {
			LOGGER.log(LogLevel.STATUS, "Tile does not contain a fort.");
			return false;
		}
		
		if (modelTile.getFort().getUpgraded()) {
			LOGGER.log(LogLevel.STATUS, "Only one upgrade per fort per turn.");
			return false;
		}
		
		int gold = player.getNumGold();
		if (gold < 5) {
			LOGGER.log(LogLevel.STATUS, "5 gold is required to upgrade a fort.");
			return false;
		}
		
		Fort oldFort = modelTile.getFort();
		Fort fort = FortFactory.getUpgradedFort(modelTile.getFort());
		
		if (fort.getType() == Fort.Type.CITADEL && player.hasCitadel()) {
			LOGGER.log(LogLevel.STATUS, "A player can only build one citadel.");
			return false;
		}
		
		fort.setPlaced(true);
		
		player.addFort(fort);
		player.removeFort(oldFort);
		player.removeGold(ConstructionPhase.UPGRADE_FORT_COST);
		
		modelTile.setFort(fort);
		
		return false;
		
	}
	
	public Tile getTile(Tile tile) {
		
		for (Tile[] row : tiles) {
			for (Tile t : row) {
				
				if (tile.equals(t)) {
					return t;
				}
				
			}
		}
		
		return null;
		
	}
	
	private void decrementMovement(List<Thing> things) {
		
		for (Thing thing : things) {
			Creature c = (Creature) thing;
			c.setMovesLeft(c.getMovesLeft() - 1);
			
			if (c.getMovesLeft() == 0) {
				c.setMovementEnded(true);
			}
		}
		
	}
	
	private void endMovement(List<Thing> things) {
		
		for (Thing thing : things) {
			Creature c = (Creature) thing;
			c .setMovementEnded(true);
		}
		
	}
	
	/*
	 * Sets an initial control marker.
	 * 
	 * Restrictions:
	 * - the player has not placed more than the maximum number of initial control markers
	 * - the tile must be adjacent to a previous tile owned by the player
	 * - the tile may not be adjacent to a tile owned by another player
	 */
	private boolean setInitialControlTile(Tile tile, Player player) { 
		
		int numControlled = player.getNumControlledTiles();
		if (numControlled >= NUM_INITIAL_TILES) {
			LOGGER.log(LogLevel.STATUS, "Only " + NUM_INITIAL_TILES + " control markers can be placed in the 'Starting Kingdoms' phase.");
			return false;
		}
		
		if (tile.getOwner() == player) {
			LOGGER.log(LogLevel.STATUS, "Tile is already owned by the player.");
			return false;
		}
		
		if (tile.getOwner() != null && !tile.getOwner().equals(player)) {
			LOGGER.log(LogLevel.STATUS, "Tile is owned by another player.");
			return false;
		}
		
		List<Tile> neighbours = getNeighbours(tiles, tile);
		
		boolean playerNeighbour = false;
		boolean enemyNeighbour = false;
		
		for (Tile neighbour : neighbours) {
			
			Player owner = neighbour.getOwner();
			
			if (owner != null && owner.equals(player)) {
				playerNeighbour = true;
			} else if (owner != null) {
				enemyNeighbour = true;
			}
			
		}
		
		if (playerNeighbour && !enemyNeighbour) {
			
			if (numControlled++ < NUM_INITIAL_TILES) {
				tile.setOwner(player);
				game.getPhaseManager().endPlayerTurn();
			}
					
			return true;
			
		}
			
		LOGGER.log(LogLevel.STATUS, "Invalid tile. Player must own at least one adjacent tile, and no enemies can own adjacent tiles.");
		return false;
		
	}
	
	public void setStartingTile(Player player, int position) {
		
		switch(position) {
		
			case 1:
				tiles[0][5].setOwner(player);
				break;
		
			case 2:
				tiles[4][5].setOwner(player);
				break;
		
			case 3:
				tiles[4][1].setOwner(player);
				break;
		
			case 4:
				tiles[0][1].setOwner(player);
				break;
				
		}
		
	}
	
	private Tile[][] generateTiles(int size) {

		Tile[][] tiles = new Tile[size][size];
		
		// column 0
		tiles[0][0] = new Tile(Terrain.PLAINS);
		tiles[1][0] = new Tile(Terrain.FOREST);
		tiles[2][0] = new Tile(Terrain.SWAMP);
		tiles[3][0] = new Tile(Terrain.SEA);
		
		// column 1
		tiles[0][1] = new Tile(Terrain.SWAMP);
		tiles[1][1] = new Tile(Terrain.FROZEN_WASTE);
		tiles[2][1] = new Tile(Terrain.MOUNTAIN);
		tiles[3][1] = new Tile(Terrain.SEA);
		tiles[4][1] = new Tile(Terrain.SWAMP);
		
		// column 3
		tiles[0][2] = new Tile(Terrain.PLAINS);
		tiles[1][2] = new Tile(Terrain.DESERT);
		tiles[2][2] = new Tile(Terrain.MOUNTAIN);
		tiles[3][2] = new Tile(Terrain.FOREST);
		tiles[4][2] = new Tile(Terrain.PLAINS);
		tiles[5][2] = new Tile(Terrain.FOREST);
		
		// column 4 (center)
		tiles[0][3] = new Tile(Terrain.SEA);
		tiles[1][3] = new Tile(Terrain.SWAMP);
		tiles[2][3] = new Tile(Terrain.SEA);
		tiles[3][3] = new Tile(Terrain.SWAMP);
		tiles[4][3] = new Tile(Terrain.DESERT);
		tiles[5][3] = new Tile(Terrain.MOUNTAIN);
		tiles[6][3] = new Tile(Terrain.MOUNTAIN);
		
		// column 5
		tiles[0][4] = new Tile(Terrain.JUNGLE);
		tiles[1][4] = new Tile(Terrain.MOUNTAIN);
		tiles[2][4] = new Tile(Terrain.PLAINS);
		tiles[3][4] = new Tile(Terrain.FROZEN_WASTE);
		tiles[4][4] = new Tile(Terrain.JUNGLE);
		tiles[5][4] = new Tile(Terrain.FROZEN_WASTE);

		// column 6
		tiles[0][5] = new Tile(Terrain.FROZEN_WASTE);
		tiles[1][5] = new Tile(Terrain.FOREST);
		tiles[2][5] = new Tile(Terrain.DESERT);
		tiles[3][5] = new Tile(Terrain.FOREST);
		tiles[4][5] = new Tile(Terrain.DESERT);
		
		// column 7
		tiles[0][6] = new Tile(Terrain.PLAINS);
		tiles[1][6] = new Tile(Terrain.FROZEN_WASTE);
		tiles[2][6] = new Tile(Terrain.SWAMP);
		tiles[3][6] = new Tile(Terrain.DESERT);
		
		// Set the neighbours
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				
				if (tile != null) {
					tile.setNeighbours(getNeighbours(tiles, tile));
				}
				
			}
			
		}
		
		return tiles;
		
	}
	
	private void setNeighbours() {
		
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				
				if (tile != null) {
					tile.setNeighbours(getNeighbours(tiles, tile));
				}
				
			}
			
		}
		
	}
	
}
