package com.kingsandthings.common.network;

import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.phase.PhaseManager;

public class NetworkRegistry {
	
	static public final int GAME_ID = 42;
	
    static public void registerClasses (EndPoint endPoint) {
    	
	    Kryo kryo = endPoint.getKryo();

	    // Property change dispatcher
	    kryo.register(PropertyChange.class);
	    
	    // Network message classes
	    kryo.register(RegisterPlayer.class);
	    kryo.register(InitializeGame.class);
	    kryo.register(NetworkPlayerStatus.class);
	    
	    // Model classes
	    registerClasses(kryo, Game.getMemberClasses());	   
	    registerClasses(kryo, PhaseManager.getMemberClasses());	   
	    
	    // RMI
	    ObjectSpace.registerClasses(kryo);
	    
	}
    
    static public void registerRMIObject(ObjectSpace objSpace, int id, Object object) {
    	objSpace.register(id, object);
    }
    
    static public void addRMIConnection(ObjectSpace objSpace, Connection c) {
    	objSpace.addConnection(c);
    }
    
    static private void registerClasses(Kryo kryo, List<Class<?>> classes) {
    	
    	for (Class<?> clazz : classes) {
    		kryo.register(clazz);
    	}
    	    	
    }
	
	static public enum NetworkPlayerStatus {
		ALL_PLAYERS_CONNECTED,
		ALL_PLAYERS_NOT_CONNECTED,
		PLAYER_DISCONNECTED,
		PLAYER_CONNECTED,
		PLAYER_INITIALIZED
	}
    
    static public class PropertyChange {
    	
    	public Object source;
    	public String property;
    	public Object oldValue;
    	public Object newValue;
    	
    	public PropertyChange() { }
    	
    	public PropertyChange(Object source, String property, Object oldValue, Object newValue) {
    		this.source = source;
    		this.property = property;
    		this.oldValue = oldValue;
    		this.newValue = newValue;
    	}
    	
    }
	
	static public class RegisterPlayer {
		
	    public String name;
	    
	    public RegisterPlayer() { }
	    
	    public RegisterPlayer(String name) {
	    	this.name = name;
	    }
	    
	}
	
	static public class InitializeGame {
		
		public Game game;
		
		public InitializeGame() { }
		
		public InitializeGame(Game game) {
			this.game = game;
		}
		
	}
	
	static public class PlayerConnection extends Connection {
		public String name;
	}
    
}
