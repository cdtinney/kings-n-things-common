package com.kingsandthings.logging;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javafx.application.Platform;

public class LogHandler extends Handler {
	
	private Timer timer = new Timer();
	
	public static void setup(Logger logger) {
		
		Logger parent = logger.getParent();
		
		for (Handler handler: parent.getHandlers()) {
			parent.removeHandler(handler);
		}
		
		LogHandler customHandler = new LogHandler();
		customHandler.setFormatter(new LogFormatter());
		customHandler.setLevel(LogLevel.DEBUG);		
		
		parent.setLevel(LogLevel.DEBUG);
		
		parent.addHandler(customHandler);
	}

	@Override
	public void close() throws SecurityException {
		// Do nothing
	}

	@Override
	public void flush() {
		// Do nothing
	}

	@Override
	public void publish(LogRecord r) {
		
		if (r.getLevel() == LogLevel.STATUS) {
			
			// Set the status message in the view
			// TODO - fix status message updating (use interface passed to log handler)
			// GameView.setStatusText(r.getMessage());
			
			// Clear the status message after 5 seconds
			clearStatusMessage(5000, r.getMessage());
			
			return;
		}
		
		System.out.println(getFormatter().format(r));
		
	}
	
	private void clearStatusMessage(int timeElapsed, final String message) {
		
		timer.schedule(new TimerTask() {
			
		    public void run() {
		    	
		         Platform.runLater(new Runnable() {
		        	 
		            public void run() {
		            	
		            	// TODO _- fix status updating w.r.t logging (use interface)
		            	//String status = GameView.getStatusText().replace("STATUS: ", "");
		            	
		            	// Ignore if the message has changed
//		            	if (!status.equals(message)) {
//		            		return;
//		            	}
		            	
		                //GameView.setStatusText(null);
		            }
		            
		        });
		         
		    }
		    
		}, timeElapsed);
		
	}

}
