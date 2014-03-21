package com.kingsandthings.common.network;

import java.net.BindException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.network.GameNetwork.RegisterPlayer;
import com.kingsandthings.logging.LogLevel;

public class GameServerImpl implements GameServer {
	
	// Logger
	private static Logger LOGGER = Logger.getLogger(GameServerImpl.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;		// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 	// milliseconds
	
	// Server instance
	private Server server;
	
	// Members
	private int numPlayers;							// # of players that will be connecting
	private boolean allPlayersConnected = false;	// indicates whether all players have connected
	private Map<String, PlayerConnection> players;	// connected players
	
	public GameServerImpl(int numPlayers) {
		this.numPlayers = numPlayers;
		
		players = new HashMap<String, PlayerConnection>();
	}
	
	@Override
	public void start(int port) {
		
		if (server == null) {
			
			server = new Server() {
				
				@Override
				protected Connection newConnection () {
	                return new PlayerConnection();
				}
				
			};
			
			server.start();
		}

		GameNetwork.register(server);
		
		// TODO - return success of server bind
		boolean success = bind(port);

	}

	@Override
	public void end() {
		server.close();
		server.stop();
	}

	@Override
	public void updateClients() {
		// TODO - impl update clients method in server
	}

	@Override
	public Game requestGameState() {
		// TODO - impl request game state method in server
		return null;
	}
	
	private boolean bind(final int port) {
		
		// Create a new thread so the UI isn't blocked
		new Thread() {
			
			public void run() {
				
				int currentAttempt = 0;
				while (currentAttempt < MAX_ATTEMPTS) {
					
					LOGGER.log(LogLevel.DEBUG, "Attempting to bind game server to port " + port);
					
					try {
						server.bind(port);
						addListener();
						
						LOGGER.log(LogLevel.INFO, "Game server successfully started on port " + port);
						break;
						
					} catch (BindException e) {
						LOGGER.log(LogLevel.DEBUG, "Port already bound: " + port);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						LOGGER.log(LogLevel.DEBUG, "Waiting " + (ATTEMPT_TIMEOUT / 1000) + " seconds before retrying.");
						Thread.sleep(ATTEMPT_TIMEOUT);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					currentAttempt++;
				}
				
			}
			
		}.start();
		
		return true;
		
	}
	
	private void addListener() {
		
		server.addListener(new Listener() {
			
			public void received(Connection c, Object object) {
				
				PlayerConnection connection = (PlayerConnection) c;
				
				if (object instanceof RegisterPlayer) {
					registerPlayerConnection(connection, object);
					return;
				}
				
				if (!(object instanceof SomeRequest)) {
					System.out.println(object);
					return;
				}
				
				// TODO - dispatch objects depending on type
				SomeRequest req = (SomeRequest) object;
				LOGGER.log(LogLevel.INFO, "Request received - " + req.text);
				
				SomeResponse resp = new SomeResponse();
				resp.text = "Thanks";
				connection.sendTCP(resp);
				
			}
			
		});
		
	}
	
	private void registerPlayerConnection(PlayerConnection c, Object object) {
		
		if (allPlayersConnected) {
			// TODO - close connection properly
			return;
		}
		
		// Player has already been registered
		if (c.name != null) {
			System.out.println("name already registered - " + c.name);
			return;
		}
		
		String name = ((RegisterPlayer) object).name;
		if (name == null || name.trim().length() == 0) {
			return;
		}
		
		if (players.containsKey(name)) {
			System.out.println("A player has already connected with this name - need to return error");
			return;
		} 
		
		// Set connection name and add to list of players connected
		players.put(c.name = name, c);
		
		if (players.keySet().size() == numPlayers) {
			allPlayersConnected = true;
		}
		
		System.out.println("connection name: " + name + " registered. curr connections = " + server.getConnections().length);
		
	}
	
	static private class PlayerConnection extends Connection {
		public String name;
	}
	
}
