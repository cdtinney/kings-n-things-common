package com.kingsandthings.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Dialog {
	
	private static Stage stage;
	
	public static void setStage(Stage primaryStage) {
		stage = primaryStage;
	}
	
	public enum Type {
		NOTIFY
	}
	
	public static void show(Type type, String message) {
		
		String title = "";
		
		if (type == Type.NOTIFY) {
			title = "Notification";
		}
		
	    final Stage dialog = new Stage(StageStyle.UTILITY);
	    dialog.initModality(Modality.WINDOW_MODAL);
	    dialog.initOwner(stage);
	    dialog.setResizable(false);
	    
	    dialog.setTitle(title);
	    //dialog.getIcons().addAll(stage.getIcons());
	    
	    Scene scene = new Scene(
	    		VBoxBuilder.create().styleClass("dialogContent").children(
	    				LabelBuilder.create().text(message).build()).build(), Color.TRANSPARENT);
	    
	    scene.getRoot().getStyleClass().add("dialog");
	    scene.getStylesheets().add(Dialog.class.getResource("/css/Dialog.css").toExternalForm());
	    
	    // stage.setMinWidth does not seem to be working here.
	    ((Pane) scene.getRoot()).setMinWidth(150);
	    
	    addOkButton(scene);
	    
	    dialog.setScene(scene);
	    dialog.show();
	    
	}
	
	private static void addOkButton(Scene scene) {
		
		Pane root = (Pane) scene.getRoot();
		Button button = ButtonBuilder.create().text("OK").defaultButton(true).build();
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Parent parent = ((Node) event.getSource()).getParent();
				((Stage) parent.getScene().getWindow()).hide();
			}
	    });
		
		root.getChildren().add(button);
		
	}
	
}