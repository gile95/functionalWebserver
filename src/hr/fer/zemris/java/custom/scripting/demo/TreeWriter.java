package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Program that demonstrates the work of {@link SmartScriptParser}.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class TreeWriter {

	/**
	 * Program execution starts with this method.
	 * @param args Command line arguments.
	 * @throws IOException If an IO Error occurs.
	 */
	public static void main(String[] args) throws IOException {
		
		String docBody = new String(Files.readAllBytes(Paths.get("webroot/scripts/brojPoziva.smscr")), StandardCharsets.UTF_8);
		
		SmartScriptParser parser = null;
		try {
			parser = new SmartScriptParser(docBody);
		} catch (SmartScriptParserException e) {
			e.printStackTrace();
			System.out.println("Unable to parse document!");
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception");
			System.exit(-1);
		}
		
		WriterVisitor visitor = new WriterVisitor();
		
		parser.getDocumentNode().accept(visitor);
	}
	
	/**
	 * Implementation of {@link INodeVisitor} which prints the script to the standard output.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	private static class WriterVisitor implements INodeVisitor{

		@Override
		public void visitTextNode(TextNode node) {
			System.out.print(node.toString());
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.print(node.toString());
			for(int i = 0; i < node.numberOfChildren(); i++){
				node.getChild(i).accept(this);
			}
			System.out.print("{$END$}");
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.print(node.toString());
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			System.out.print(node.toString());
			for(int i = 0; i < node.numberOfChildren(); i++){
				node.getChild(i).accept(this);
			}	
		}
	}
}