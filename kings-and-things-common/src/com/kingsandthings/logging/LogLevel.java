package com.kingsandthings.logging;

import java.util.logging.Level;

@SuppressWarnings("serial")
public class LogLevel extends Level {
	
	public static final Level STATUS = new LogLevel("STATUS", Level.INFO.intValue() + 1);
	public static final Level DEBUG = new LogLevel("DEBUG", Level.INFO.intValue() - 1);

	public LogLevel(String name, int value) {
		super(name, value);
	}

}
