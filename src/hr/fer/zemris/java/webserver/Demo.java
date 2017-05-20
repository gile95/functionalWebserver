package hr.fer.zemris.java.webserver;

/**
 * Runs the {@link SmartHttpServer}. Expects path to a file with server properties as a command
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
