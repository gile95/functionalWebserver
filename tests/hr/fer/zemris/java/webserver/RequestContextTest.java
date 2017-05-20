package hr.fer.zemris.java.webserver;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

@SuppressWarnings("javadoc")
public class RequestContextTest {

	@Test
	public void testHeaderGenerated() throws IOException{
		OutputStream os = Files.newOutputStream(Paths.get("primjer4.txt"));
		RequestContext rc = new RequestContext(os, new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding("UTF-8");
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		// Only at this point will header be created and written...
		rc.write("Čevapčići i Šiščevapčići.");
		os.close();
		assertTrue(rc.getHeadreGenerated());
	}
	
	@Test
	public void testHeaderNotGenerated() throws IOException{
		OutputStream os = Files.newOutputStream(Paths.get("primjer4.txt"));
		RequestContext rc = new RequestContext(os, new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		assertFalse(rc.getHeadreGenerated());
	}
	
	@Test
	public void testWrite() throws IOException{
		OutputStream os = Files.newOutputStream(Paths.get("primjer4.txt"));
		RequestContext rc = new RequestContext(os, new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding("UTF-8");
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		// Only at this point will header be created and written...
		rc.write("Čevapčići i Šiščevapčići.");
		os.close();

		String content = new String(Files.readAllBytes(Paths.get("primjer4.txt")));
		
	    assertTrue(content.endsWith("Čevapčići i Šiščevapčići."));
	}
	
	@Test
	public void testMultipleWrite() throws IOException{
		OutputStream os = Files.newOutputStream(Paths.get("primjer4.txt"));
		RequestContext rc = new RequestContext(os, new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding("UTF-8");
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		// Only at this point will header be created and written...
		rc.write("Čevapčići i Šiščevapčići.").write("Blabla").write("Test");
		os.close();

		String content = new String(Files.readAllBytes(Paths.get("primjer4.txt")));
		
	    assertTrue(content.endsWith("Čevapčići i Šiščevapčići.BlablaTest"));
	}
	
	@Test
	public void testRCCookie1() throws IOException{
		OutputStream os = Files.newOutputStream(Paths.get("primjer4.txt"));
		RequestContext rc = new RequestContext(os, new HashMap<String, String>(), new HashMap<String, String>(),
				new ArrayList<RequestContext.RCCookie>());
		rc.setEncoding("UTF-8");
		rc.setMimeType("text/plain");
		rc.setStatusCode(205);
		rc.setStatusText("Idemo dalje");
		rc.addRCCookie(new RCCookie("korisnik", "perica", "127.0.0.1", "/", 3600));
		rc.addRCCookie(new RCCookie("zgrada", "B4", null, "/", null));
		// Only at this point will header be created and written...
		rc.write("Čevapčići i Šiščevapčići.");
		os.close();
		
		String content = new String(Files.readAllBytes(Paths.get("primjer4.txt")));
		
	    assertTrue(content.contains("Set-Cookie: korisnik=\"perica\"; Domain=127.0.0.1; Path=/; Max-Age=3600"));
	}
	
	@Test
	public void testRCCookie2(){
		RCCookie cookie = new RCCookie("korisnik", "perica", "127.0.0.1", "/", 3600);
		assertEquals(cookie.getName(), "korisnik");
		assertEquals(cookie.getValue(), "perica");
		assertEquals(cookie.getDomain(), "127.0.0.1");
		assertEquals(cookie.getPath(), "/");
		assertEquals(cookie.getMaxAge(), new Integer(3600));
		
	}
}
