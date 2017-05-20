package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * A node representing a command which generates some textual output dynamically
 * @author Mislav Gillinger
 * @version 1.0
 */
public class EchoNode extends Node{

	/** Elements which form Echo expression */
	private Element[] elements;
	
	/**
	 * Constructor which creates a new EchoNode based on given elements.
	 * @param elements New EchoNode will be created based on these.
	 */
	public EchoNode(Element[] elements){
		if(elements == null){
			throw new SmartScriptParserException();
		}
		this.elements = elements;
	}
	
	/**
	 * Getter for array of Elements
	 * @return Elements which form Echo expression
	 */
	public Element[] getElements(){
		return elements;
	}
	
	/**
	 * Returns a string representation of EchoNode in format {$= elem1 elem2 elem3 elemN $}
	 */
	@Override
	public String toString(){
		String ret = "{$= ";
		for(Element e : elements){
			ret += e.toString();
			ret += " ";
		}
		ret += "$}";
		return ret;
	}
	
	public void accept(INodeVisitor nodeVisitor){
		nodeVisitor.visitEchoNode(this);
	}
}
