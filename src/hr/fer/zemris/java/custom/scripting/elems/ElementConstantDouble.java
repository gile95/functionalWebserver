package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Represents an element of a text which is a constant double.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementConstantDouble extends Element{

	/** Value of the constant double. */
	private double value;
	
	/**
	 * Creates a new ElementConstantDouble with value of the given argument.
	 * @param value Value of the new constant double
	 */
	public ElementConstantDouble(double value){
		this.value = value;
	}
	
	/**
	 * Getter for element value
	 * @return element value
	 */
	public double getValue(){
		return value;
	}
	
	/**
	 * Returns a string representation of a value property.
	 */
	@Override
	public String asText(){
		return String.valueOf(value);
	}
	
	/**
	 * Returns a string representation of an ElementConstantDouble.
	 */
	@Override
	public String toString(){
		return String.valueOf(value);
	}
}
