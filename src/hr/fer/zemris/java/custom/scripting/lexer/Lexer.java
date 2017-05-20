package hr.fer.zemris.java.custom.scripting.lexer;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * This class represents a lexer which is in charge of lexical analysis of the given text.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class Lexer {
	
	/** Text to be analyzed. */
	private String data;
	/** Represents a part of a text. */
	private Token token;
	
	/**
	 * Constructor which accepts text to be analyzed.
	 * @param data Text to be analyzed.
	 */
	public Lexer(String data){
		this.data = data;
		token = new Token();
		token.type = TokenType.TAG;
	}
	
	/**
	 * Searches for the next token behind the last returned one.
	 * @return the next token behind the last returned one
	 */
	public Token nextToken(){
		String retString;
		
		if(data.trim().length() == 0){
			token.type = TokenType.EOF;
			token.value = null;
			return token;
		}
		
		if(token.type == TokenType.TAG){
			if(!data.contains("{$")){
				token.value = data;
				data = "";
				token.type = TokenType.TEXT;
				return token;
			}
			retString = data.substring(0, data.indexOf("{$"));
			int i = data.indexOf("{$");
			if(i != 0){
				if(data.charAt(i-1) == '\\'){
					data = data.substring(data.indexOf("$"));
					retString = retString + "{" + data.substring(data.indexOf("$"), data.indexOf("{$"));
				}
			}
			data = data.substring(data.indexOf("{$"));
			token.type = TokenType.TEXT;
		}
		else{
			retString = data.substring(0, data.indexOf("$}") + 2);
			data = data.substring(data.indexOf("$}") + 2);
			token.type = TokenType.TAG;
		}
		token.value = retString;	
		return token;
	}
	
	/**
	 * Method which determines if a given expression is one of valid Elements. If it is, it returns an instance of it.
	 * @param expression Expression to be evaluated.
	 * @return Instance of ElementConstantDouble, ElementConstantInteger, ElementFunction, ElementOperator, 
	 * 		ElementString or ElementVariable
	 * @throws SmartScriptParserException if the given expression is not a valid Element
	 */
	public static Element determineExpression(String expression) {
		//is the expression a method
		if (expression.matches("@[A-Za-z]\\w*")) {
			expression = expression.substring(1, expression.length());
			return new ElementFunction(expression);
		}
		//is the expression a string
		else if (expression.startsWith("\"") && expression.endsWith("\"")) {
			
			expression = expression.replaceAll("[\\\\][n]", "\n");
			expression = expression.replaceAll("[\\\\][r]", "\r");
			
			
			expression = expression.replaceAll("\\\\\\\\", "\\\\");
			expression = expression.replaceAll("\\\\\"", "\\\"");
			expression = expression.replaceAll("\"", "");
			return new ElementString(expression);
		}
		//is the expression operator
		else if (expression.matches("[*+-/^]")) {
			return new ElementOperator(expression);
		}
		//is the expression integer
		else if (expression.matches("[-]?\\d+")) {
			try {
				return new ElementConstantInteger(Integer.parseInt(expression));
			} catch (NumberFormatException e) {
				throw new SmartScriptParserException();
			}
		}
		//is the expression double
		else if (expression.matches("[-]?\\d+\\.\\d+")) {
			try {
				return new ElementConstantDouble(Double.parseDouble(expression));
			} catch (NumberFormatException e) {
				throw new SmartScriptParserException();
			}
		}
		//is the expression variable
		else if (expression.matches("[A-Za-z]\\w*")) {
			return new ElementVariable(expression);
		}
		//expression is invalid
		else {
			throw new SmartScriptParserException();
		}
	}
}
