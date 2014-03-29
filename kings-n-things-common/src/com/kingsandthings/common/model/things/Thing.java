package com.kingsandthings.common.model.things;

import java.io.Serializable;

import javafx.scene.image.Image;

@SuppressWarnings("serial")
public abstract class Thing implements Serializable {
	
	private static Image backImg = new Image("/images/other/thing_back.png");
	private static Image stackImg = new Image("/images/other/thing_stack.png");

	protected String name;
	
	private int id;
	
	private transient Image image;
	private String imagePath;
	
	public Thing() { }

    public Thing(String name, Image image) {
    	this.name = name;
    	this.image = image;
    	
    	id = System.identityHashCode(this);
    }
    
    public String getName() {
    	return name;
    }
    
    public Image getImage() {
    	
    	if (image == null) {
    		image = new Image(imagePath);
    	}
    	
    	return image;
    }
    
    public void setImage(Image image) {
    	this.image = image;
    }
    
    public void setImagePath(String path) {
    	imagePath = path;
    }
    
    @Override
    public boolean equals(Object that) {
    	
    	if (this == that) {
    		return true;
    	}
    	
    	if (!(that instanceof Thing)) {
    		return false;
    	}
    	
    	Thing thing = (Thing) that;
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
