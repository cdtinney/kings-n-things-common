package com.kingsandthings.common.model.things;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.scene.image.Image;

import com.kingsandthings.util.FileUtils;

public class ThingImport {
	
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(ThingImport.class.getName());
	
	private static final String thingPath = "resources\\images\\things\\";
	
	private static final String creaturePath = thingPath + "defenders\\";
	private static final String magicPath = thingPath + "magic";
//	private static final String fortPath = thingPath + "fort";
//	private static final String specialPath = thingPath + "special";
//	private static final String incomePath = thingPath + "special_income";
//	private static final String treasurePath = thingPath + "treasure";
	
	private static final Map<String, String> options;
	
	static {
		options = new HashMap<String, String>();
		options.put("name", "-n");
		options.put("value", "-a");
		options.put("terrain", "-t");
		options.put("special", "-s");
		options.put("num", "-c");
	}
    
    public static List<Creature> importCreatures() {
    	
    	List<Creature> creatures = new ArrayList<Creature>();
    	
        List<File> files = FileUtils.listFiles(creaturePath, FileUtils.imageExtensions);
        
        for (File file : files) {
        	
        	String name = file.getName();
        	
        	String creatureName = getValue(name, options.get("name")).replace("_", " ");
        	Integer combatValue = Integer.parseInt(getValue(name, options.get("value")));
        	String terrainType = getValue(name, options.get("terrain"));
        	
        	List<String> specialAbilities = getMultipleValues(name, options.get("special"));
        	
        	String path = getImagePath(creaturePath, terrainType.toLowerCase(), name);
        	
        	Integer numOccurrences = 1;
        	String num = getValue(name, options.get("num"));
        	if (num != null) {
        		numOccurrences = Integer.parseInt(num);
        	}
        	
        	for (int i=0; i<numOccurrences; ++i) {
        		
            	Creature creature = null;
            	
            	if (specialAbilities.isEmpty()) {
            		creature = new Creature(creatureName, terrainType, combatValue, new Image(path));
            		creature.setImagePath(path);
            	} else {
            		creature = new Creature(creatureName, terrainType, specialAbilities, combatValue, new Image(path));
            		creature.setImagePath(path);
            	}
            	
            	creatures.add(creature);
            	
        	}
        	
        }
        
        return creatures;
        
    }

	@SuppressWarnings("unused")
	public static List<Magic> importMagicItems() {

    	List<Magic> magicItems = new ArrayList<Magic>();
    	
        List<File> files = FileUtils.listFiles(magicPath, FileUtils.imageExtensions);
    	
    	for (File file : files) {
    		// TODO - import magic items
    	}
    	
    	return magicItems;
		
	}
   
	private static List<String> getMultipleValues(String fileName, String optionSpecifier) {
    	
    	List<String> values = new ArrayList<String>();
    	
    	// Split by whitespace
    	String[] splitName = fileName.split(" ");
    	
    	for (int i=0; i<splitName.length; ++i) {
    		
    		if (splitName[i].equals(optionSpecifier) && (i + 1 < splitName.length)) {
    			
    			String value = splitName[i+1];
    			
    			if (value.contains(".")) {
    				value = value.substring(0, value.lastIndexOf("."));
    			}
    			
    			values.add(value);
    		}
    		
    	}
    	
    	return values;
    	
    }
    
    private static String getValue(String fileName, String optionSpecifier) {
    	
    	// Split by whitespace
    	String[] splitName = fileName.split(" ");
    	
    	for (int i=0; i<splitName.length; ++i) {
    		
    		if (splitName[i].equals(optionSpecifier) && (i + 1 < splitName.length)) {
    			
    			String value = splitName[i+1];
    			
    			// The last string will contain the file extension, so take the substring of everything before the last '.'
    			if (value.contains(".")) {
    				value = value.substring(0, value.lastIndexOf("."));
    			}
    			
    			return value;
    		}
    		
    	}
    	
    	return null;
    	
    }

    /*
     * Paths to load files are different than paths to load images. Namely, slashes are
     * the opposite direction, and 'resources' folder is not necessary. Fix that, append
     * any sub folder names, and append the file name to get a valid URL for loading images.
     */
    private static String getImagePath(String path, String subFolder, String fileName) {
    	
    	path = path.replace("\\", "/").replace("resources", "");
    	
    	if (subFolder != null) {
    		path = path + "/" + subFolder;
    	}
    	
    	return path + "/" + fileName;
    	
    }
    
}
