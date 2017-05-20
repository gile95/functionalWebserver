package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * This class executes given script.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class SmartScriptEngine {

	/**
	 * A node representing an entire document.
	 */
	private DocumentNode documentNode;
	/**
	 * Context of a request.
	 */
	private RequestContext requestContext;
	/**
	 * Stack which is storage for objects of type {@link ValueWrapper}.
	 */
	private ObjectMultistack multistack = new ObjectMultistack();
	/**
	 * {@link INodeVisitor} for given document node.
	 */
	private INodeVisitor visitor = new TreeExecutor();

	/**
	 * Creates a new {@link SmartScriptEngine}.
	 * @param documentNode A node representing an entire document.
	 * @param requestContext Context of a request.
	 */
	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}

	/**
	 * Executes the given script.
	 */
	public void execute() {
		documentNode.accept(visitor);
	}
	
	/**
	 * Implementation of {@link INodeVisitor} which executes the given script.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	private class TreeExecutor implements INodeVisitor{

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			ValueWrapper endExpression = new ValueWrapper(node.getEndExpression().asText());
			String variable = node.getVariable().asText();
			multistack.push(variable, new ValueWrapper(node.getStartExpression().asText()));
			while(true){
				ValueWrapper currentExpression = multistack.peek(variable);
				if(currentExpression.numCompare(endExpression.getValue()) > 0) break;
				for(int i = 0; i < node.numberOfChildren(); i++){
					node.getChild(i).accept(this);
				}
				ValueWrapper currentExpr = multistack.pop(variable);
				currentExpr.increment(node.getStepExpression().asText());
				multistack.push(variable, currentExpr);
				if(currentExpr.numCompare(endExpression.getValue()) > 0) break;
			}
			multistack.pop(variable);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			Stack<Object> stack = new Stack<>();
			for(Element e : node.getElements()){
				if(e instanceof ElementConstantDouble){
					stack.push(((ElementConstantDouble) e).getValue());
				}
				else if(e instanceof ElementConstantInteger){
					stack.push(((ElementConstantInteger) e).getValue());
				}
				else if(e instanceof ElementString){
					stack.push(((ElementString) e).getValue());
				}
				else if(e instanceof ElementVariable){
					String variable = e.asText();
					stack.push(multistack.peek(variable).getValue());
				}
				else if(e instanceof ElementOperator){
					String operator = e.asText();
					ValueWrapper firstOperator = new ValueWrapper(stack.pop());
					Object secondOperator = stack.pop();
					if(operator.equals("+")){
						firstOperator.increment(secondOperator);
					}
					else if(operator.equals("-")){
						firstOperator.decrement(secondOperator);
					}
					else if(operator.equals("*")){
						firstOperator.multiply(secondOperator);
					}
					else if(operator.equals("/")){
						firstOperator.divide(secondOperator);
					}
					stack.push(firstOperator.getValue());
				}
				else if(e instanceof ElementFunction){
					String function = e.asText();
					Functions.functions.get(function).execute(stack, requestContext);
				}
			}
			Stack<Object> temp = new Stack<>();
			int size = stack.size();
			for(int i = 0; i < size; i++){
				temp.push(stack.pop());
			}
			for(int i = 0; i < size; i++){
				try {
					requestContext.write(temp.pop().toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for(int i = 0; i < node.numberOfChildren(); i++){
				node.getChild(i).accept(this);
			}
			try {
				requestContext.write("\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
