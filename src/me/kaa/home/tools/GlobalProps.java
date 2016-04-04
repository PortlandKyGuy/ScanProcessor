package me.kaa.home.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Accessor for property file.
 * 
 * @author kyle
 *
 */
public class GlobalProps extends Properties{

	/**
	 * The property file containing all the property key/value pairs.
	 */
	static public final String CONFIG_FILE = "config.properties";
	/**
	 * The folder that will hold the current active config file. The active config file is the config file that will be used when a scan script is run.
	 */
	static public final String ACTIVE_CONFIG_FOLDER = "folder.config.active";
	/**
	 * The folder containing some pre-configured scan scripts.
	 */
	static public final String PRECONFIG_FOLDER = "folder.preconfig";
	/**
	 * The filename of the destination configuration file. This file has the default folder destinations based on type.
	 */
	static public final String CONFIG_DESTINATION_FILENAME = "config.destination.filename";
	/**
	 * The default filename patterns based on type. A scan of type medical can have a different filename pattern than a school filename.
	 */
	static public final String CONFIG_FILENAME_PATTERN = "config.pattern.filename";
	/**
	 * The base destination folder for file locations after a scan script is run.
	 */
	static public final String DESTINATION_FOLDER = "folder.destination";
	/**
	 * The folder where the scanner sends the scans to.
	 */
	static public final String SCANS_FOLDER = "folder.scans";
	
	/**
	 * The default filename pattern. This will be used if a specific configuration doesn't have a pattern and the {@link GlobalProps#CONFIG_FILENAME_PATTERN} doesn't contain a pattern for type, then this default filename pattern will be used.
	 */
	static public final String CONFIG_DEFAULT_FILENAME_PATTERN = "config.pattern.filename.default";
	
	static public final String CONFIG_POSTFIX = "config.postfix";

	static private final String[] PROP_KEYS = {ACTIVE_CONFIG_FOLDER, PRECONFIG_FOLDER, PRECONFIG_FOLDER, DESTINATION_FOLDER, SCANS_FOLDER, CONFIG_POSTFIX, CONFIG_FILENAME_PATTERN, CONFIG_DEFAULT_FILENAME_PATTERN};
	
	static private GlobalProps instance;
	
	static public GlobalProps instance() {
		if(instance == null) {
			
			instance = new GlobalProps();
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final InputStream input = classLoader.getResourceAsStream(CONFIG_FILE);
			
			try {
				instance.load(input);
			} catch (IOException e) {
				//We want the application to stop because something isn't configured correctly.
				e.printStackTrace();
				throw new RuntimeException("Could not load property file. Going to die now.");
			}
		
			//ensure the properties are set for all keys.
			System.out.println("GlobalProps: loading config properties...");
			for (String key : PROP_KEYS) {
				final String val = (String) instance.get(key);
				System.out.println(key + "={" + val + "}");
				if(val == null || val.isEmpty()) 
					throw new RuntimeException("The property {" + key + "} is not set in the {" + CONFIG_FILE + "}. This must be fixed.");
			}
			System.out.println("GlobalProps: DONE loading config properties.");
		}
		
		return instance;
	}
	
	/**
	 * {@link GlobalProps#DESTINATION_FOLDER}
	 * @return
	 */
	public String destinationFolder() {
		return (String) get(DESTINATION_FOLDER);
	}
	
	/**
	 * {@link GlobalProps#ACTIVE_CONFIG_FOLDER}
	 * @return
	 */
	public String activeConfigFolder () {
		return (String) get(ACTIVE_CONFIG_FOLDER);
	}
	
	/**
	 * {@link GlobalProps#PRECONFIG_FOLDER}
	 * @return
	 */
	public String preconfigFolder() {
		return (String) get(PRECONFIG_FOLDER);
	}
	
	/**
	 * {@link GlobalProps#CONFIG_DESTINATION_FILENAME}
	 * @return
	 */
	public String destinationConfigFile() {
		return (String) get(CONFIG_DESTINATION_FILENAME);
	}
	
	/**
	 * {@link GlobalProps#SCANS_FOLDER}
	 * @return
	 */
	public String scansFolder() {
		return (String) get(SCANS_FOLDER);
	}
	
	/**
	 * {@link GlobalProps#CONFIG_POSTFIX}
	 * @return
	 */
	public String configFileNamePostfix() {
		return (String) get(CONFIG_POSTFIX);
	}
	
	/**
	 * {@link GlobalProps#CONFIG_FILENAME_PATTERN}
	 * @return
	 */
	public String filenamePatterns() {
		
		return (String) get(CONFIG_FILENAME_PATTERN);
	}
	
	/**
	 * {@link GlobalProps#CONFIG_DEFAULT_FILENAME_PATTERN}
	 * @return
	 */
	public String defaultFilenamePattern() {
		return (String) get(CONFIG_DEFAULT_FILENAME_PATTERN);
	}
	
	
	/**
	 * For testing
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(GlobalProps.instance().get("folder.config.current"));
		System.out.println(GlobalProps.instance().get("folder.destination"));

	}
}
