package hr.fer.zemris.java.custom.scripting.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.Lexer;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

/**
 * Represents a simple parser, which is a part of a language processor. 
 * @author Mislav Gillinger
 * @version 1.0
 */
public class SmartScriptParser {

	/** Node which represents an entire document*/
	private DocumentNode documentNode;
	/** Internal storage which helps organizing structure of stored nodes. */
	private Stack<Node> stack;
	
	/**
	 * Accepts a string that contains a document body. Creates an instance of lexer and initializes it
	 * with obtained text. Delegates actual parsing to separate method.
	 * @param data String that contains a document body.
	 */
	public SmartScriptParser(String data){
		if (data == null) {
			throw new SmartScriptParserException();
		}
		documentNode = new DocumentNode();
		stack = new Stack<>();
		stack.push(documentNode);
		
		Lexer lexer = new Lexer(data);
		parse(lexer);
	}
	
	/**
	 * Method which gets tokens and is supposed to deal with semantic analysis of given tokens.
	 * @param lexer lexer that supplies parser with tokens
	 */
	private void parse(Lexer lexer){
		Token currentToken;
		
		while ((currentToken = lexer.nextToken()).getType() != TokenType.EOF){
			
			if(currentToken.getType().equals(TokenType.TEXT)){
				if(currentToken.getValue().toString().equals("")) continue;
			}
			
			String text = currentToken.getValue().toString();
			
			//if token is tag
			if (currentToken.getType() == TokenType.TAG){
				
				//getting rid of {$ and $}
				text = text.substring(2, text.length() - 2).trim();
				
				//if tag is for
				if (text.toUpperCase().contains("FOR")) {
					processFor(text);
				}
				
				//if tag is end
				else if (text.toUpperCase().equals("END")) {
					processEnd();
				}
				
				//if tag is echo
				else if (text.contains("=")) {					
					if (text.indexOf("=") != 0) {
						throw new SmartScriptParserException();
					}
					else {
						processEcho(text);
					}
				}
			}
			
			//if token is text
			else {		
				char[] textChar = text.toCharArray();
				for(int i = 0, n = textChar.length; i < n-1; i++){
					if((textChar[i] == '\\' && textChar[i+1] != '\\') && (textChar[i] == '\\' && textChar[i+1] != '{')){
						throw new SmartScriptParserException();
					}
				}
				
				Node temp = (Node) stack.pop();
				temp.addChildNode(new TextNode(text));
				stack.push(temp);
			}
		}
		if(stack.size() != 1){
			throw new SmartScriptParserException();
		}
	}

	/**
	 * Method which processes the given expression which is a potential for loop. After determining that the
	 * expression is for loop, it creates an instance of ForLoopNode based on given expression value,
	 * adds that node as a child to a current top of stack, and pushes it on a stack.
	 * @param text Potential for loop expression
	 */
	private void processFor(String text){
		text = text.substring(4, text.length()).trim();
		String[] elements = text.split("\\s+");
		if (elements.length != 3 && elements.length != 4) {
			throw new SmartScriptParserException();
		}
		
		//elements[0]
		ElementVariable variable;
		Element variable1 = Lexer.determineExpression(elements[0]);
		if (!(variable1 instanceof ElementVariable)) {
			throw new SmartScriptParserException();
		}
		else {
			variable = (ElementVariable) variable1;
		}
		
		//elements[1]
		Element startExpression = Lexer.determineExpression(elements[1]);
		if (startExpression instanceof ElementFunction || startExpression instanceof ElementOperator) {
			throw new SmartScriptParserException();
		}
	
		//elements[2]
		Element endExpression = Lexer.determineExpression(elements[2]);
		if (endExpression instanceof ElementFunction || endExpression instanceof ElementOperator) {
			throw new SmartScriptParserException();
		}
		
		//elements[3]
		Element stepExpression = null;
		
		if(elements.length == 4){
			if (elements[3] != null) {
				stepExpression = Lexer.determineExpression(elements[3]);
				if (stepExpression instanceof ElementFunction || stepExpression instanceof ElementOperator) {
					throw new SmartScriptParserException();
				}
			}
		}
		
		if (stack.isEmpty()) {
			throw new SmartScriptParserException();
		}
		Node temp = (Node) stack.pop();
		ForLoopNode forLoop = new ForLoopNode(variable, startExpression, endExpression, stepExpression);
		temp.addChildNode(forLoop);
		stack.push(temp);
		stack.push(forLoop);
	}
	
	/**
	 * End tag is processed in a way that last element stored on stack is popped.
	 */
	private void processEnd() {
		if (stack.isEmpty()) {
			throw new SmartScriptParserException();
		}
		stack.pop();
	}
	
	/**
	 * Determines whether the given expression is an echo. After determining that it is, it creates an instance of
	 * EchoNode based on given expression value, and adds it as a child to a current top of stack node.
	 * @param text Potential echo expression.
	 */
	private void processEcho(String text){
		text = text.substring(1, text.length()).trim();
		
		//echos without space like (i+i*@sin "0.00"@decfmt) can be processed
		text = text.replaceAll("@", " @").replaceAll("[*]", " * ").replaceAll("[+]", " + ");
	
		text = text.replaceAll("\\s+", " "); //same : text = text.replaceAll("\n", " ");
		
		text = " " + text;
		text = text.replaceAll(" \"", " \"\"");
		
		String[] tempS = text.split(" \"");
		
		boolean first = true;
		
		List<String> elements1 = new ArrayList<>();
		for(String s : tempS){
			if(first && tempS[0] == ""){
				first = false;
				continue;
			}
			if(s.startsWith("\"") && s.endsWith("\"")) elements1.add(s);
			else{
				String[] elems = s.split("\\s+");
				for(String s1 : elems){
					if(s1.equals("")) continue;
					elements1.add(s1);
				}
			}
		}
		
		
		Element[] elements = new Element[elements1.size()];
		for (int i = 0, n = elements1.size(); i < n; i++) {
			elements[i] = Lexer.determineExpression(elements1.get(i));
		}
		
		if (stack.isEmpty()) {
			throw new SmartScriptParserException();
		}
		Node temp = (Node) stack.pop();
		temp.addChildNode(new EchoNode(elements));
		stack.push(temp);
	}
	
	/**
	 * Getter for a documentNode.
	 * @return Node which represents an entire document
	 */
	public DocumentNode getDocumentNode(){
		return documentNode;
	}
}
