package com.kingsandthings.common.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class GameNetwork {
	
    static public void register (EndPoint endPoint) {
	    Kryo kryo = endPoint.getKryo();
	    kryo.register(RegisterPlayer.class);
	    kryo.register(SomeRequest.class);
	    kryo.register(SomeResponse.class);
	}
	
	static public class RegisterPlayer {
	    public String name;
	}

}
