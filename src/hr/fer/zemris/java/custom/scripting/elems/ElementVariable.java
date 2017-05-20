package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Represents an element of a text which is a variable
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementVariable extends Element{
	
	/** Variable's name */
	private String name;
	
	/**
	 * Creates a new ElementVariable with name of the given argument
	 * @param name Name of the variable.
	 * 		Valid variable name starts by letter and after follows zero or more letters, digits or underscores.
	 */
	public ElementVariable(String name){
		if(name == null){
			throw new SmartScriptParserException();
		}
		this.name = name;
	}
	
	/**
	 * Getter for variable's name
	 * @return variable's name
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
	 * Returns a string representation of an ElementFunction.
	 */
	@Override
	public String toString(){
		return name;
	}
}
