package com.kingsandthings.common.network;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Platform;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.ObjectSpace.InvokeMethodResult;
import com.kingsandthings.common.model.IGame;
import com.kingsandthings.common.model.Player;
import com.kingsandthings.common.model.board.IBoard;
import com.kingsandthings.common.network.NetworkRegistry.PropertyChange;
import com.kingsandthings.common.network.NetworkRegistry.RegisterPlayer;
import com.kingsandthings.game.events.PropertyChangeDispatcher;
import com.kingsandthings.logging.LogLevel;

public class GameClient {
	
	private static Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;			// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 		// milliseconds
	private final int CONNECTION_TIMEOUT = 5000;	// milliseconds
	
	// Networking
	private Client client;
	private boolean connected = false;
	
	private String name;
	
	private NetworkObjectHandler objectHandler;
	
	public GameClient(String name) { 
		this.name = name;
	}
	
	public void start(String ip, int port) {
		
		if (client == null) {
			
			client = new Client(8192, 8192);
			client.start();
			client.addListener(new ClientListener());
			
			NetworkRegistry.registerClasses(client);
			
		}
		
		connect(ip, port);

	}

	public void end() {
		client.stop();
	}
	
	public String getName() {
		return name;
	}
	
	public void setHandler(NetworkObjectHandler handler) {
		objectHandler = handler;
	}
	
	public void send(Object object) {
		client.sendTCP(object);
	}
	
	public IGame requestGame() {
		return ObjectSpace.getRemoteObject ((Connection) client, NetworkRegistry.GAME_ID, IGame.class);
	}
	
	public IBoard requestBoard() {
		return ObjectSpace.getRemoteObject ((Connection) client, NetworkRegistry.BOARD_ID, IBoard.class);
	}
	
	public boolean activePlayer() {
		
		IGame game =  ObjectSpace.getRemoteObject ((Connection) client, NetworkRegistry.GAME_ID, IGame.class);
		Player player = game.getActivePlayer();
		return player.getName().equals(name);
		
	}
	
	private void connect(final String ip, final int port) {
		
		// connect on a new thread so the UI isn't blocked (i.e. frozen)
		new Thread() {
			
			public void run() {
				
				int currentAttempt = 0;
				while (currentAttempt < MAX_ATTEMPTS) {
					
					LOGGER.log(LogLevel.DEBUG, "Attempting to connect client to " + ip + ":" + port);
					
					try {
						client.connect(CONNECTION_TIMEOUT, ip, port);
						
						LOGGER.log(LogLevel.INFO, "Game client successfully connected to " + ip + ":" + port);
						break;
						
					} catch (IOException e) {
						LOGGER.log(LogLevel.DEBUG, "No server found: " + ip + ":" + port);
						
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
		
	}
	
	private void handlePropertyChange(final PropertyChange propertyChange) {
		
		// Run on JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				PropertyChangeDispatcher.getInstance().notify(propertyChange.source, propertyChange.property, 
						propertyChange.oldValue, propertyChange.newValue);
				
			}
		
		});
		
	}
	
	private class ClientListener extends Listener {
		
		@Override
		public void connected(final Connection connection) {
			
		    RegisterPlayer registerPlayer = new RegisterPlayer(name);
		    client.sendTCP(registerPlayer);

			PropertyChangeDispatcher.getInstance().notify(GameClient.this, "connected", connected, connected = true);
		
		}
		
		@Override
		public void received(Connection connection, Object object) {
			
			// Ignore keep alive messages and RMI method return
			if (object instanceof KeepAlive || object instanceof InvokeMethodResult) {
				return;
			}
			
			if (object instanceof PropertyChange) {
				handlePropertyChange((PropertyChange) object);
				return;
			}
			
			if (objectHandler == null) {
				LOGGER.log(LogLevel.DEBUG, "No handler set - " + object);
				return;
			}
			
			objectHandler.handleObject(object);
			
		}

		@Override
		public void disconnected(Connection connection) {
			
			PropertyChangeDispatcher.getInstance().notify(GameClient.this, "connected", connected, connected = false);
			
		}
		
	}

}
