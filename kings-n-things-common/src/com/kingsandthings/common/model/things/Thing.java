package com.kingsandthings.common.model.things;

import java.io.Serializable;

import javafx.scene.image.Image;

@SuppressWarnings("serial")
public abstract class Thing implements Serializable {
	
	private static Image backImg = new Image("/images/other/thing_back.png");
	private static Image stackImg = new Image("/images/other/thing_stack.png");
	
	public int id;
	
	protected String name;
	private transient Image image;
	
	public Thing() {
		
	}

    public Thing(String name, Image image) {
    	this.name = name;
    	this.image = image;
    	
    	this.id = System.identityHashCode(this);
    }
    
    public String getName() {
    	return name;
    }
    
    public Image getImage() {
    	return image;
    }
    
    public void setImage(Image image) {
    	this.image = image;
    }
    
    @Override
    public boolean equals(Object other) {
    	
    	Thing thing = (Thing) other;
    	return thing.id == this.id;   
    	
    }
    
    @Override
    public String toString() {
    	return name;   	
    }
    
    public static Image getBackImage() {
    	return backImg;
    }
    
    public static Image getStackImage() {
    	return stackImg;
    }

}
