package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Exception which handles all exceptions, or is thrown, when an error in SmartScriptParser occurs.
 * @author Mislav Gillinger
 * @version 1.0
 */

public class SmartScriptParserException extends RuntimeException{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor for class SmartScriptParserException
	 */
	public SmartScriptParserException(){
		
	}
	
	/**
	 * Constructor which accepts a message and delegates it to a superior exception.
	 * @param message - message sent from where an exception occured
	 */
	public SmartScriptParserException(String message){
		super(message);
	}
}
