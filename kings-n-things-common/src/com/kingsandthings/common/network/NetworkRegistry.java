package com.kingsandthings.common.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkRegistry {
	
    static public void register (EndPoint endPoint) {
	    Kryo kryo = endPoint.getKryo();
	    
	    kryo.register(RegisterPlayer.class);
	    kryo.register(Status.class);
	    
	}
	
	static public class RegisterPlayer {
	    public String name;
	}
	
	static public enum Status {
		ALL_PLAYERS_CONNECTED,
		PLAYER_DISCONNECTED,
		PLAYER_CONNECTED
	}

}
