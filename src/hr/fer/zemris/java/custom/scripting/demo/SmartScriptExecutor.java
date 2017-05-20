package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * Demo for class {@link SmartScriptEngine}. Uses {@link SmartScriptParser} to parse the input.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class SmartScriptExecutor {

	/**
	 * Program execution starts with this method.
	 * @param args Command line arguments. 
	 * @throws IOException If an IO error occurs.
	 */
	public static void main(String[] args) throws IOException {
	
		example1();
		System.out.println("_____________________________________________");
		example2();
		System.out.println("_____________________________________________");
		example3();
		System.out.println("_____________________________________________");
		example4();
		
	}

	/**
	 * Example 1
	 * @throws IOException If an IO Error occurs.
	 */
	private static void example1() throws IOException {
		String documentBody = new String(Files.readAllBytes(Paths.get("webroot/scripts/osnovni.smscr")), StandardCharsets.UTF_8);
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		// create engine and execute it
		new SmartScriptEngine(
				new SmartScriptParser(documentBody).getDocumentNode(),
				new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
	
	/**
	 * Example 2
	 * @throws IOException If an IO Error occurs.
	 */
	private static void example2() throws IOException {
		String documentBody = new String(Files.readAllBytes(Paths.get("webroot/scripts/zbrajanje.smscr")), StandardCharsets.UTF_8);
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		parameters.put("a", "4");
		parameters.put("b", "2");
		// create engine and execute it
		new SmartScriptEngine(
				new SmartScriptParser(documentBody).getDocumentNode(),
				new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
	
	/**
	 * Example 3
	 * @throws IOException If an IO Error occurs.
	 */
	private static void example3() throws IOException {
		String documentBody = new String(Files.readAllBytes(Paths.get("webroot/scripts/brojPoziva.smscr")), StandardCharsets.UTF_8);
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		persistentParameters.put("brojPoziva", "3");
		RequestContext rc = new RequestContext(System.out, parameters, persistentParameters, cookies);
		new SmartScriptEngine(
				new SmartScriptParser(documentBody).getDocumentNode(), rc
		).execute();
		System.out.println("Vrijednost u mapi: "+rc.getPersistentParameter("brojPoziva"));
	}
	
	/**
	 * Example 4
	 * @throws IOException If an IO Error occurs.
	 */
	private static void example4() throws IOException {
		String documentBody = new String(Files.readAllBytes(Paths.get("webroot/scripts/fibonacci.smscr")), StandardCharsets.UTF_8);
		Map<String,String> parameters = new HashMap<String, String>();
		Map<String,String> persistentParameters = new HashMap<String, String>();
		List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
		// create engine and execute it
		new SmartScriptEngine(
				new SmartScriptParser(documentBody).getDocumentNode(),
				new RequestContext(System.out, parameters, persistentParameters, cookies)
		).execute();
	}
}
