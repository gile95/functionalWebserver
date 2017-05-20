package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A node representing an entire document
 * @author Mislav Gillinger
 * @version 1.0
 */
public class DocumentNode extends Node{

	public void accept(INodeVisitor nodeVisitor){
		nodeVisitor.visitDocumentNode(this);
	}
}
