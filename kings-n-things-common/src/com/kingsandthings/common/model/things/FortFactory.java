package com.kingsandthings.common.model.things;

import javafx.scene.image.Image;

import com.kingsandthings.common.model.things.Fort.Type;

public class FortFactory {

	private static final String towerImgPath = "/images/things/fort/-n Tower -a 1.jpg";
	private static final String keepImgPath = "/images/things/fort/-n Keep -a 2.jpg";
	private static final String castleImgPath = "/images/things/fort/-n Castle -a 3.jpg";
	private static final String citadelImgPath = "/images/things/fort/-n Citadel -a 4.jpg";

	private static final String nTowerImgPath = "/images/things/fort/-n Tower -s Neutralised.jpg";
	private static final String nKeepImgPath = "/images/things/fort/-n Keep -s Neutralised.jpg";
	private static final String nCastleImgPath = "/images/things/fort/-n Castle -s Neutralised.jpg";
	private static final String nCitadelImgPath = "/images/things/fort/-n Citadel -s Neutralised.jpg";
	
	public static Fort getFort(Type type, boolean placed, boolean neutralised) {

		Fort fort = null;
		
		if (type == Type.TOWER) {
			
			if (neutralised) {
				fort = new Fort(Type.TOWER, 0, new Image(nTowerImgPath));
				fort.setImagePath(nTowerImgPath);
				
			} else {
				fort = new Fort(Type.TOWER, 1, new Image(towerImgPath));
				fort.setImagePath(towerImgPath);
			}
			
		} else if (type == Type.KEEP) {
			
			if (neutralised) {
				fort = new Fort(Type.KEEP, 0, new Image(nKeepImgPath));
				fort.setImagePath(nKeepImgPath);
				
			} else {
				fort = new Fort(Type.KEEP, 2, new Image(keepImgPath));
				fort.setImagePath(keepImgPath);
				
			}
			
		} else if (type == Type.CASTLE) {
			
			if (neutralised) {
				fort = new Fort(Type.CASTLE, 0, new Image(nCastleImgPath));
				fort.setImagePath(nCastleImgPath);
				
			} else {
				fort = new Fort(Type.CASTLE, 3, new Image(castleImgPath));
				fort.setImagePath(castleImgPath);
				
			}
			
		} else if (type == Type.CITADEL) {
			
			if (neutralised) {
				fort = new Fort(Type.CITADEL, 0, new Image(nCitadelImgPath));
				fort.setImagePath(nCitadelImgPath);
				
			} else {
				fort = new Fort(Type.CITADEL, 4, new Image(citadelImgPath));
				fort.setImagePath(citadelImgPath);
				
			}
			
		}
		
		if (fort != null) {
			fort.setPlaced(placed);
			fort.setNeutralised(neutralised);
		}
		
		return fort;
		
	}
	
	public static Fort getUpgradedFort(Fort fort) {

		Type type = fort.getType();
		Fort upgradedFort = null;
		
		if (type == Type.TOWER) {
			upgradedFort = getFort(Type.KEEP, false, true);
			
		} else if (type == Type.KEEP) {
			upgradedFort = getFort(Type.CASTLE, false, true);
			
		} else if (type == Type.CASTLE) {
			upgradedFort = getFort(Type.CITADEL, false, true);
			
		}
		
		upgradedFort.setUpgraded(true);
		return upgradedFort;
		
	}

}
