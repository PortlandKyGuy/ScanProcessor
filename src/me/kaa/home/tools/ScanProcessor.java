package me.kaa.home.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class ScanProcessor.
 * 
 * This is the servlet that will actual manipulate the scanned files. It will be called after each scan is complete.
 */
@WebServlet("/run")
public class ScanProcessor extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			loadConfig();
			loadDestinationFolders();
		} catch (ParseException e) {
			System.out.println("There was a problem loading configuration data. e=" + e.getMessage());
			e.printStackTrace();
		}
		
		String retMessage = "";
		try {
			processScans();
			retMessage = "Scanned files have been processed!";
			
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("There was a problem loading configuration data. e=" + e.getMessage());
			retMessage = "The scanned files were not processed because of an error. E=" + e.getMessage();
		}
		
		response.getWriter().append(retMessage);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private HashMap<String, String> destinationFolders;
	private HashMap<String, String> configData;
	
	private File getConfigFile() {
		//There should be only one file in the active config folder. If there is more, then use the first.
		final File folder = new File(GlobalProps.instance().activeConfigFolder());
		for (File file : folder.listFiles()) {
			if(file.getName().contains("config.")) {
				return file;
			}
		}
		
		throw new RuntimeException("config file wasn't found");
	}
	
	/**
	 * Populates the destination folders dictionary.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	void loadDestinationFolders() throws FileNotFoundException, IOException, ParseException {
		
		//There should be only one file in the config folder. If there is more, then use the first.
		final File destinationConfigFile = new File( GlobalProps.instance().destinationConfigFile());
				
		JSONParser parser = new JSONParser();
		
		JSONObject obj = (JSONObject) parser.parse(new FileReader(destinationConfigFile));
		
		destinationFolders = new Gson().fromJson(obj.toJSONString(), new TypeToken<HashMap<String, String>>() {}.getType());
		
	}
	
	void loadConfig() throws FileNotFoundException, IOException, ParseException {
		
		JSONParser parser = new JSONParser();
		
		JSONObject obj = (JSONObject) parser.parse(new FileReader(getConfigFile()));
		
		configData = new Gson().fromJson(obj.toJSONString(), new TypeToken<HashMap<String, String>>() {}.getType());
	
		System.out.println("configData={" + configData.toString() + "}");
	}
	

	
	/**
	 * A scan was done and this class was called. Process the scan.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	void processScans() throws FileNotFoundException, IOException, ParseException {
		//Determine the new file name.

		//final String individual = configData.get("individual");
		final String type = configData.get("type");
		final String destFolderAppenderStr = configData.get("destination_folder_appender");
		final String newFileName = Utils.buildFileName(type, configData);
		
		String destinationFolderStr = "";
		if(configData.containsKey("destination_folder"))
			destinationFolderStr = configData.get("destination_folder");
		
		if( destinationFolderStr.isEmpty() && !destinationFolders.containsKey(type) )
			throw new RuntimeException("The config file and the destination folders config does not contain a folder path for type=" + type);
		
		if( destinationFolderStr.isEmpty() )
			destinationFolderStr = destinationFolders.get(type);
		
		if(destFolderAppenderStr != null && !destFolderAppenderStr.isEmpty()) {
			destinationFolderStr = destinationFolderStr + File.separator + destFolderAppenderStr;
		}
		
		//create the folder if it doesn't exist.
		final File destDir = new File(destinationFolderStr);
		if ( !destDir.exists() ) {
			destDir.mkdirs();
		}
		
		//rename each file in the folder
		File dir = new File(GlobalProps.instance().scansFolder());
		if (dir.isDirectory()) { // make sure it's a directory
			int fileCount = 0;
		    for (final File f : dir.listFiles()) {
		    	fileCount++;
		        try {
		        	//get the extension
		        	final String ext = FilenameUtils.getExtension(f.getAbsolutePath());
		        	
		        	final String newFileStr = newFileName + "_" + Integer.toString(fileCount) + '.' + ext;
		        	
		        	final String moveToStr = destinationFolderStr  + File.separator + newFileStr;
		        	
		            final File newFile = new File(moveToStr);
		            
		            if(f.renameTo(newFile)){
		                System.out.println("Rename succesful. Move to location={" + moveToStr + "}");
		            }else{
		                System.out.println("Rename failed. Move to location={" + moveToStr + "}");
		            }         
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}
		
	}

}
