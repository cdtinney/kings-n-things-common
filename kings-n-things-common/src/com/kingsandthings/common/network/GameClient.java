package com.kingsandthings.common.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.kingsandthings.common.network.NetworkRegistry.RegisterPlayer;
import com.kingsandthings.logging.LogLevel;

public class GameClient {
	
	private final Object connectedLock = new Object();
	
	// Logger
	private static Logger LOGGER = Logger.getLogger(GameClient.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;			// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 		// milliseconds
	private final int CONNECTION_TIMEOUT = 5000;	// milliseconds
	
	// Client instance
	private Client client;
	
	// Members
	private String name;
	private List<NetworkObjectHandler> handlers;
	
	public GameClient(String name) { 
		this.name = name;
		
		handlers = new ArrayList<NetworkObjectHandler>();
	}
	
	public void start(String ip, int port) {
		
		if (client == null) {
			
			client = new Client();
			client.start();
			
			NetworkRegistry.register(client);
			
			addListener();
			
		}
		
		connect(ip, port);

	}

	public void end() {
		client.stop();
	}

	public List<NetworkObjectHandler> getHandlers() {
		return handlers;
	}
	
	private boolean connect(final String ip, final int port) {
		
		if (client.isConnected()) {
			return true;
		}
		
		new Thread() {
			
			public void run() {
				
				int currentAttempt = 0;
				while (currentAttempt < MAX_ATTEMPTS) {
					
					LOGGER.log(LogLevel.DEBUG, "Attempting to connect client to " + ip + ":" + port);
					
					try {
						client.connect(CONNECTION_TIMEOUT, ip, port);
						
						synchronized (connectedLock) {
							connectedLock.notify();
						}
						
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
		
		return true;
		
	}
	
	private void addListener() {
		
		if (client == null) {
			return;
		}
		
		client.addListener(new Listener() {
			
			public void connected(Connection connection) {
				
				// Send a network object to register via name upon connection
			    RegisterPlayer registerPlayer = new RegisterPlayer();
			    registerPlayer.name = name;
			    client.sendTCP(registerPlayer);
			
			}
			
			public void received(Connection connection, Object object) {
				
				// Ignore keep alive messages
				if (object instanceof KeepAlive) {
					return;
				}
				
				for (NetworkObjectHandler handler : handlers) {
					handler.handleObject(object);
				}
				
			}
		
		});
		
	}

}
