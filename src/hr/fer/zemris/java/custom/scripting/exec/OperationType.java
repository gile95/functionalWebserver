package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Types of operations ValueWrapper supports.
 * @author Mislav Gillinger
 * @version 1.0
 */
public enum OperationType {
	/** Incrementing the stored value. */
	INCREMENT,
	/** Decrementing the stored value. */
	DECREMENT, 
	/** Multiplying the stored value. */
	MULTIPLY, 
	/** Dividing the stored value. */
	DIVIDE;
}
