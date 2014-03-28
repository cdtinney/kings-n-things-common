package com.kingsandthings.common.network;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.ObjectSpace.InvokeMethod;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.network.NetworkRegistry.InitializeGame;
import com.kingsandthings.common.network.NetworkRegistry.NetworkPlayerStatus;
import com.kingsandthings.common.network.NetworkRegistry.PlayerConnection;
import com.kingsandthings.common.network.NetworkRegistry.RegisterPlayer;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.logging.LogLevel;

public class GameServer  {
	
	private static Logger LOGGER = Logger.getLogger(GameServer.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;		// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 	// milliseconds
	
	// Networking
	private final ObjectSpace objectSpace = new ObjectSpace();
	private Server server;
	
	// Model
	private Game game;
	
	private int numPlayers;										// # of players that will be connecting
	private int numConnected;									// # of players currently connected
	private int numInitialized = 0;								// # of players who have initialized a game (i.e. view displayed)
	private boolean allPlayersConnected = false;				// indicates whether all players have connected
	private Map<String, PlayerConnection> connectedPlayers;		// connected players
	
	public GameServer(int numPlayers) {
		this.numPlayers = numPlayers;
		
		game = new Game();
		game.setNumPlayers(numPlayers);
		
		connectedPlayers = new HashMap<String, PlayerConnection>();
		
	}
	
	public void start(int port) {
		
		if (server == null) {
			
			server = new Server() {
				
				@Override
				protected Connection newConnection () {
	                return new PlayerConnection();
				}
				
			};
			
			server.start();
			server.addListener(new GameServerListener());

			NetworkRegistry.registerClasses(server);
			NetworkRegistry.registerRMIObject(objectSpace, NetworkRegistry.GAME_ID, game);
			
			PropertyChangeDispatcher.getInstance().setServer(this);
			PropertyChangeDispatcher.getInstance().setNetworkSend(true);
			
		}
		
		bind(port);

	}

	public void end() {
		server.stop();
	}
	
	public void sendAll(Object object) {
		server.sendToAllTCP(object);
	}
	
	public void sendToPlayer(String playerName, Object object) {
		
		PlayerConnection c = connectedPlayers.get(playerName);
		if (c == null) {
			return;
		}
		
		c.sendTCP(object);
		
	}
	
	public List<String> getConnectedPlayerNames() {
		return new ArrayList<String>(connectedPlayers.keySet());
	}
	
	public int getNumConnected() {
		return numConnected;
	}
	
	public int getNumRemaining() {
		return numPlayers - numConnected;
	}
	
	private void onAllPlayersConnected() {

		allPlayersConnected = true;
		server.sendToAllTCP(NetworkPlayerStatus.ALL_PLAYERS_CONNECTED);
		
		LOGGER.info("All players connected. Initializing game.");
		
		game.initalize();
    	server.sendToAllTCP(new InitializeGame(game));
		
	}
	
	private void onAllPlayersInitialized() {
		
		game.start();
		LOGGER.log(LogLevel.DEBUG, "Game started");
		
	}
	
	private void handleStatus(NetworkPlayerStatus status) {
		
		LOGGER.log(LogLevel.DEBUG, "Status received - " + status);
		
		if (status == NetworkPlayerStatus.PLAYER_INITIALIZED) {
			numInitialized++;
			
			if (numInitialized == numConnected) {
				onAllPlayersInitialized();
			}
			
		}
		
	}
	
	private void handleRegisterPlayer(PlayerConnection c, RegisterPlayer registerPlayer) {
		
		boolean valid = isValidPlayerConnection(c, registerPlayer);
		if (!valid) {
			c.close();
			return;
		}
		
		addConnectedPlayer(registerPlayer.name, c);
		
	}
	
	private boolean isValidPlayerConnection(PlayerConnection c, RegisterPlayer registerPlayer) {

		// Invalid if all players have already connected
		if (allPlayersConnected) {
			LOGGER.warning("Player cannot connect - all players already connected.");
			return false;
		}
		
		// Player has already been registered
		if (c.name != null) {
			LOGGER.warning("Player already registered: " + c.name);
			return false;
		}
		
		// Name is non-empty
		String name = registerPlayer.name;
		if (name == null || name.trim().length() == 0) {
			LOGGER.warning("Invalid player name: " + name);
			return false;
		}
		
		// Player names must be unique
		if (connectedPlayers.containsKey(name)) {
			LOGGER.warning("A player has already connected with name: " + name);
			return false;
		} 
		
		return true;
		
	}
	
	private void addConnectedPlayer(String name, PlayerConnection c) {

		connectedPlayers.put(c.name = name, c);
		PropertyChangeDispatcher.getInstance().notifyListeners(this, "connectedPlayers", null, connectedPlayers);
		
		game.addPlayer(name);
		numConnected++;
		
		LOGGER.info("Player connected: " + name);
		LOGGER.info(numConnected + " player(s) connected. Waiting for " + getNumRemaining() + " more player(s).");
		
		if (connectedPlayers.keySet().size() == numPlayers) {
			onAllPlayersConnected();
		}
		
	}
	
	private void removeConnectedPlayer(String name) {
		
		allPlayersConnected = false;
	
		connectedPlayers.remove(name);
		game.removePlayer(name);
		numConnected--;
		
		LOGGER.info("Player disconnected: " + name);
		LOGGER.info(numConnected + " player(s) connected. Waiting for " + getNumRemaining() + " more player(s).");

		PropertyChangeDispatcher.getInstance().notifyListeners(this, "connectedPlayers", null, connectedPlayers);
		
	}
	
	private boolean bind(final int port) {
		
		// Run on a new thread so the UI isn't blocked
		new Thread() {
			
			public void run() {
				
				int currentAttempt = 0;
				while (currentAttempt < MAX_ATTEMPTS) {
					
					LOGGER.log(LogLevel.INFO, "Attempting to bind game server to port " + port);
					
					try {
						server.bind(port);
						
						LOGGER.log(LogLevel.INFO, "Game server successfully started on port " + port);
						LOGGER.log(LogLevel.INFO, "Waiting for " + numPlayers + " players to connect...");
						
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
	
	private class GameServerListener extends Listener {
		
		@Override
		public void connected(Connection c) {
			
			NetworkRegistry.addRMIConnection(objectSpace, c);
			
		}
		
		@Override
		public void received(Connection c, Object object) {
			
			// Ignore keep alive messages and RMI method invocation
			if (object instanceof KeepAlive || object instanceof InvokeMethod) {
				return;
			}
			
			PlayerConnection connection = (PlayerConnection) c;
			
			if (object instanceof RegisterPlayer) {
				handleRegisterPlayer(connection, (RegisterPlayer) object);
				return;
			}
			
			if (object instanceof NetworkPlayerStatus) {
				handleStatus((NetworkPlayerStatus) object);
				return;
			}
			
			LOGGER.log(LogLevel.DEBUG, object.toString());
			
		}
		
		@Override
		public void disconnected(Connection c) {
			
			PlayerConnection connection = (PlayerConnection) c;
			if (connection.name == null) {
				return;
			}
			
			removeConnectedPlayer(connection.name);
			
			// Notify clients	
			server.sendToAllTCP(NetworkPlayerStatus.ALL_PLAYERS_NOT_CONNECTED);
			
		}
		
	}
	
}
