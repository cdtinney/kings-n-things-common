package com.kingsandthings.game.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PropertyChangeDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(PropertyChangeDispatcher.class.getName());
	
	// Single instance of the class
	private static PropertyChangeDispatcher INSTANCE = null;
	
	// Maps classes to a second map which maps properties to listeners
	private Map<Class<?>, Map<String, List<PropertyChangeListener>>> listeners;
	
	private PropertyChangeDispatcher() {
		listeners = new HashMap<Class<?>, Map<String, List<PropertyChangeListener>>>();
	}
	
	public static PropertyChangeDispatcher getInstance() {
		
		if (INSTANCE == null) {
			INSTANCE = new PropertyChangeDispatcher();
		}
		
		return INSTANCE;
	}
	
	public void addListener(Class<?> clazz, String property, final Object instance, PropertyChangeListener listener) {
		getListeners(clazz, property).add(listener);
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
	
	private void handleEvent(PropertyChangeEvent evt, Object instance, Object data, Class<?> dataClass, String handlerMethodName) {
		
		try {
			
			if (data == null) {
				Method handlerMethod = instance.getClass().getDeclaredMethod(handlerMethodName, evt.getClass());
				handlerMethod.setAccessible(true);
				handlerMethod.invoke(instance, evt);
				
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
		
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	public void notify(Object source, String property, Object oldValue, Object newValue) {
		
		Class<?> clazz = source.getClass();
		
		for (PropertyChangeListener listener : getListeners(clazz, property)) {
			listener.propertyChange(new PropertyChangeEvent(source, property, oldValue, newValue));
		}
		
	}
	
	private List<PropertyChangeListener> getListeners(Class<?> clazz, String property) {
		
		if (listeners.get(clazz) == null) {
			
			// Add a new map for the class
			listeners.put(clazz, new HashMap<String, List<PropertyChangeListener>>());
			
		}
		
		if (listeners.get(clazz).get(property) == null) {
			
			// Add a new list of listeners for each property of the class
			listeners.get(clazz).put(property, new ArrayList<PropertyChangeListener>());
			
		}
		
		return listeners.get(clazz).get(property);
		
	}

}
