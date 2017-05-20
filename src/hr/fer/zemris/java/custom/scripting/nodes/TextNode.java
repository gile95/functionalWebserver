package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * A node representing a piece of textual data.
 * @author Mislav Gillinger
 * @version 1.0
 */

public class TextNode extends Node{
	
	/** Text of this TextNode */
	private String text;
	
	/**
	 * Constructor which creates a new TextNode and initializes it text with the given argument.
	 * @param text text of this TextNode
	 */
	public TextNode(String text){
		if(text == null){
			throw new SmartScriptParserException();
		}
		
		this.text = text;
	}
	
	/**
	 * Getter for text variable
	 * @return text variable
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * Returns String representation of a text.
	 */
	@Override
	public String toString(){
		return text;
	}
	
	public void accept(INodeVisitor nodeVisitor){
		nodeVisitor.visitTextNode(this);
	}
}
