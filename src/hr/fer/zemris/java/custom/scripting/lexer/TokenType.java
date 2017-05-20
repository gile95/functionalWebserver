package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Valid types of token.
 * @author Mislav Gillinger
 * @version 1.0
 */
public enum TokenType {
	/** Token type which represents a text. */
	TEXT, 
	/** Token type which represent a tag. Tag are strings which start with {$ and end with $} */
	TAG, 
	/** Token type which represents an end of file */
	EOF;
}
