package com.kingsandthings.common.network;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.image.Image;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.kingsandthings.common.model.Cup;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;

public class NetworkRegistry {
	
    static public void register (EndPoint endPoint) {
	    Kryo kryo = endPoint.getKryo();
	    
	    kryo.register(RegisterPlayer.class);
	    kryo.register(ConnectionStatus.class);
	    
	    registerGameClasses(kryo);
	    
	}
	
	static public class RegisterPlayer {
	    public String name;
	}
	
	static public class InitializeGame {
		public Game game;
	}
	
	static public enum ConnectionStatus {
		ALL_PLAYERS_CONNECTED,
		ALL_PLAYERS_NOT_CONNECTED,
		PLAYER_DISCONNECTED,
		PLAYER_CONNECTED
	}
    
    static private void registerGameClasses(Kryo kryo) {
	    
	    kryo.register(InitializeGame.class);
	    kryo.register(Game.class);
	    kryo.register(PlayerManager.class);
	    kryo.register(PhaseManager.class);
	    kryo.register(Cup.class);
	    kryo.register(Board.class);
	    kryo.register(Tile.class);
	    kryo.register(Tile[][].class);
	    kryo.register(Tile[].class);
	    kryo.register(Player.class);
	    kryo.register(Creature.class);
	    kryo.register(Ability.class);
	    kryo.register(Terrain.class);
	    
	    kryo.register(ArrayList.class);
	    kryo.register(HashMap.class);
	    
	    kryo.register(Image.class);
	    
	    
    	    	
    }

}
