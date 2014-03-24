package com.kingsandthings.common.network;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kingsandthings.common.model.Game;
import com.kingsandthings.common.network.NetworkRegistry.ConnectionStatus;
import com.kingsandthings.common.network.NetworkRegistry.InitializeGame;
import com.kingsandthings.common.network.NetworkRegistry.RegisterPlayer;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.logging.LogLevel;

public class GameServer  {
	
	private static Logger LOGGER = Logger.getLogger(GameServer.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;		// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 	// milliseconds
	
	private Server server;
	private Game game;
	
	private int numPlayers;										// # of players that will be connecting
	private boolean allPlayersConnected = false;				// indicates whether all players have connected
	private Map<String, PlayerConnection> connectedPlayers;		// connected players
	
	public GameServer(int numPlayers) {
		this.numPlayers = numPlayers;
		
		game = new Game();
		game.setNumPlayers(numPlayers);
		game.generateBoard();
		
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
			addListener();
			
		}

		NetworkRegistry.register(server);
		
		bind(port);

	}

	public void end() {
		server.stop();
	}

	public void updateClients() {
		
		// Send an updated game state object to all clients, force refresh/update
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
				
				System.out.println(object);
				
			}
			
			@Override
			public void disconnected(Connection c) {
				
				PlayerConnection connection = (PlayerConnection) c;
				if (connection.name == null) {
					return;
				}
				
				removeConnectedPlayer(connection.name);
				
				// Notify clients
				server.sendToAllTCP(ConnectionStatus.ALL_PLAYERS_NOT_CONNECTED);
				
			}
			
		});
		
	}
	
	private void registerPlayerConnection(PlayerConnection c, Object object) {
		
		// Ignore connections if all players have already connected
		if (allPlayersConnected) {
			LOGGER.warning("Player cannot connect - all players already connected.");
			c.close();
			return;
		}
		
		// Player has already been registered
		if (c.name != null) {
			LOGGER.warning("Player already registered: " + c.name);
			c.close();
			return;
		}
		
		// Check if the name is valid
		String name = ((RegisterPlayer) object).name;
		if (name == null || name.trim().length() == 0) {
			LOGGER.warning("Invalid player name: " + name);
			c.close();
			return;
		}
		
		// Player names must be unique
		if (connectedPlayers.containsKey(name)) {
			LOGGER.warning("A player has already connected with name: " + name);
			c.close();
			return;
		} 
		
		addConnectedPlayer(name, c);
		
		// Send a status message to all clients if all players have connected
		if (connectedPlayers.keySet().size() == numPlayers) {
			allPlayersConnected = true;
			server.sendToAllTCP(ConnectionStatus.ALL_PLAYERS_CONNECTED);
			LOGGER.info("All players connected. Starting game in 5 seconds...");
			
			new Timer().schedule(new TimerTask() {
			    public void run() {
			    	sendInitializeGame();
					LOGGER.info("Game started!");
			    }
			}, 5000);
			
		}
		
	}
	
	private void sendInitializeGame() {
		    	
    	InitializeGame initialize = new InitializeGame();
    	initialize.game = this.game;
    	server.sendToAllTCP(initialize);
		
	}
	
	private void addConnectedPlayer(String name, PlayerConnection c) {

		connectedPlayers.put(c.name = name, c);
		game.addPlayer(name);
		
		LOGGER.info("Player connected: " + name);
		LOGGER.info(numPlayersConnected() + " player(s) connected. Waiting for " + numPlayersRemaining() + " more player(s).");
		
		PropertyChangeDispatcher.getInstance().notify(this, "connectedPlayers", null, connectedPlayers);
		
	}
	
	private void removeConnectedPlayer(String name) {
		
		allPlayersConnected = false;
	
		connectedPlayers.remove(name);
		game.removePlayer(name);
		
		LOGGER.info("Player disconnected: " + name);
		LOGGER.info(numPlayersConnected() + " player(s) connected. Waiting for " + numPlayersRemaining() + " more player(s).");

		PropertyChangeDispatcher.getInstance().notify(this, "connectedPlayers", null, connectedPlayers);
		
	}
	
	static private class PlayerConnection extends Connection {
		public String name;
	}
	
}
