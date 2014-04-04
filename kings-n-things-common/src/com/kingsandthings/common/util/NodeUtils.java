package com.kingsandthings.common.util;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * @author Colin Tinney
 * 
 * Utility class providing support functions for JavaFX nodes.
 * 
 */
public class NodeUtils {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(NodeUtils.class.getName());
	
	
	/** 
	 * Recursive JavaFX element lookup.
	 * @param parent	The parent element.
	 * @param id		The id associated with the child element.
	 * @return			The child element, if found. Otherwise, null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T lookup(Parent parent, String id) {
		
		String nodeId;
		
		// Special case
		if (parent instanceof MenuBar) {
			
			// Search through all MenuItems in all top level menus
			for (Menu menu : ((MenuBar) parent).getMenus()) {
				for (MenuItem item : menu.getItems()) {
					nodeId = item.getId();
					
					if (nodeId != null && nodeId.equals(id)) {
						return (T) item;
					}
				}
			}
			
		} 
		
		// All Node types
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodeId = node.getId();
			
			if (nodeId != null && nodeId.equals(id)) {
				return (T) node;
			} else if (node instanceof Parent) {
				
				// The node is a parent itself - do a recursive lookup
				T child = lookup((Parent) node, id);
				
				if (child != null) {
					return child;
				}
			}
			
		}
		
		return null;
	}

}
