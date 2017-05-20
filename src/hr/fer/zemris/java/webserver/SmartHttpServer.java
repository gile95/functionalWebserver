package hr.fer.zemris.java.webserver;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

/**
 * This class is an implementation of a HTTP Server. It is able to show text pages, html pages, images
 * and it is able to execute scripts.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class SmartHttpServer {

	/**
	 * Address of this server.
	 */
	private String address;
	/**
	 * Port on which server listens.
	 */
	private int port;
	/**
	 * Number of worker threads.
	 */
	private int workerThreads;
	/**
	 * Root of a parsed document.
	 */
	private Path documentRoot;
	/**
	 * Map of types which server recognizes.
	 */
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	/**
	 * Number of session timeout, in seconds.
	 */
	private int sessionTimeout;
	/**
	 * Map of {@link IWebWorker}s.
	 */
	private Map<String,IWebWorker> workersMap = new HashMap<String, IWebWorker>();
	
	/**
	 * Thread which runs this server.
	 */
	private ServerThread serverThread;
	/**
	 * Thread pool.
	 */
	private ExecutorService threadPool;
	
	/**
	 * Map of sessions.
	 */
	private Map<String, SessionMapEntry> sessions = new HashMap<String, SmartHttpServer.SessionMapEntry>();
	/**
	 * Variable for generating random numbers.
	 */
	private Random sessionRandom = new Random();

	/**
	 * Thread which checks whether there are expired sessions.
	 */
	final TimerTask killSessions = new TimerTask() {

		@Override
		public void run() {
			for(String session : sessions.keySet()){
				if(sessions.get(session).validUntil < new Date().getTime()/1000) sessions.remove(session);
			}
		}

	};
	
	/**
	 * Represents one session.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	private static class SessionMapEntry {
		/**
		 * Session ID is a string containing 20 upper case random letters.
		 */
		@SuppressWarnings("unused")
		String sid;
		/**
		 * Represents for how long the session will be valid.
		 */
		long validUntil;
		/**
		 * Map of sessions.
		 */
		Map<String,String> map;
		
		/**
		 * Creates a new {@link SessionMapEntry}.
		 * @param sid Session ID is a string containing 20 upper case random letters.
		 * @param validUntil Represents for how long the session will be valid.
		 * @param map Map of sessions.
		 */
		public SessionMapEntry(String sid, long validUntil, Map<String, String> map){
			this.sid = sid;
			this.validUntil = validUntil;
			this.map = new ConcurrentHashMap<>(map);
		}
	}
	
	/**
	 * Creates a new {@link SmartHttpServer}.
	 * @param configFileName Path to a file where server configurations are.
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws IOException If an IO Error occurs.
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws InstantiationException Instantiation exception
	 * @throws IllegalAccessException IllegalAcessException
	 */
	public SmartHttpServer(String configFileName) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		serverThread = new ServerThread();
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(configFileName));
		
		address = properties.getProperty("server.address");
		port = Integer.parseInt(properties.getProperty("server.port"));
		workerThreads = Integer.parseInt(properties.getProperty("server.workerThreads"));
		documentRoot = Paths.get(properties.getProperty("server.documentRoot"));
		getMimeTypes(properties.getProperty("server.mimeConfig"));
		sessionTimeout = Integer.parseInt(properties.getProperty("session.timeout"));
		getWorkersMap(properties.getProperty("server.workers"));
	}

	/**
	 * Starts the server thread.
	 */
	protected synchronized void start() {
		if(!serverThread.isAlive()){
			serverThread.start(); // start server thread if not already running
			threadPool = Executors.newFixedThreadPool(workerThreads); //init threadpool
			
			final Timer timer = new Timer();
			timer.schedule(killSessions, 300*1000, 300*1000);
		}
	}

	/**
	 * Stops the server thread.
	 */
	protected synchronized void stop() {
		serverThread.interrupt(); // â€¦ signal server thread to stop running â€¦
		threadPool.shutdown(); // â€¦ shutdown threadpool â€¦
	}

	/**
	 * Loads the mime types.
	 * @param mimePath mimePath
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws IOException If an IO Error occurs.
	 */
	private void getMimeTypes(String mimePath) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(mimePath));
		
		for(Object o : properties.keySet()){
			mimeTypes.put((String)o, properties.getProperty((String) o));
		}
	}
	
	/**
	 * Loads the map of workers.
	 * @param workersPath Path to a file with workers configurations.
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws IOException If an IO Error occurs.
	 * @throws ClassNotFoundException ClassNotFoundException.
	 * @throws InstantiationException InstantiationException.
	 * @throws IllegalAccessException IllegalAccessException.
	 */
	private void getWorkersMap(String workersPath) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(workersPath));
		
		for(Object o : properties.keySet()){
			String path = (String) o;
			String fqcn = properties.getProperty((String) o);
			
			Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
			Object newObject = referenceToClass.newInstance();
			IWebWorker iww = (IWebWorker)newObject;
			workersMap.put(path, iww);
		}
	}
	
	/**
	 * Represents a thread which runs the server.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	protected class ServerThread extends Thread {
		/**
		 * Server socket used for running a server.
		 */
		ServerSocket serverSocket;
		
		@Override
		public void run() {			
			try {
				serverSocket = new ServerSocket(); // open serverSocket on specified port
				serverSocket.bind(new InetSocketAddress((InetAddress)null, port)); 
			} catch (IOException e) {e.printStackTrace();}
			
			while(true) {
				Socket client = null;
				
				try {
					client = serverSocket.accept();
				} catch (IOException e) {e.printStackTrace();}
				
				ClientWorker cw = new ClientWorker(client);
				threadPool.submit(cw); // submit cw to threadpool for execution
			}
		}
		@Override
		public void interrupt() {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This thread represents a client worker of this server.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	private class ClientWorker implements Runnable {
		
		/**
		 * Client socket.
		 */
		private Socket csocket;
		/**
		 * Input stream.
		 */
		private PushbackInputStream istream;
		/**
		 * Output stream.
		 */
		private OutputStream ostream;
		/**
		 * HTTP version.
		 */
		private String version;
		/**
		 * HTTP method.
		 */
		private String method;
		/**
		 * Program parameters, given through URL.
		 */
		private Map<String, String> params = new HashMap<String, String>();
		/**
		 * Permanent parameters.
		 */
		private Map<String, String> permPrams = null;
		/**
		 * Output cookies.
		 */
		private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
		/**
		 * Session ID is a string containing 20 upper case random letters.
		 */
		private String SID;

		/**
		 * Creates a new {@link ClientWorker}.
		 * @param csocket Client socket.
		 */
		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}

		@Override
		public void run() {
			try{
				// obtain input stream from socket and wrap it to pushback input stream
				istream = new PushbackInputStream(csocket.getInputStream());
				// obtain output stream from socket
				ostream = new BufferedOutputStream(csocket.getOutputStream());
				
				// Then read complete request header from your client in separate method...
				List<String> request = readRequest();
			
				// If header is invalid (less then a line at least) return response status 400
				if(request.isEmpty() || request.get(0).split(" ").length != 3){
					sendError(ostream, 400, "Bad request");
					return;
				}
			
				String[] firstLine = request.get(0).split(" ");
		
				// Extract (method, requestedPath, version) from firstLine
				method = firstLine[0].toUpperCase();
				// if method not GET or version not HTTP/1.0 or HTTP/1.1 return response status 400
				if(!method.equals("GET")) {
					sendError(ostream, 400, "Method Not Allowed");
					return;
				}
			
				String requestedPath = firstLine[1];
				
				version = firstLine[2].toUpperCase();
				if(!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
					sendError(ostream, 400, "HTTP Version Not Supported");
					return;
				}
			
				if(request.get(0).contains("favicon")) return;
				checkSession(request);
			
				// (path, paramString) = split requestedPath to path and parameterString
				String path = requestedPath.split("\\?")[0];
			
				if(requestedPath.split("\\?").length != 1){
					String paramString = requestedPath.split("\\?")[1];
					parseParameters(paramString); //==> your method to fill map parameters
				}
			
				// requestedPath = resolve path with respect to documentRoot
				Path resolvedPath = Paths.get(documentRoot + path);
				
				// if requestedPath is not below documentRoot, return response status 403 forbidden
				if(!resolvedPath.toString().contains(documentRoot.toString())){
					sendError(ostream, 403, "Forbidden");
					return;
				}
			
				if(path.startsWith("/ext/")){
					String pathToClass = "hr.fer.zemris.java.webserver.workers.";
					String pathPart = path.split("\\?")[0];
					pathToClass += pathPart.substring(pathPart.lastIndexOf('/') + 1);
					Class<?> referenceToClass = null;
					try {
						referenceToClass = this.getClass().getClassLoader().loadClass(pathToClass);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					Object newObject = null;
					try {
						newObject = referenceToClass.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					IWebWorker iww = (IWebWorker)newObject;
					RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
					iww.processRequest(rc);
			
					ostream.flush();					
					csocket.close();
					return;
				}
			
				IWebWorker potentialWorker = workersMap.get(path);
				if(potentialWorker != null){
					RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
					potentialWorker.processRequest(rc);
				
					ostream.flush();
					csocket.close();
					return;
				}
			
				String extension = null;
				// check if requestedPath exists, is file and is readable; if not, return status 404
				if(!Files.exists(resolvedPath) || !Files.isReadable(resolvedPath)){
					sendError(ostream, 404, "Path doesn't exist or is not readable");
					return;
				}
				// else extract file extension
				else{
					extension = resolvedPath.toString().substring(resolvedPath.toString().lastIndexOf('.')+1);
				}
			
				// find in mimeTypes map appropriate mimeType for current file extension
				// (you filled that map during the construction of SmartHttpServer from mime.properties)
				String mimeType = mimeTypes.get(extension);
			
				// if no mime type found, assume application/octet-stream
				if(mimeType == null){
					mimeType = "application/octet-stream";
				}
			
				// create a rc = new RequestContext(...); set mime-type; set status to 200
				RequestContext rc = new RequestContext(ostream, params, permPrams, outputCookies);
				rc.setMimeType(mimeType);
				rc.setStatusCode(200);
				
				// If you want, you can modify RequestContext to allow you to add additional headers
				// so that you can add â€œContent-Length: 12345â€� if you know that file has 12345 bytes
			
			
				if(extension.equals("smscr")){
					String documentBody = null;
					documentBody = new String(Files.readAllBytes(resolvedPath), StandardCharsets.UTF_8);
				
					// create engine and execute it
					new SmartScriptEngine(
						new SmartScriptParser(documentBody).getDocumentNode(), rc
					).execute();
				}
				else{
					// open file, read its content and write it to rc (that will generate header and send file bytes to client)
					rc.write(Files.readAllBytes(resolvedPath));
				}
				
				ostream.flush();
				csocket.close();
			
			}catch(IOException e){
				e.printStackTrace();
			}
		}

		/**
		 * Checks whether session is still valid and refreshes parameters.
		 * @param request Client request.
		 */
		private void checkSession(List<String> request) {
			synchronized(sessions){
				String sidCandidate = null;
				SessionMapEntry session = null;
				
				for(int i = 0; i < request.size(); i++){
					if(request.get(i).startsWith("Cookie:")){
						if(request.get(i).contains("sid")){
							String[] elements = request.get(i).split(" ");
							for(String s : elements){
								if(s.startsWith("sid")){
									sidCandidate = s.split("=")[1];
									sidCandidate = sidCandidate.substring(1, sidCandidate.length()-1);
								}
							}
						}
					}
				}
				
				if(sidCandidate == null){
					Map<String, String> map = new ConcurrentHashMap<>();
					SID = getRandomString();
					session = new SessionMapEntry(SID, sessionTimeout + new Date().getTime()/1000, map); 
					
					sessions.put(SID, session);
					
					outputCookies.add(new RCCookie("sid", SID, address, "/", null));
				}
				else if (sessions.containsKey(sidCandidate)){
					
					session = sessions.get(sidCandidate);			
					
					if(new Date().getTime() - session.validUntil < 0){
						sessions.remove(session);
					}
					else{
						session.validUntil = new Date().getTime()/1000 + sessionTimeout;
					}
				}
				permPrams = session.map;
			}
		}
		
		/**
		 * Generator for {@link #SID}s.
		 * @return {@link #SID}.
		 */
		private String getRandomString() {
			String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			StringBuilder sb = new StringBuilder(20);
			for(int i = 0; i < 20; i++){
				sb.append(letters.charAt(sessionRandom.nextInt(letters.length())));
			}
			return sb.toString();
		}
		
		/**
		 * Parses input parameters.
		 * @param paramString String to parse.
		 */
		private void parseParameters(String paramString) {
			String[] mappings = paramString.split("&");
			for(String param : mappings){
				params.put(param.split("=")[0], param.split("=")[1]);
			}
		}

		/**
		 * Reads the request and returns headers.
		 * @return Headers. 
		 * @throws IOException If an IO Error occurs.
		 */
		private List<String> readRequest() throws IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int state = 0;
	l:		while(true) {
				int b = istream.read();
				if(b==-1) return null;
				if(b!=13) {
					bos.write(b);
				}
				switch(state) {
				case 0: 
					if(b==13) { state=1; } else if(b==10) state=4;
					break;
				case 1: 
					if(b==10) { state=2; } else state=0;
					break;
				case 2: 
					if(b==13) { state=3; } else state=0;
					break;
				case 3: 
					if(b==10) { break l; } else state=0;
					break;
				case 4: 
					if(b==10) { break l; } else state=0;
					break;
				}
			}
			
			if(bos.toByteArray() == null){
				sendError(ostream, 400, "Bad request");
				stop(); 
			}
			
			String requestStr = new String(bos.toByteArray(), StandardCharsets.US_ASCII);
			
			return extractHeaders(requestStr);
		}

		/**
		 * Helper method for extracting headers.
		 * @param requestHeader String to parse.
		 * @return Headers.
		 */
		private List<String> extractHeaders(String requestHeader) {
			List<String> headers = new ArrayList<String>();
			String currentLine = null;
			for(String s : requestHeader.split("\n")) {
				if(s.isEmpty()) break;
				char c = s.charAt(0);
				if(c==9 || c==32) {
					currentLine += s;
				} else {
					if(currentLine != null) {
						headers.add(currentLine);
					}
					currentLine = s;
				}
			}
			if(!currentLine.isEmpty()) {
				headers.add(currentLine);
			}
			return headers;
		}

		/**
		 * Helper method used for sending errors.
		 * @param cos Output stream.
		 * @param statusCode Status code of an error.
		 * @param statusText Status text of an error.
		 */
		private void sendError(OutputStream cos, int statusCode, String statusText){
			try {
				cos.write(
					("HTTP/1.1 "+statusCode+" "+statusText+"\r\n"+
					"Server: simple java server\r\n"+
					"Content-Type: text/plain;charset=UTF-8\r\n"+
					"Content-Length: 0\r\n"+
					"Connection: close\r\n"+
					"\r\n").getBytes(StandardCharsets.US_ASCII)
				);
				cos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
