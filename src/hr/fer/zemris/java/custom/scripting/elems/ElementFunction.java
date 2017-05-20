package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Represents an element of a text which is a function.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementFunction extends Element{

	/** Function's name */
	private String name;
	
	/**
	 * Creates a new ElementFunction which name is the one of a given argument.
	 * @param name Name of the function. 
	 * 		Valid function name starts with @ after which follows a letter and after than can follow zero or more
	 * 		letters, digits or underscores.
	 */
	public ElementFunction(String name){
		if(name == null){
			throw new SmartScriptParserException();
		}
		this.name = name;
	}
	
	/**
	 * Getter for function's name
	 * @return function's name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns a name property
	 */
	@Override
	public String asText(){
		return name;
	}
	
	/**
	 * Returns a string representation of an ElementFunction in form "@name"
	 */
	@Override
	public String toString(){
		return "@" + name;
	}
}
