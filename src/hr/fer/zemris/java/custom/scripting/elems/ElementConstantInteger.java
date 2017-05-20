package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Represents an element of a text which is a constant integer.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementConstantInteger extends Element{
	
	/** Value of the constant integer */
	private int value;
	
	/**
	 * Creates a new ElementConstantInteger with value of the given argument.
	 * @param value value of the new constant integer
	 */
	public ElementConstantInteger(int value){
		this.value = value;
	}
	
	/**
	 * Getter for element value
	 * @return element value
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * Returns a string representation of value property.
	 */
	@Override
	public String asText(){
		return String.valueOf(value);
	}
	
	/**
	 * Returns a string representation of an ElementConstantInteger
	 */
	@Override
	public String toString(){
		return String.valueOf(value);
	}
}
