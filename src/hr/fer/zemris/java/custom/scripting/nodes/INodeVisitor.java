package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Interface which declares methods needed for visiting all node.
 * Implementation is using Composite and Visitor design patterns.
 * @author Mislav Gillinger
 * @version 1.0
 */
public interface INodeVisitor {
	/**
	 * Visits {@link TextNode}.
	 * @param node Node to visit.
	 */
	public void visitTextNode(TextNode node);
	/**
	 * Visits {@link ForLoopNode}.
	 * @param node Node to visit.
	 */
	public void visitForLoopNode(ForLoopNode node);
	/**
	 * Visits {@link EchoNode}.
	 * @param node Node to visit.
	 */
	public void visitEchoNode(EchoNode node);
	/**
	 * Visits {@link DocumentNode}.
	 * @param node Node to visit.
	 */
	public void visitDocumentNode(DocumentNode node);
}
