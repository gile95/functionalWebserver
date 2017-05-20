package hr.fer.zemris.java.webserver;

/**
 * Implementations of this interface know how to write given content
 * on a web server.
 * @author Mislav Gillinger
 * @version 1.0
 */
public interface IWebWorker {

	/**
	 * Method which needs to be implemented in a way new worker will output its content.
	 * @param context Context of a request.
	 */
	public void processRequest(RequestContext context);
}
