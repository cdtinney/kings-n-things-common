package com.kingsandthings.common.network;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.network.NetworkRegistry.RegisterPlayer;
import com.kingsandthings.common.network.NetworkRegistry.Status;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
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
	private int numPlayers;										// # of players that will be connecting
	private boolean allPlayersConnected = false;				// indicates whether all players have connected
	private Map<String, PlayerConnection> connectedPlayers;		// connected players
	
	public GameServerImpl(int numPlayers) {
		this.numPlayers = numPlayers;
		
		connectedPlayers = new HashMap<String, PlayerConnection>();
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
			addListener();
			
		}

		NetworkRegistry.register(server);
		
		bind(port);

	}

	@Override
	public void end() {
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
	
	public List<String> connectedPlayerNames() {
		return new ArrayList<String>(connectedPlayers.keySet());
	}
	
	public int numPlayersConnected() {
		return connectedPlayers.size();
	}
	
	public int numPlayersRemaining() {
		return numPlayers - numPlayersConnected();
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
			
			@Override
			public void received(Connection c, Object object) {
				
				PlayerConnection connection = (PlayerConnection) c;
				
				if (object instanceof RegisterPlayer) {
					registerPlayerConnection(connection, object);
					return;
				}
				
				if (object instanceof FrameworkMessage.KeepAlive) {
					return;
				}

				// TODO - dispatch objects depending on type
				System.out.println(object);
				
				
			}
			
			@Override
			public void disconnected(Connection c) {
				
				// Remove the connection from the map
				connectedPlayers.remove(((PlayerConnection) c).name);
				
				// TODO - End game if a player disconnects
				
			}
			
		});
		
	}
	
	private void registerPlayerConnection(PlayerConnection c, Object object) {
		
		// Ignore connections if all players have already connected
		if (allPlayersConnected) {
			c.close();
			return;
		}
		
		// Player has already been registered
		if (c.name != null) {
			LOGGER.warning("Player already registered: " + c.name);
			return;
		}
		
		// Check if the name is valid
		String name = ((RegisterPlayer) object).name;
		if (name == null || name.trim().length() == 0) {
			LOGGER.warning("Invalid player name: " + name);
			return;
		}
		
		// Player names must be unique
		if (connectedPlayers.containsKey(name)) {
			LOGGER.warning("A player has already connected with name: " + name);
			return;
		} 
		
		addConnectedPlayer(name, c);
		
		LOGGER.log(LogLevel.DEBUG, "Player connection registered with name: " + name + 
				". Current connections: " + server.getConnections().length);
		
	}
	
	private void addConnectedPlayer(String name, PlayerConnection c) {

		connectedPlayers.put(c.name = name, c);
		
		// Check if the number of players specified have connected
		if (connectedPlayers.keySet().size() == numPlayers) {
			allPlayersConnected = true;
			server.sendToAllTCP(Status.ALL_PLAYERS_CONNECTED);
		}
		
		PropertyChangeDispatcher.getInstance().notify(this, "connectedPlayers", null, connectedPlayers);
		
	}
	
	static private class PlayerConnection extends Connection {
		public String name;
	}
	
}
