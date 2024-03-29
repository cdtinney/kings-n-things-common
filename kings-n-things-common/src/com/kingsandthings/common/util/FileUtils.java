package com.kingsandthings.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileUtils {
	
	private static Logger LOGGER = Logger.getLogger(FileUtils.class.getName());
	
	public final static String[] imageExtensions = {"jpg", "png"};

	/*
	 * Returns a list of all the files within a directory with valid extensions.
	 */
    public static List<File> listFiles(String directoryName, String[] extensions) {
    	
    	List<File> results = new ArrayList<File>();    	
    	
        File directory = new File(directoryName);
        
        File[] fList = directory.listFiles();
        
        if (fList == null) {
        	LOGGER.warning("Cannot list files of a directory which does not exit - " + directoryName);
        	return results;
        }
        
        for (File file : fList) {
        	
        	if (file.isDirectory()) {
        		
        		// Resursively add files within sub-directories
        		results.addAll(listFiles(file.getAbsolutePath(), extensions));
        		
        	} else {
        		
        		if (extensions == null) {
        			results.add(file);
        			continue;
        		}
        		
        		if (validExtension(file, extensions)) {
        			results.add(file);
        		}
        	}
        	
        }
        
        return results;
        
    }
    
    private static boolean validExtension(File file, String[] extensions) {
    	
    	boolean valid = false;
    	
    	for (String extension : extensions) {
			
			if (file.getName().endsWith(extension) || file.getName().endsWith("." + extension)) {
				valid = true;
				break;
			}
			
		}
    	
    	return valid;
    	
    }

}
