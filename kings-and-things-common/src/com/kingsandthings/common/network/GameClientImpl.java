package com.kingsandthings.common.network;

import java.io.IOException;
import java.util.logging.Logger;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kingsandthings.common.network.GameNetwork.RegisterPlayer;
import com.kingsandthings.logging.LogLevel;

public class GameClientImpl implements GameClient {
	
	private final Object connectedLock = new Object();
	
	// Logger
	private static Logger LOGGER = Logger.getLogger(GameClientImpl.class.getName());
	
	// Constants
	private final int MAX_ATTEMPTS = 20;			// # of max connection attempts
	private final int ATTEMPT_TIMEOUT = 5000; 		// milliseconds
	private final int CONNECTION_TIMEOUT = 5000;	// milliseconds
	
	// Client instance
	private Client client;
	
	public GameClientImpl(String ip, int port) {
		start(ip, port);
	}

	@Override
	public void start(String ip, int port) {
		
		if (client == null) {
			client = new Client();
			client.start();
			addListener();
		}
		
		GameNetwork.register(client);
		
		boolean success = connect(ip, port);
		
	    SomeRequest request = new SomeRequest();
	    request.text = "Here is the request";
	    client.sendTCP(request);

	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}
	
	public Object getConnectedLock() {
		return connectedLock;
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
		
		// TODO - Make listener a static class so it can only be added once
		client.addListener(new Listener() {
			
			public void connected(Connection connection) {
				
                RegisterPlayer registerPlayer = new RegisterPlayer();
                
                // TODO - Get name from player
                registerPlayer.name = "test";
                client.sendTCP(registerPlayer);

			}
			
			public void received(Connection connection, Object object) {
				 
		          if (!(object instanceof SomeResponse)) {
					System.out.println(object);
					return;
		          }
		          
	             SomeResponse response = (SomeResponse)object;
	             System.out.println(response.text);
		          
		       }
		
		});
		
	}

}
