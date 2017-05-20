package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;
import java.util.Iterator;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Implementation of {@link IWebWorker} which outputs the given parameter on the specified output.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class EchoParams implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) {
		
		context.setMimeType("text/html");
		
		String content = null;
		content = "<html><body><table>";
		
		for(Iterator<String> it = context.getParameterNames().iterator(); it.hasNext();){
			content += "<tr><td>";
			String next = it.next();
			content += next + "</td><td>";
			content += context.getParameter(next);
			content += "</td></tr>";
		}
		
		content += "</table></body></html>";
		
		try {
			context.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
