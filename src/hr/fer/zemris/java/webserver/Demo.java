package hr.fer.zemris.java.webserver;

/**
 * Runs the {@link SmartHttpServer}. Expects path to a file with server properties as a command
 * Once when its running, try links:
 * http://localhost:5721/index.html
 * http://localhost:5721/scripts/doc1.smscr
 * line argument.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class Demo {

	/**
	 * Program execution starts with this method.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		
		SmartHttpServer shs = null;
		try {
			shs = new SmartHttpServer(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		shs.start();
		
	}
}
