package com.kingsandthings.common.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javafx.scene.image.Image;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.kingsandthings.common.model.Cup;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.model.IGame;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.PlayerManager;
import com.kingsandthings.common.model.Rack;
import com.kingsandthings.common.model.board.Board;
import com.kingsandthings.common.model.board.IBoard;
import com.kingsandthings.common.model.board.Tile;
import com.kingsandthings.common.model.enums.Terrain;
import com.kingsandthings.common.model.phase.ChangingPlayerOrderPhase;
import com.kingsandthings.common.model.phase.ConstructionPhase;
import com.kingsandthings.common.model.phase.GoldCollectionPhase;
import com.kingsandthings.common.model.phase.InitialRecruitmentPhase;
import com.kingsandthings.common.model.phase.MovementPhase;
import com.kingsandthings.common.model.phase.Phase;
import com.kingsandthings.common.model.phase.PhaseManager;
import com.kingsandthings.common.model.phase.RandomEventsPhase;
import com.kingsandthings.common.model.phase.RecruitCharactersPhase;
import com.kingsandthings.common.model.phase.SpecialPowersPhase;
import com.kingsandthings.common.model.phase.StartingKingdomsPhase;
import com.kingsandthings.common.model.phase.ThingRecruitmentPhase;
import com.kingsandthings.common.model.phase.TowerPlacementPhase;
import com.kingsandthings.common.model.things.Creature;
import com.kingsandthings.common.model.things.Creature.Ability;
import com.kingsandthings.common.model.things.Fort;

public class NetworkRegistry {
	
	static public final int GAME_ID = 42;
	static public final int BOARD_ID = 43;
	
    static public void registerClasses (EndPoint endPoint) {
    	
	    Kryo kryo = endPoint.getKryo();

	    // Property change dispatcher wrapper
	    kryo.register(PropertyChange.class);
	    
	    // Network message classes
	    kryo.register(RegisterPlayer.class);
	    kryo.register(InitializeGame.class);
	    kryo.register(UpdateGame.class);
	    kryo.register(Instruction.class);
	    kryo.register(NetworkPlayerStatus.class);
	    
	    // Model classes
	    registerClasses(kryo, NetworkRegistry.getGameClasses());	   
	    registerClasses(kryo, NetworkRegistry.getPhaseClasses());	   
	    
	    // RMI
	    ObjectSpace.registerClasses(kryo);
	    
	}
    
    static public void registerRMIObject(ObjectSpace objSpace, int id, Object object) {
    	objSpace.register(id, object);
    }
    
    static public void addRMIConnection(ObjectSpace objSpace, Connection c) {
    	objSpace.addConnection(c);
    }
    
    static private List<Class<?>> getGameClasses() {

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		// RMI
		classes.add(IGame.class);
		classes.add(IBoard.class);
		
		classes.add(Game.class);
		
		classes.add(PlayerManager.class);
		classes.add(Player.class);
		classes.add(Rack.class);
		
		classes.add(PhaseManager.class);
		classes.add(Phase.class);
		
		classes.add(Cup.class);
		classes.add(Creature.class);
		classes.add(Ability.class);
		classes.add(Terrain.class);

		classes.add(Board.class);
		
		classes.add(Tile.class);
		classes.add(Tile[][].class);
		classes.add(Tile[].class);
		
		classes.add(Fort.class);
		classes.add(Fort.Type.class);
	    
		// JDK classes
		classes.add(ArrayList.class);
		classes.add(HashMap.class);
		classes.add(LinkedHashMap.class);
	    
		// Java FX
		classes.add(Image.class);
		
		return classes;
    	
    }
	
    static private List<Class<?>> getPhaseClasses() {
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		classes.add(StartingKingdomsPhase.class);
		classes.add(TowerPlacementPhase.class);
		classes.add(InitialRecruitmentPhase.class);
		
		classes.add(GoldCollectionPhase.class);
		classes.add(RecruitCharactersPhase.class);
		classes.add(ThingRecruitmentPhase.class);
		classes.add(RandomEventsPhase.class);
		classes.add(MovementPhase.class);
		classes.add(ConstructionPhase.class);
		classes.add(SpecialPowersPhase.class);
		classes.add(ChangingPlayerOrderPhase.class);
	
		return classes;
		
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
	
	static public class PlayerConnection extends Connection {
		public String name;
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
    
    static public class Instruction {
    	
    	public String text;
    	
    	public Instruction() { }
    	
    	public Instruction(String text) {
    		this.text = text;
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
	
	static public class UpdateGame {

		public Game game;
		
		public UpdateGame() { }
		
		public UpdateGame(Game game) {
			this.game = game;
		}
		
	}
    
}
