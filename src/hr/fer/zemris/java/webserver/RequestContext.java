package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Context of a request.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class RequestContext {

	/**
	 * Represents a request context cookie.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	public static class RCCookie{
		/**
		 * Cookie name.
		 */
		private String name;
		/**
		 * Cookie value.
		 */
		private String value;
		/**
		 * Cookie domain.
		 */
		private String domain;
		/**
		 * Cookie path.
		 */
		private String path;
		/**
		 * Cookie max age.
		 */
		private Integer maxAge;
		
		/**
		 * Creates a new {@link RCCookie}.
		 * @param name Cookie name.
		 * @param value Cookie value.
		 * @param domain Cookie domain.
		 * @param path Cookie path.
		 * @param maxAge Cookie max age.
		 */
		public RCCookie(String name, String value, String domain, String path, Integer maxAge) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
		}

		/**
		 * Fetches the cookie name. 
		 * @return Cookie name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Fetches the cookie value. 
		 * @return Cookie value.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Fetches the cookie domain. 
		 * @return Cookie domain.
		 */
		public String getDomain() {
			return domain;
		}

		/**
		 * Fetches the cookie path. 
		 * @return Cookie path.
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Fetches the cookie max age. 
		 * @return Cookie max age.
		 */
		public Integer getMaxAge() {
			return maxAge;
		}
	}
	
	/**
	 * Output stream.
	 */
	private OutputStream outputStream;
	/**
	 * Current charset.
	 */
	private Charset charset;
	/**
	 * Current encoding.
	 */
	private String encoding = "UTF-8";
	/**
	 * Current status code.
	 */
	private int statusCode = 200;
	/**
	 * Current status text.
	 */
	private String statusText = "OK";
	/**
	 * Current mimeType.
	 */
	private String mimeType = "text/html";
	/**
	 * Parameters.
	 */
	private Map<String, String> parameters;
	/**
	 * Temporary parameters.
	 */
	private Map<String, String> temporaryParameters = new HashMap<>();
	/**
	 * Persistent parameters.
	 */
	private Map<String, String> persistentParameters;
	/**
	 * Output cookies.
	 */
	private List<RCCookie> outputCookies;
	/**
	 * Says whether header was already generated.
	 */
	private boolean headerGenerated = false;
	
	/**
	 * Creates a new {@link RequestContext}.
	 * @param outputStream Output stream.
	 * @param parameters Parameters.
	 * @param persistentParameters Persistent parameters.
	 * @param outputCookies Output cookies.
	 */
	public RequestContext(OutputStream outputStream, Map<String,String> parameters,
			Map<String,String> persistentParameters, List<RCCookie> outputCookies) {
		if(outputStream == null) throw new IllegalArgumentException("Given outputstream in constructor of class "
				+ "RequestContext must not be null!");
		this.outputStream = outputStream;
		
		this.parameters = parameters == null ? new HashMap<>() : new HashMap<String, String>(parameters);
		
		this.persistentParameters = persistentParameters == null ? new HashMap<>() : new HashMap<String, String>(persistentParameters);
		
		this.outputCookies = outputCookies == null ? new ArrayList<>() : new ArrayList<RCCookie>(outputCookies);
		
		parameters = Collections.unmodifiableMap(parameters);
	}
	
	/**
	 * Setter for encoding.
	 * @param encoding New encoding.
	 */
	public void setEncoding(String encoding){
		if(headerGenerated) throw new RuntimeException("Header was already generated!");
		this.encoding = encoding;
	}
	
	/**
	 * Setter for status code.
	 * @param statusCode New status code.
	 */
	public void setStatusCode(int statusCode){
		if(headerGenerated) throw new RuntimeException("Header was already generated!");
		this.statusCode = statusCode;
	}
	
	/**
	 * Setter for status text.
	 * @param statusText New status text.
	 */
	public void setStatusText(String statusText){
		if(headerGenerated) throw new RuntimeException("Header was already generated!");
		this.statusText = statusText;
	}
	
	/**
	 * Setter for mime type.
	 * @param mimeType New mime type.
	 */
	public void setMimeType(String mimeType){
		if(headerGenerated) throw new RuntimeException("Header was already generated!");
		this.mimeType = mimeType;
	}
	
	/**
	 * Fetches the {@link #headerGenerated} variable value.
	 * @return The {@link #headerGenerated} variable value.
	 */
	public boolean getHeadreGenerated(){
		return headerGenerated;
	}
	
	/**
	 * Method that retrieves value from parameters map (or null if no association exists).
	 * @param name Key for the wanted value. 
	 * @return Value from parameters map (or null if no association exists).
	 */
	public String getParameter(String name){
		return parameters.get(name);
	}
	
	/**
	 * Method that retrieves names of all parameters in parameters map.
	 * @return Names of all parameters in parameters map.
	 */
	public Set<String> getParameterNames(){
		return parameters.keySet();
	}
	
	/**
	 * Method that retrieves value from persistentParameters map (or null if no association exists).
	 * @param name Key for the wanted value.
	 * @return Value from persistentParameters map (or null if no association exists).
	 */
	public String getPersistentParameter(String name){
		return persistentParameters.get(name);
	}
	
	/**
	 * Method that retrieves names of all parameters in persistent parameters map.
	 * @return Names of all parameters in persistent parameters map.
	 */
	public Set<String> getPersistentParameterNames(){
		return Collections.unmodifiableSet(persistentParameters.keySet());
	}
	
    /**
     * Method that stores a value to persistentParameters map.
     * @param name Key of a new value.
     * @param value New value to be stored in persistentParameters map.
     */
	public void setPersistentParameter(String name, String value){
		persistentParameters.put(name, value);
	}
	
	/**
	 * Method that removes a value from persistentParameters map.
	 * @param name Value that is wanted to be removed.
	 */
	public void removePersistentParameter(String name){
		persistentParameters.remove(name);
	}
	
	/**
	 * Method that retrieves value from temporaryParameters map (or null if no association exists).
	 * @param name Key for the wanted value. 
	 * @return Value from temporaryParameters map (or null if no association exists).
	 */
	public String getTemporaryParameter(String name){
		return temporaryParameters.get(name);
	}
	
	/**
	 * Method that retrieves names of all parameters in temporary parameters map.
	 * @return Names of all parameters in temporary parameters map.
	 */
	public Set<String> getTemporaryParameterNames(){
		return Collections.unmodifiableSet(temporaryParameters.keySet());
	}
	
	/**
	 * Method that stores a value to temporaryParameters map.
	 * @param name Key of a new value.
	 * @param value New value to be stored.
	 */
	public void setTemporaryParameter(String name, String value){
		temporaryParameters.put(name, value);
	}
	
	/**
	 * Method that removes a value from temporaryParameters map.
	 * @param name Value to be removed.
	 */
	public void removeTemporaryParameter(String name){
		temporaryParameters.remove(name);
	}
	
	/**
	 * Adds a new {@link RCCookie} to {@link #outputCookies}.
	 * @param cookie New cookie to be added to {@link #outputCookies}.
	 */
	public void addRCCookie(RCCookie cookie){
		if(headerGenerated) throw new RuntimeException("Header was already generated!");
		this.outputCookies.add(cookie);
	}
	
	/**
	 * Writes given data to a specified output.
	 * @param data Data to be written to a specified output.
	 * @return Returns {@link RequestContext} changed.
	 * @throws IOException If an IO Error occurs.
	 */
	public RequestContext write(byte[] data) throws IOException{
		if(!headerGenerated){
			generateHeader();
		}
		outputStream.write(data);
		
		return this;
	}
	
	/**
	 * Writes given data to a specified output.
	 * @param text Text to be written to a specified output.
	 * @return Returns {@link RequestContext} changed.
	 * @throws IOException If an IO Error occurs.
	 */
	public RequestContext write(String text) throws IOException{
		if(!headerGenerated){
			generateHeader();
		}
		byte[] data = text.getBytes(charset);
		outputStream.write(data);
		
		return this;
	}

	/**
	 * Helper method which generates header.
	 * @throws IOException If an IO Error occurs.
	 */
	private void generateHeader() throws IOException {
		headerGenerated = true;
		charset = Charset.forName(encoding);
		
		StringBuilder header = new StringBuilder();
		header.append("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
		header.append("Content-Type: " + mimeType);
		header.append(mimeType.startsWith("text/") ? "; charset=" + encoding + "\r\n" : "\r\n");
		if(!outputCookies.isEmpty()){
			for(RCCookie cookie : outputCookies){
				header.append("Set-Cookie: " + cookie.name + "=\"" + cookie.value + "\"");
				if(cookie.domain != null){
					header.append("; Domain=" + cookie.domain);
				}
				if(cookie.path != null){
					header.append("; Path=" + cookie.path);
				}
				if(cookie.maxAge != null){
					header.append("; Max-Age=" + cookie.maxAge);
				}
				header.append("\r\n");
			}
		}
		header.append("\r\n");
		
		outputStream.write(header.toString().getBytes(StandardCharsets.ISO_8859_1));
	}
} 