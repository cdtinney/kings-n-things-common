package com.kingsandthings.common.model.phase;

import java.util.logging.Logger;

import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.things.Fort;
import com.kingsandthings.common.model.things.SpecialIncome;
import com.kingsandthings.logging.LogLevel;
import com.kingsandthings.util.Dialog;
import com.kingsandthings.util.Dialog.Type;

public class GoldCollectionPhase extends Phase {
	
	private static Logger LOGGER = Logger.getLogger(GoldCollectionPhase.class.getName());
	
	public GoldCollectionPhase() { }

	public GoldCollectionPhase(Game game) {
		super(game, "Gold Collection", true, false, 1, false);
	}

	@Override
	public void begin() {
		super.begin();
		
		next();
	}
	
	@Override
	public void next() {
		super.next();

		currentInstruction = "please collect gold";
		//BoardView.setInstructionText("please collect gold");
		
		Player player = game.getActivePlayer();
		
		int total = computeIncome(player);
		player.addGold(total);

		LOGGER.log(LogLevel.STATUS, "Player '" + player.getName() + "' collected " + total + " gold.");
		
	}
	
	private int computeIncome(Player player) {
		
		// One gold piece for each land hex controlled
		int controlledHexValue = player.getControlledTiles().size();
		
		// Add gold pieces for the combat value of controlled forts
		int fortValue = 0;
		for (Fort fort : player.getForts()) {
			fortValue += fort.getCombatValue();			
		}
		
		// Add gold pieces for value of special income counters 
		int specialIncomeValue = 0;
		for (SpecialIncome counter : player.getSpecialIncomeCounters()) {
			
			// Special income counters placed on the board only
			if (counter.placedOnBoard()) {
				specialIncomeValue += counter.getGoldValue();
			}
			
		}
		
		// One gold piece for each special character controlled
		int specialCharacterValue = player.getSpecialCharacters().size();
		
		// Add it all up
		int total = controlledHexValue + fortValue + specialIncomeValue + specialCharacterValue;
		
		String message = "Gold collected:\n";
		message += "\n\tControlled Hexes: " + controlledHexValue;
		message += "\n\tForts: " + fortValue;
		message += "\n\tSpecial Income: " + specialIncomeValue;
		message += "\n\tSpecial Characters: " + specialCharacterValue;
		message += "\n\nTotal: " + total;
		Dialog.show(Type.NOTIFY, message);
		
		return total;
		
	}

}
