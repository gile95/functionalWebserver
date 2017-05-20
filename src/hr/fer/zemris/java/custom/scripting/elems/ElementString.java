package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Represents an element of a text which is a string
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementString extends Element{
	
	/** Value of a string */
	private String value;
	
	/**
	 * Creates a new ElementString with value of the given argument.
	 * @param value new string's value
	 */
	public ElementString(String value){
		if(value == null){
			throw new SmartScriptParserException();
		}
		this.value = value;
	}
	
	/**
	 * Getter for value of a string
	 * @return value of a string
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Returns a value property
	 */
	@Override
	public String asText(){
		return value;
	}
	
	/**
	 * Returns a string representation of an ElementString
	 */
	@Override
	public String toString(){
		return "\"" + value + "\"";
	}
}
