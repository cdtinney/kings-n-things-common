package com.kingsandthings.game.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.input.MouseEvent;

public class EventPropogationHandler implements EventHandler<MouseEvent> {
	
	private EventTarget target;
	
	public EventPropogationHandler(EventTarget target) {
		this.target = target;
	}

	@Override
	public void handle(MouseEvent event) {
		Event.fireEvent(target, event);
	}
	
}