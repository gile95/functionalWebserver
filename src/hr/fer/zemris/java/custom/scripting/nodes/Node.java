package hr.fer.zemris.java.custom.scripting.nodes;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Base class for all graph nodes. Nodes are used for representation of structured documents.
 * @author Mislav Gillinger
 * @version 1.0
 */
public abstract class Node {

	/**	Collection used for keeping references to all objects which are children of this one. */
	public List<Node> children;
	
	/**
	 * Every node has to implements this method. Basically it calls nodeVisitor.visitXNode(this), 
	 * where X is a class which extends {@link Node}.
	 * @param nodeVisitor Concrete implementation of class {@link INodeVisitor}.
	 */
	public abstract void accept(INodeVisitor nodeVisitor);
	
	/**
	 * Adds a node in a collection of this node's children.
	 * @param child Node to be added as a child.
	 */
	public void addChildNode(Node child){
		if(child == null){
			throw new SmartScriptParserException();
		}
		
		if(children == null){
			children = new ArrayList<>();
		}
		
		children.add(child);
	}
	
	/**
	 * Returns a size of collection which contains all children of this node.
	 * @return a size of collection which contains all children of this node.
	 */
	public int numberOfChildren(){
		return children.size();
	}
	
	/**
	 * Returns an element from collection with children on the given index
	 * @param index Index of the wanted element
	 * @return an element from collection with children on the given index
	 */
	public Node getChild(int index){
		if(index < 0 || index > children.size()-1){
			throw new SmartScriptParserException();
		}
		return children.get(index);
	}
	
	/**
	 * Returns a string representation of a node. Will be overriden by more specific types of nodes.
	 */
	@Override
	public String toString(){
		return "";
	}
}
