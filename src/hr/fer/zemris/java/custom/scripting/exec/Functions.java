package hr.fer.zemris.java.custom.scripting.exec;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import hr.fer.zemris.java.webserver.RequestContext;

/**
 * This class is storage for functions which are supported in {@link SmartScriptEngine} to execute the scripts.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class Functions {

	/**
	 * Map which contains supported functions.
	 */
	public static Map<String, IFunction> functions = new HashMap<>();
	
	static{
		functions.put("sin", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Double num = convertToDouble(stack.pop());
				stack.push(Math.sin(Math.toRadians(num)));
			}
		});
		
		functions.put("decfmt", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				String format = (String) stack.pop();
				Double num = convertToDouble(stack.pop());
				
				DecimalFormat df = new DecimalFormat(format);
				stack.push(df.format(num));
			}
		});
		
		functions.put("dup", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Object temp = new Object(); 
				temp = stack.peek();
				stack.push(temp);
			}
		});
		
		functions.put("swap", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Object a = stack.pop();
				Object b = stack.pop();
				stack.push(a);
				stack.push(b);
			}
		});
		
		functions.put("setMimeType", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				requestContext.setMimeType((String) stack.pop());
			}
		});
		
		functions.put("paramGet", new IFunction() {
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Object defValue = stack.pop();
				String name = (String)stack.pop();
				
				String value = requestContext.getParameter(name);
				
				stack.push(value == null ? defValue : value);
			}
		});
		
		functions.put("pparamGet", new IFunction() {
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Object defValue = stack.pop();
				String name = (String)stack.pop();
				
				String value = requestContext.getPersistentParameter(name);
				
				stack.push(value == null ? defValue : value);
			}
		});
		
		functions.put("pparamSet", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				String name = (String) stack.pop();
				String value = String.valueOf(stack.pop());
				
				requestContext.setPersistentParameter(name, value);
			}
		});
		
		functions.put("pparamDel", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				String name = (String) stack.pop();
				
				requestContext.removePersistentParameter(name);
			}
		});
		
		functions.put("tparamGet", new IFunction() {
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				Object defValue = stack.pop();
				String name = (String)stack.pop();
				
				String value = requestContext.getTemporaryParameter(name);
				
				stack.push(value == null ? defValue : value);
			}
		});
		
		functions.put("tparamSet", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				String name = (String) stack.pop();
				String value = String.valueOf(stack.pop());
				
				requestContext.setTemporaryParameter(name, value);
			}
		});
		
		functions.put("tparamDel", new IFunction(){
			@Override
			public void execute(Stack<Object> stack, RequestContext requestContext) {
				String name = (String) stack.pop();
				
				requestContext.removeTemporaryParameter(name);
			}
		});
	}

	/**
	 * Helper method which converts given object in double.
	 * @param number Object to be converted.
	 * @return Double number.
	 */
	protected static Double convertToDouble(Object number) {
		return Double.valueOf(String.valueOf(number));
	}
}
