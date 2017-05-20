package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * A node representing a single for-loop construct
 * @author Mislav Gillinger
 * @version 1.0
 */

public class ForLoopNode extends Node{

	/** First argument of a for loop. Variable which receives a value of a second argument */
	private ElementVariable variable;
	/** Second argument of a classic for loop. Value on which a first argument will be initialized. */
	private Element startExpression;
	/** Third argument of a for loop, second argument progresses to this value. */
	private Element endExpression;
	/** Fourth element of a for loop, step of an iteration. Can be null. */
	private Element stepExpression;
	
	/**
	 * Constructor which creates a new for-loop based on given arguments
	 * @param variable First argument of a for loop. Variable which receives a value of a second argument
	 * @param startExpression Second argument of a classic for loop. Value on which a first argument will be initialized.
	 * @param endExpression Third argument of a for loop, second argument progresses to this value.
	 * @param stepExpression Fourth element of a for loop, step of an iteration. Can be null.
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression,
						Element endExpression, Element stepExpression){
		if(variable == null){
			throw new SmartScriptParserException();
		}
		if(startExpression == null){
			throw new SmartScriptParserException();
		}
		if(endExpression == null){
			throw new SmartScriptParserException();
		}
		
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}
	
	/**
	 * Getter for variable 
	 * @return First argument of a for loop. Variable which receives a value of a second argument
	 */
	public ElementVariable getVariable(){
		return variable;
	}
	
	/**
	 * Getter for startExpression
	 * @return Second argument of a classic for loop. Value on which a first argument will be initialized.
	 */
	public Element getStartExpression(){
		return startExpression;
	}
	
	/**
	 * Getter for endExpression
	 * @return Third argument of a for loop, second argument progresses to this value.
	 */
	public Element getEndExpression(){
		return endExpression;
	}
	
	/**
	 * Getter for stepExpression
	 * @return Fourth element of a for loop, step of an iteration. Can be null.
	 */
	public Element getStepExpression(){
		return stepExpression;
	}
	
	/**
	 * Returns a String representation of a for-loop in format {$FOR i 1 2 3 $}
	 */
	@Override
	public String toString(){
		if(stepExpression != null){
			return "{$FOR " + variable.asText() + " " + startExpression.asText()+ " " + endExpression.asText()+ " " + stepExpression.asText() + " $}";
		}
		else{
			return "{$FOR " + variable.asText() + " " + startExpression.asText()+ " " + endExpression.asText() + " $}";
		}
	}
	
	public void accept(INodeVisitor nodeVisitor){
		nodeVisitor.visitForLoopNode(this);
	}
}
