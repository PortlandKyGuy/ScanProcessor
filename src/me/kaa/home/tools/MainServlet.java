package me.kaa.home.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet(description = "Main servlet to handle the scan runner setup.", urlPatterns = { "/process" })
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */ 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		final HashMap<String, String> configFilePairs = getConfigFileParams(request);
		
		//get the command.
		final String cmd = request.getParameter("cmd");
		System.out.println("cmd={" + cmd + "}");
		
		if(cmd != null && cmd.equals("saveconfigfile")) {
			processSaveConfigCmd(request, configFilePairs);
			
			//Now go back to main page.
			final String nextJSP = "index.jsp";
			response.sendRedirect(nextJSP);
			
		} else if(cmd != null && cmd.equals("getpreconfigs")) {
			processGetPreConfigsCmd(request, response);
				
		} else if(cmd != null && cmd.equals("getspecificconfig")) {
			processGetSpecificConfigCmd(request, response);
			
		} else {
			
			clearConfigDirector();
			
			createJSONFile(configFilePairs);
			
			//Now go back to main page.
			final String nextJSP = "index.jsp";
			response.sendRedirect(nextJSP);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	private void processSaveConfigCmd(HttpServletRequest request, final HashMap<String, String> configFilePairs) {
		final String newFileName = request.getParameter("save-file-as");
		
		if (newFileName == null || newFileName.isEmpty())
			throw new RuntimeException("There is no request param for save-file-as");
		
		saveConfigFile(configFilePairs, newFileName);
	}
	
	private void processGetPreConfigsCmd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final File[] files = loadPreConfiguratedConfigFiles();
		
		String jsonStr = toJsonStr(files);
		
		response.getWriter().append(jsonStr);
	}
	
	private void processGetSpecificConfigCmd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final File[] files = loadPreConfiguratedConfigFiles();
		
		String jsonStr;
		
		//see if there was a request for a specific file name.
		final String fileToReturn = request.getParameter("file_name");
		
		if(fileToReturn == null || fileToReturn.isEmpty())
			throw new RuntimeException("file_name is required when cmd={getpreconfig}");
	
		System.out.println("file to get is={" + fileToReturn + "}");
		
		final File file = findFileIn(files, fileToReturn);
		JSONObject obj;
		try {
			obj = fileAsJsonObj(file);
			jsonStr = obj.toJSONString();
		} catch (ParseException e) {
			e.printStackTrace();
			jsonStr = "Could not load the specific config file. see system.out for reason.";
		}
			
		response.getWriter().append(jsonStr);
	}
	
	
	private HashMap<String, String> getConfigFileParams(HttpServletRequest request) {
		final Enumeration<String> keys = request.getParameterNames();
		final String paramPrefix = "use_";
		
		final HashMap<String, String> retMap = new HashMap<String, String>();
		
		//use all the strings starting with 'use_'
		while (keys.hasMoreElements()) {
			final String key = keys.nextElement();
			if(key != null && !key.isEmpty() && key.contains(paramPrefix)) {
				final String newKey = key.substring(key.indexOf(paramPrefix) + paramPrefix.length());
				final String value = request.getParameter(key);
				if(value != null && !value.isEmpty()) {
					System.out.println("Adding config file key/pair " + newKey + "={" + value + "}");
					retMap.put(newKey, value);
				}
			}
		}
		return retMap;
		
	}
	
	private void saveConfigFile(final HashMap<String, String> configPairs, final String toSaveAs)  {
		
		final String fileName = GlobalProps.instance().preconfigFolder() + File.separator + toSaveAs + GlobalProps.instance().configFileNamePostfix();
		
		//Since this is  a JSONFile, use the gson library to create a JSON object. Then just write it to the correct file.
		JSONObject obj = new JSONObject();
		obj.putAll(configPairs);		
		
		final boolean appendToFile = false;
		
		try(FileWriter file = new FileWriter(fileName, appendToFile)) {
			file.write(obj.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not open the config file for writing. filename={" + fileName + "}");
		} 
	}

	@SuppressWarnings("unchecked")
	private void createJSONFile(HashMap<String, String> configPairs)  {
		//Since this is  a JSONFile, use the gson library to create a JSON object. Then just write it to the correct file.
		
		JSONObject obj = new JSONObject();
		obj.putAll(configPairs);
		
		final String dateStr = Utils.dateStr();
		
		final String fileName = GlobalProps.instance().activeConfigFolder() + File.separator + dateStr + "_custom" + GlobalProps.instance().configFileNamePostfix();
		
		try(FileWriter file = new FileWriter(fileName)) {
			file.write(obj.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not open the config file for writing. filename={" + fileName + "}");
		} 
	}
	
	private void clearConfigDirector()  {
		String cmdConfigFolder = GlobalProps.instance().activeConfigFolder().replace(" ", "\\ ");
		if(!cmdConfigFolder.endsWith("/"))
			cmdConfigFolder = cmdConfigFolder + "/";
		final String cmd = "rm -f " + cmdConfigFolder + "*";
		
		String[] commandAndArgs = new String[]{ "/bin/sh", "-c", cmd };
		
		System.out.println("Clean cmd ={" + cmd + "}");
		try {
			Process proc = Runtime.getRuntime().exec(commandAndArgs);
			//Process proc = Runtime.getRuntime().
			BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(proc.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(proc.getErrorStream()));

				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				String s = null;
				while ((s = stdInput.readLine()) != null) {
				    System.out.println(s);
				}

				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
				    System.out.println(s);
				}
		} catch (IOException e) {

			e.printStackTrace();
		}
	
	}
	
	
	//MARK: Pre-Loading

	private File findFileIn(final File[] files, final String fileName) {
		for(File f : files) {
			System.out.println("comparing {" + f.getName() + "} to {" + fileName + "}");
			if(f.getName().equalsIgnoreCase(fileName)) 
				return f;
		}
		return null;
	}
	
	private String toJsonStr(final File[] files) throws FileNotFoundException, IOException {
		JSONArray jarray = new JSONArray();
		if(files != null) {
			for(File file : files) {
				JSONObject newObj;
				try {
					newObj = fileAsJsonObj(file);
					jarray.add(newObj);
				} catch (ParseException e) {
					System.out.println("Error trying to parse file {"  + file.getName() + "}. Skipping and moving to next config file.");
					e.printStackTrace();
				}

			}
			
		}
		
		return jarray.toJSONString();	
	}
	
	private JSONObject fileAsJsonObj(final File file) throws FileNotFoundException, IOException, ParseException {
		
		System.out.println("Converting {" + file.getName() + "} to JSON string");
		
		JSONObject newObj = new JSONObject();
		newObj.put("file_name", file.getName());
		newObj.put("location", file.getAbsolutePath());
		newObj.put("dir", file.getPath());
		
		//final File destinationConfigFile = new File(Utils.DESTINATION_FOLDER_CONFIG_FILE);
		final JSONParser parser = new JSONParser();
		JSONObject configDetailInfo = (JSONObject) parser.parse(new FileReader(file));
		newObj.put("config_details", configDetailInfo);
		
		return newObj;
	}

	private File[] loadPreConfiguratedConfigFiles() {
		//load all the pre-configured files.
		final File folder = new File(GlobalProps.instance().preconfigFolder());
		if(folder.exists() && folder.isDirectory()) {
			
			final File[] files = folder.listFiles(new FilenameFilter() {
			    
				//Only return files that have the correct postfix.
				public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(GlobalProps.instance().configFileNamePostfix());
			    }
				
			});
			
			return files;
			
		}
		
		return null;
	}
}
