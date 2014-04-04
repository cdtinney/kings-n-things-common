package com.kingsandthings.common.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.kingsandthings.common.network.GameServer;
import com.kingsandthings.common.network.NetworkRegistry.PropertyChange;

public class PropertyChangeDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(PropertyChangeDispatcher.class.getName());
	
	// Singleton
	private static PropertyChangeDispatcher INSTANCE = null;

	private Map<Class<?>, Map<String, List<PropertyChangeListener>>> listeners;
	
	private GameServer gameServer;
	private boolean networkSend = false;
	
	private PropertyChangeDispatcher() {
		listeners = new HashMap<Class<?>, Map<String, List<PropertyChangeListener>>>();
	}
	
	public static PropertyChangeDispatcher getInstance() {
		
		if (INSTANCE == null) {
			INSTANCE = new PropertyChangeDispatcher();
		}
		
		return INSTANCE;
	}
	
	public void setServer(GameServer server) {
		this.gameServer = server;
	}
	
	public void setNetworkSend(boolean networkSend) {
		this.networkSend = networkSend;
	}
	
	public void addListener(Class<?> clazz, String property, final Object instance, final String handlerMethodName) {
		addListener(clazz, property, instance, null, null, handlerMethodName);				
	}
	
	public void addListener(Class<?> clazz, String property, final Object instance, final Object data, final Class<?> dataClass, final String handlerMethodName) {
		
		getListeners(clazz, property).add(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				handleEvent(evt, instance, data, dataClass, handlerMethodName);
			}
		
		});
		
	}
	
	public void notify(Object source, String property, Object oldValue, Object newValue) {
		
		if (gameServer != null && networkSend) {
			notifyClients(source, property, oldValue, newValue);	
		}
		
		notifyListeners(source, property, oldValue, newValue);
		
	}
	
	public void notifyListeners(Object source, String property, Object oldValue, Object newValue) {

		Class<?> clazz = source.getClass();
		
		for (PropertyChangeListener listener : getListeners(clazz, property)) {
			listener.propertyChange(new PropertyChangeEvent(source, property, oldValue, newValue));
		}
		
	}
	
	private void notifyClients(final Object source, final String property, final Object oldValue, final Object newValue) {
		
		new Thread() {
			
			public void run() {
				
				// Update game state of clients
				gameServer.updateClientGameState();
				
				// Send property change events which will reflect the updated state
				PropertyChange obj = new PropertyChange(source, property, oldValue, newValue);
				gameServer.sendObject(obj);
				
			}
		
		}.start();
		
	}
	
	private void handleEvent(final PropertyChangeEvent evt, final Object instance, Object data, Class<?> dataClass, String handlerMethodName) {
		
		try {
			
			// No additional argument specified - handlerMethod(PropertyChangeEvent)
			if (data == null) {
				final Method handlerMethod = instance.getClass().getDeclaredMethod(handlerMethodName, evt.getClass());
				handlerMethod.setAccessible(true);	
				handlerMethod.invoke(instance, evt);
			
			// Additional argument specified - handlerMethod(dataClass)
			} else {
				Method handlerMethod = instance.getClass().getDeclaredMethod(handlerMethodName, dataClass);
				handlerMethod.setAccessible(true);	
				handlerMethod.invoke(instance, data);
				
			}
			
		} catch (NoSuchMethodException e) {
			LOGGER.warning("Property change handler method not found: " + instance.getClass().getSimpleName() 
			+ "." + handlerMethodName);
			
		} catch (InvocationTargetException e) {
			LOGGER.warning("Exception thrown in " + instance.getClass().getSimpleName() + "." + handlerMethodName);
			
			e.getTargetException().printStackTrace();
		
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	private List<PropertyChangeListener> getListeners(Class<?> clazz, String property) {
		
		// No listeners mapped to the class
		if (listeners.get(clazz) == null) {
			listeners.put(clazz, new HashMap<String, List<PropertyChangeListener>>());
		}
		
		// No listeners mapped to the property
		if (listeners.get(clazz).get(property) == null) {
			listeners.get(clazz).put(property, new ArrayList<PropertyChangeListener>());
		}
		
		return listeners.get(clazz).get(property);
		
	}

}
