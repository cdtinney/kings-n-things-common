package com.kingsandthings.game.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.kingsandthings.common.model.phase.Phase;

public class NotificationDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(NotificationDispatcher.class.getName());
	
	// Single instance of the class
	private static NotificationDispatcher INSTANCE = null;
	
	private Map<Class<? extends Phase>, Map<Phase.Notification, List<NotificationListener>>> listeners;
	
	private NotificationDispatcher() { 
		listeners = new HashMap<Class<? extends Phase>, Map<Phase.Notification, List<NotificationListener>>>();
	}
	
	public static NotificationDispatcher getInstance() {
		
		if (INSTANCE == null) {
			INSTANCE = new NotificationDispatcher();
		}
		
		return INSTANCE;
		
	}
	
	public void addListener(Class<? extends Phase> clazz, Phase.Notification type, final Object instance, final String handlerMethodName) {
		
		getListeners(clazz, type).add(new NotificationListener() {

			@Override
			public void handleNotification(Notification notification, Object handback) {
				handleNotificationEvent(notification, instance, handlerMethodName);
			}
			
		});
		
	}
	
	public void notify(Class<? extends Phase> clazz, Phase.Notification begin) {
		
		for (NotificationListener listener : getListeners(clazz, begin)) {
			listener.handleNotification(new Notification(begin.toString(), clazz, 0L), null);
		}
		
	}
	
	private void handleNotificationEvent(Notification notification, Object instance, String handlerMethodName) {
		
		try {
			
			Method handlerMethod = instance.getClass().getDeclaredMethod(handlerMethodName);
			handlerMethod.setAccessible(true);
			handlerMethod.invoke(instance);
			
		} catch (NoSuchMethodException e) {
			LOGGER.warning("Method not found: " + instance.getClass().getSimpleName() 
			+ "." + handlerMethodName + "()");
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	private List<NotificationListener> getListeners(Class<? extends Phase> clazz, Phase.Notification type) {
		
		// Add an entry for the phase class if it doesn't exist already
		if (!listeners.containsKey(clazz)) {
			listeners.put(clazz, new HashMap<Phase.Notification, List<NotificationListener>>());
			
		}
		
		// Add a map for the specific signal if it doesn't exist already
		if (!listeners.get(clazz).containsKey(type)) {
			listeners.get(clazz).put(type, new ArrayList<NotificationListener>());
			
		}
		
		return listeners.get(clazz).get(type);
		
	}

}
