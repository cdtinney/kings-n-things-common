package com.kingsandthings.util;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DataImageView extends ImageView {
	
	private static ImageViewHoverHandler hoverHandler = new ImageViewHoverHandler();
	private static final float hoverRatio = 2.8f;
	
	private Object data;
	
	private DataImageView hoverImageView;
	private boolean hover = false;
	
	private boolean selected = false;
	
	public DataImageView(boolean hover) {
		setPreserveRatio(true);
		setCache(true);
		
		this.hover = hover;
		
		if (hover) {
			this.setOnMouseEntered(hoverHandler);
			this.setOnMouseExited(hoverHandler);
		}
		
	}
	
	public DataImageView(boolean hover, double width) {
		this(hover);
		
		setFitWidth(width);
	}
	
	public static void clear(List<DataImageView> imageViews, boolean removeSelection) {
		
		for (DataImageView imageView : imageViews) {
			imageView.setData(null);
			imageView.setImage(null);
			
			if (removeSelection) {
				imageView.setSelected(false);
			}
		}
		
	}
	
	public void setData(Object object) {
		this.data = object;
	}
	
	public Object getData() {
		return this.data;
	}
	
	public void setSelected(boolean selected) {
		showBorder(this.selected = selected);
		showHoverImage(this.selected);
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	private void showHoverImage(boolean show) {
		
		if (!hover) {
			return;
		}
		
		if (!show && hoverImageView != null) {
			hoverImageView.setVisible(false);
			return;
		}

		Pane parent = (Pane) this.getParent();

		double imageWidth = this.getBoundsInParent().getWidth();
		
		if (hoverImageView == null) {
			hoverImageView = new DataImageView(false);
			hoverImageView.setOpacity(0.95);
			hoverImageView.setPreserveRatio(true);
			hoverImageView.setFitWidth(imageWidth * hoverRatio);
			hoverImageView.managedProperty().bind(hoverImageView.visibleProperty());
			
			parent.getChildren().add(hoverImageView);
		}
		
		hoverImageView.setImage(getImage());

		double x = getLayoutX() + imageWidth + 7 + (selected? -4 : 0);
		
		double hoverImageHeight = hoverImageView.getBoundsInParent().getHeight();
		double viewHeight = parent.getHeight();
		
		double y = getLayoutY();
		if (y + hoverImageHeight > viewHeight) {
			y -= (hoverImageHeight / 2);
		}
		
		hoverImageView.relocate(x, y);
		hoverImageView.setVisible(true);
		hoverImageView.showBorder(true);
		
	}
	
	private void showBorder(boolean show) {
		
		if (show) {
			setEffect(DropShadowBuilder.create().color(Color.TURQUOISE).spread(1).radius(3).build());
		} else if (!selected) {
			setEffect(null);
		}
		
	}
	
	private static class ImageViewHoverHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			DataImageView imgView = (DataImageView) event.getSource();
			
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
				imgView.showHoverImage(true);
				imgView.showBorder(true);
			}
			
			if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
				imgView.showHoverImage(false);
				imgView.showBorder(false);
			}
			
		}
		
	}

}
