package me.kaa.home.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * @author kaa
 *
 */
public class UtilsTest {

	/**
	 * Test method for {@link me.kaa.home.tools.Utils#buildFileName(java.lang.String, java.util.HashMap)}.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testBuildFileName() throws FileNotFoundException, IOException, ParseException {
		
		final HashMap<String, String> configData = new HashMap<String, String>();
		configData.put("pattern", "_Scan_+individual");
		configData.put("individual", "tester1");
		String type = "test"; //doesn't matter for this test.
		
		String result = Utils.buildFileName(type, configData);
		String expected = "_Scan_tester1";
		
		assertEquals(expected, result);
		
		//Reset
		String date = Utils.dateStr();//TODO: there is a risk that the date might be off if the minute changes.
		configData.clear();
		configData.put("pattern", "date+_Scan_+individual");
		configData.put("individual", "tester2");
		type = "test"; //doesn't matter for this test.
		
		result = Utils.buildFileName(type, configData);
		expected = date + "_Scan_tester2";
		
		assertEquals(expected, result);
		
		//Reset
		date = Utils.dateStr();//TODO: there is a risk that the date might be off if the minute changes.
		configData.clear();
		configData.put("pattern", "20150101 +Medical +individual+ Scanned+date");
		configData.put("individual", "tester3");
		type = "medical"; 
		
		result = Utils.buildFileName(type, configData);
		expected = "20150101 Medical tester3 Scanned" + date;
		
		assertEquals(expected, result);
		
		//Reset
		//Test with | (or) in the pattern
		date = Utils.dateStr();//TODO: there is a risk that the date might be off if the minute changes.
		configData.clear();
		configData.put("pattern", "20150101 +Medical +\"title\"|individual+ Scanned+date");
		configData.put("individual", "tester4");
		type = "medical"; 
		
		result = Utils.buildFileName(type, configData);
		expected = "20150101 Medical tester4 Scanned" + date;
		
		assertEquals("Filename results are incorrect", expected, result);
		
		//Reset
		//Test with | (or) in the pattern
		date = Utils.dateStr();//TODO: there is a risk that the date might be off if the minute changes.
		configData.clear();
		configData.put("pattern", "20150101 +Medical +state|individual|\"title\"+ Scanned+date");
		configData.put("individual", "tester5");
		configData.put("title", "Sir");
		type = "medical"; 
		
		result = Utils.buildFileName(type, configData);
		expected = "20150101 Medical tester5 Scanned" + date;
		
		assertEquals("Filename results are incorrect", expected, result);
		
		//Reset
		//Test with | (or) in the pattern
		date = Utils.dateStr();//TODO: there is a risk that the date might be off if the minute changes.
		configData.clear();
		configData.put("pattern", "20150101 +Medical +state|individual|\"title\"+ Scanned+date");
		type = "medical"; 
		
		result = Utils.buildFileName(type, configData);
		expected = "20150101 Medical title Scanned" + date;
		
		assertEquals("Filename results are incorrect", expected, result);
	}

	/**
	 * Test method for {@link me.kaa.home.tools.Utils#getFileNamePatterns()}.
	 */
	@Test
	public void testGetFileNamePatterns() {
		fail("Not yet implemented");
	}

}
