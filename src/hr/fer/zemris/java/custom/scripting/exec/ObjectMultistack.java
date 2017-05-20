package hr.fer.zemris.java.custom.scripting.exec;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a collection which can store multiple stacks.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ObjectMultistack {

	/**
	 * Map for inner storage of stacks.
	 */
	private Map<String, MultistackEntry> map;
	
	/**
	 * Represents one entry of the stack.
	 * @author Mislav Gillinger
	 * @version 1.0
	 */
	public static class MultistackEntry{
		/** Value of the entry stored, packed in {@link ValueWrapper}. */
		private ValueWrapper valueWrapper;
		/** Reference to a next value on a stack. */
		private MultistackEntry next;
		
		/**
		 * Creates a new stack entry.
		 * @param valueWrapper Value to be stored on a stack.
		 * @param next Reference to a former top of stack.
		 */
		public MultistackEntry(ValueWrapper valueWrapper, MultistackEntry next){
			this.valueWrapper = valueWrapper;
			this.next = next;
		}

		/**
		 * Fetches the value stored in this {@link MultistackEntry}.
		 * @return The value stored in this {@link MultistackEntry}.
		 */
		public ValueWrapper getValueWrapper() {
			return valueWrapper;
		}

		/**
		 * Fetches the reference to a next {@link MultistackEntry}.
		 * @return The reference to a next {@link MultistackEntry}.
		 */
		public MultistackEntry getNext() {
			return next;
		}
	}
	
	/**
	 * Creates a new {@link ObjectMultistack}.
	 */
	public ObjectMultistack(){
		this.map = new LinkedHashMap<>();
	}
	
	/**
	 * Pushes the given {@link ValueWrapper} on the stack with the given name.
	 * @param name Key of one of the stacks.
	 * @param valueWrapper Value to be pushed.
	 */
	public void push(String name, ValueWrapper valueWrapper){
		if(name == null || valueWrapper == null){
			throw new IllegalArgumentException("Arguments in method push must not be null!");
		}
		
		map.put(name, new MultistackEntry(valueWrapper, map.get(name)));
	}
	
	/**
	 * Pops the last stored element on the given stack.
	 * @param name Key of a stack from which the last pushed element will be popped.
	 * @return The last stored element on the given stack.
	 */
	public ValueWrapper pop(String name){
		if(name == null){
			throw new IllegalArgumentException("Arguments in method pop must not be null!");
		}
		if(map.get(name) == null){
			throw new NullPointerException("Stack does not contain the wanted element");
		}
		
		ValueWrapper ret = map.get(name).getValueWrapper();
		map.put(name, map.get(name).getNext());
		return ret;
	}
	
	/**
	 * Returns the last stored element on the given stack, but does not pop it.
	 * @param name The name of a stack from which the last pushed element will be returned.
	 * @return The last stored element on the given stack, but does not pop it.
	 */
	public ValueWrapper peek(String name){
		if(name == null){
			throw new IllegalArgumentException("Arguments in method pop must not be null!");
		}
		if(map.get(name) == null){
			throw new NullPointerException("Stack does not contain the wanted element");
		}
		
		return map.get(name).getValueWrapper();
	}
	
	/**
	 * Checks whether the given stack is empty.
	 * @param name Key of the checked stack.
	 * @return True if the given stack is empty, false otherwise.
	 */
	public boolean isEmpty(String name){
		return map.get(name) == null;
	}
}
