package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Represents a meaningful part of a text. 
 * @author Mislav Gillinger
 * @version 1.0
 */
public class Token {

	/** Type of a token */
	protected TokenType type;
	/** Value of a token */
	protected Object value;
	
	/**
	 * Default constructor for class Token
	 */
	public Token(){
		
	}
	
	/**
	 * Constructor which accepts token type and token value
	 * @param type token type
	 * @param value token value
	 */
	public Token (TokenType type, Object value){
		this.type = type;
		this.value = value;
	}
		
	/**
	 * Getter for token value
	 * @return token value
	 */
	public Object getValue(){
		return value;
	}
		
	/**
	 * Getter for token type
	 * @return token type
	 */
	public TokenType getType(){
		return type;
	}
}
