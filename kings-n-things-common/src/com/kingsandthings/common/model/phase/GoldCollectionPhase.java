package com.kingsandthings.common.model.phase;

import java.util.logging.Logger;

import com.kingsandthings.common.logging.LogLevel;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.SpecialIncome;

public class GoldCollectionPhase extends Phase {
	
	private static Logger LOGGER = Logger.getLogger(GoldCollectionPhase.class.getName());
	
	public GoldCollectionPhase() { }

	public GoldCollectionPhase(Game game) {
		super(game, "Gold Collection", 1, false);
	}

	@Override
	public void begin() {
		super.begin();
		
		next();
	}
	
	@Override
	public void next() {
		super.next();

		setInstruction("gold collected - end turn now");
		
		Player player = game.getActivePlayer();
		
		int total = computeIncome(player);
		player.addGold(total);
		
	}
	
	private int computeIncome(Player player) {
		
		// One gold piece for each land hex controlled
		int controlledHexValue = player.getNumControlledTiles();
		
		// Add gold pieces for the combat value of controlled forts
		int fortValue = 0;
		for (Fort fort : player.getForts()) {
			if (fort.getNeutralised()) continue;
			fortValue += fort.getCombatValue();			
		}
		
		// Add gold pieces for value of special income counters 
		int specialIncomeValue = 0;
		for (SpecialIncome counter : player.getSpecialIncomeCounters()) {
			
			// Special income counters placed on the board only
			if (counter.isPlaced()) {
				specialIncomeValue += counter.getGoldValue();
			}
			
		}
		
		// One gold piece for each special character controlled
		int specialCharacterValue = player.getSpecialCharacters().size();
		
		// Add it all up
		int total = controlledHexValue + fortValue + specialIncomeValue + specialCharacterValue;
		
		String message = "Gold collected: ";
		message += "Controlled Hexes: " + controlledHexValue + ", ";
		message += "Forts: " + fortValue + ", ";
		message += "Special Income: " + specialIncomeValue + ", ";
		message += "Special Characters: " + specialCharacterValue + " ";
		message += "TOTAL: " + total;
		
		LOGGER.log(LogLevel.STATUS, message);
		
		return total;
		
	}

}
