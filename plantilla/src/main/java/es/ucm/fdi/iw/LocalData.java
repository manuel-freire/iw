package es.ucm.fdi.iw;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstraction for local file-storage path.
 * 
 * This is intended to be used as a Spring MVC Bean, initialized from an
 * application.properties property, and providing access to paths where,
 * say, user-uploaded photos or files can be safely stored. 
 * 
 * @author mfreire
 */
public class LocalData {    	    
	private static Log log = LogFactory.getLog(LocalData.class);

    private File baseFolder;
    
    public LocalData(File baseFolder) {
		this.baseFolder = baseFolder;
    	log.info("base folder is " + baseFolder.getAbsolutePath());
    	if (!baseFolder.isDirectory()) {
    		if (baseFolder.exists()) {
    			log.error("exists and is not a directory -- cannot create: " + baseFolder);
    		} else if ( ! baseFolder.mkdirs()){
    			log.error("could not be created -- check permissions " + baseFolder);        			
    		}
    	} else {
    		log.info("using already-existing base folder :-)");
    	}
    	baseFolder.mkdirs();
    }
    
    /**
     * Returns a folder-type file with a given name.
     * Beware: you should carefully escape any user-supplied 
     * folderName before trusting it.
     * @param folderName
     * @return a File pointing to the folder baseFolder/folderName, which will be
     * created if absent.
     */
    public File getFolder(String folderName) {
    	File folder = new File(baseFolder, folderName);
    	if ( ! folder.exists()) {
    		folder.mkdirs();
    	}
    	return folder;
    }
    
    /**
     * Returns a file in a given folder. Beware: you should have
     * carefully escaped any user-supplied folder or file names prior to calling this.
     * @param folderName to use; the (hardcoded) name of a DB table is a simple, safe bet
     * @param fileName to use; the integer ID of a table row is a simple, safe bet
     * @return a File pointing to baseFolder/folderName/fileName. If
     * the file does not exist, it is not created. However, if its folder
     * does not exist, it *will* be created (as by a call to getFolder).
     */
    public File getFile(String folderName, String fileName) {
    	return new File(getFolder(folderName), fileName);
    }
}