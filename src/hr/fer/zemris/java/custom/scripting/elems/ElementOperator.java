package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Represents an element of a text which is an operator. Valid operators are +(plus),-(minus),/(division),
 * 					*(multiplication),^(power)
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ElementOperator extends Element{

	/**
	 * Symbol for the operator
	 */
	private String symbol;
	
	/**
	 * Creates a new operator with the given symbol
	 * @param symbol symbol which will be given to the operator. 
	 * 		Valid symbols are +(plus),-(minus),/(division),*(multiplication),^(power)
	 */
	public ElementOperator(String symbol){
		if(symbol == null){
			throw new SmartScriptParserException();
		}
		this.symbol = symbol;
	}
	
	/**
	 * Getter for operator's symbol
	 * @return operator's symbol
	 */
	public String getSymbol(){
		return symbol;
	}
	
	/**
	 * Returns a symbol property
	 */
	@Override
	public String asText(){
		return symbol;
	}
	
	/**
	 * Returns a string representation of an ElementOperator
	 */
	@Override
	public String toString(){
		return symbol;
	}
}
