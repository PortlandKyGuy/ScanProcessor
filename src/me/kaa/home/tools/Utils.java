package me.kaa.home.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utils {

	static public String dateStr() {
		final Date curDate = new Date();
		 
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
		 
		final String DateToStr = format.format(curDate);
		
		return DateToStr;
	}
	
	/**
	 * Filename patterns use + as the divider. 
	 * First the pattern is split on the '+' character.
	 * Next each element is checked as a key in the {@configData}. If a match is found, then the value is used.
	 * Otherwise the element is used as a string.
	 * If an element has '|' then the first element is checked as key in {@configData}. If not found, the string after '|' will be used.
	 * 
	 * NOTE: a specific config file can overwrite the one listed in main file patterns.
	 * 
	 * @param type
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	static public String buildFileName(final String type, final HashMap<String, String> configData) throws FileNotFoundException, IOException, ParseException {
		//load patterns data.
		final HashMap<String, String> filenamePatterns = getFileNamePatterns();
		
		String pattern = "";
		
		if(configData.containsKey("pattern"))
			pattern = configData.get("pattern");

		if(pattern == null || pattern.isEmpty())
			pattern = filenamePatterns.get(type);
		
		if(pattern == null || pattern.isEmpty()) {
			System.out.println("No pattern found for type=" + type + ". Using the default pattern");
			pattern = GlobalProps.instance().defaultFilenamePattern();
		}
		
		final StringBuilder retString = new StringBuilder();

		final String[] elements = pattern.split("\\+"); //Since + is a reserved character in regex, it needs to be escaped.
		
		for (String el : elements) {
			String strToUse = "";
			
			if(el.equals("date"))
				strToUse = Utils.dateStr();
			else {
			

				//first see if we have an | item
				if (el.contains("|")) {
					final String[] subEls = el.split("\\|");
					
					for(String keyToTry : subEls) {
						if(!keyToTry.contains("\"") && configData.containsKey(keyToTry)) {
							strToUse = configData.get(keyToTry);
							break;
						}
					}
					
					if(strToUse == null || strToUse.isEmpty()) {
						//find the first key with a double quote around it.
						for(String keyToTry : subEls) {
							if(keyToTry.contains("\"")) {
								strToUse = keyToTry.substring(1, keyToTry.length()-1);
								break;
							}
						}
					}
					
				} else {
					if(configData.containsKey(el)) {
						strToUse = configData.get(el);
					} else
						strToUse = el;
				}
				
			}
			
			retString.append(strToUse);
		}
		final String returnStr = retString.toString();
		System.out.println("Filename is going to be={" + returnStr + "}");
		return returnStr;
	}
	
	static public HashMap<String, String> getFileNamePatterns() throws FileNotFoundException, IOException, ParseException {
		
		final File destinationConfigFile = new File(GlobalProps.instance().filenamePatterns());
				
		JSONParser parser = new JSONParser();
		
		JSONObject obj = (JSONObject) parser.parse(new FileReader(destinationConfigFile));
		
		return new Gson().fromJson(obj.toJSONString(), new TypeToken<HashMap<String, String>>() {}.getType());
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
