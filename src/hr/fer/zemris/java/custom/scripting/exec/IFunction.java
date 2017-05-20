package hr.fer.zemris.java.custom.scripting.exec;

import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Every function has to implement this interface so that the method {@link #execute(Stack, RequestContext)} 
 * performs the wanted behavior.
 * @author Mislav Gillinger
 * @version 1.0
 */
public interface IFunction {

	/**
	 * Method which determines what the function will do.
	 * @param stack Stack with variables sent to function.
	 * @param requestContext Context of a request.
	 */
	void execute(Stack<Object> stack, RequestContext requestContext);
}
