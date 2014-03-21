package com.kingsandthings.common.network;

import com.kingsandthings.common.model.Game;

public interface GameServer {
	
	public void start(int port);
	public void end();
	
	public void updateClients();
	
	public Game requestGameState();

}
