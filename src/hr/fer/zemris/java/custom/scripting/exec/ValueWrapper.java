package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Class that represents a wrapper around the value. Supports incrementing, decrementing, multiplying, dividing and
 * comparing the stored value with the given one.
 * Operations are only supported for types Integer, Double and their String representations.
 * In supported operations values which are null are treated as Integers with value 0.
 * @author Mislav Gillinger
 * @version 1.0
 */
public class ValueWrapper {

	/**
	 * Value stored in {@link ValueWrapper}.
	 */
	private Object value;
	
	/**
	 * Creates a new ValueWrapper with the given value.
	 * @param value Value to be stored in a {@link ValueWrapper}.
	 */
	public ValueWrapper(Object value){
		this.value = value;
	}
	
	/**
	 * Fetches the value stored in this {@link ValueWrapper}.
	 * @return The value stored in this {@link ValueWrapper}.
	 */
	public Object getValue(){
		return value;
	}
	
	/**
	 * Sets the value in this {@link ValueWrapper} on the given one.
	 * @param value Value in this {@link ValueWrapper} will be set to this one.
	 */
	public void setValue(Object value){
		this.value = value;
	}
	
	/**
	 * Increments the value stored in this {@link ValueWrapper} for the given one.
	 * Operation is only supported for types Integer, Double and their String representations.
	 * @param incValue Value stored in this {@link ValueWrapper} will be incremented for this one.
	 */
	public void increment(Object incValue){
		value = checkType(value);
		incValue = checkType(incValue);
		
		value = processOperation(OperationType.INCREMENT, value, incValue);
	}
	
	/**
	 * Decrements the value stored in this {@link ValueWrapper} for the given one.
	 * Operation is only supported for types Integer, Double and their String representations.
	 * @param decValue Value stored in this {@link ValueWrapper} will be decremented for this one.
	 */
	public void decrement(Object decValue){
		value = checkType(value);
		decValue = checkType(decValue);
		
		value = processOperation(OperationType.DECREMENT, value, decValue);
	}
	
	/**
	 * Multiplies the value stored in this {@link ValueWrapper} with the given one and updates it.
	 * Operation is only supported for types Integer, Double and their String representations.
	 * @param mulValue Value stored in this {@link ValueWrapper} will be multiplied with this one and updated.
	 */
	public void multiply(Object mulValue){
		value = checkType(value);
		mulValue = checkType(mulValue);
		
		value = processOperation(OperationType.MULTIPLY, value, mulValue);
	}
	
	/**
	 * Divides the value stored in this {@link ValueWrapper} with the given one and updates it.
	 * Operation is only supported for types Integer, Double and their String representations.
	 * @param divValue Value stored in this {@link ValueWrapper} will be divided with this one and updated.
	 */
	public void divide(Object divValue){
		value = checkType(value);
		divValue = checkType(divValue);
		
		value = processOperation(OperationType.DIVIDE, value, divValue);
	}
	
	/**
	 * Compares the given value to the one stored in this {@link ValueWrapper}.
	 * Operation is only supported for types Integer, Double and their String representations.
	 * If the given value is equals to this value, returns 0. If the stored value is less than the given one, 
	 * returns a negative number, and if the stored value is greater than the given one returns a positive number. 
	 * @param withValue Value which will be compared to the stored one.
	 * @return Zero if the two values are equal. Number less than zero if the stored value is less than the given one,
	 * and a number greater than zero if the stored value is greater than the given one.
	 */
	public int numCompare(Object withValue){
		value = checkType(value);
		withValue = checkType(withValue);
		
		Object result =  processOperation(OperationType.DECREMENT, value, withValue);
		
		if(result instanceof Integer){
			result = ((Integer) result).doubleValue();
		}
		
		if((Double)result > 0){
			return 1;
		}
		if((Double)result < 0){
			return -1;
		}
		return 0;
	}
	
	/**
	 * Checks if the type of the given value is Integer, Double or String. If value is null, returns new Integer with
	 * value 0. String representation of a number is processed to Integer or Double.
	 * @param value Value to be checked.
	 * @return Value of type Integer or String.
	 * @throws RuntimeException If value is one of not supported types.
	 */
	private Object checkType(Object value){
		if(!(value == null || value instanceof Integer || value instanceof Double || value instanceof String)){
			throw new RuntimeException("ValueWrapper can only work with null, or types Integer, Double or String!");
		}
		if(value == null){
			return new Integer(0);
		}
		if(value instanceof String){
			return processString((String)value);
		}
		return value;
	}
	
	/**
	 * Applies the given operation on the given values. If one operand is Double, and the other one is Integer,
	 * result will be a Double.
	 * @param operation Operation to execute.
	 * @param value First operand. 
	 * @param value1 Second operand.
	 * @return Result of the operation.
	 */
	private Object processOperation(OperationType operation, Object value, Object value1){
		
		if(value instanceof Double && value1 instanceof Double){
			if(operation == OperationType.INCREMENT) return (Double)value + (Double)value1;
			if(operation == OperationType.DECREMENT) return (Double)value - (Double)value1;
			if(operation == OperationType.MULTIPLY) return (Double)value * (Double)value1;
			else{
				if((Double)value1 == 0){
					throw new ArithmeticException("Cannot divide with zero!");
				}
				return (Double)value / (Double)value1;
			}
		}
		else if(value instanceof Double && value1 instanceof Integer){
			return processDI(operation, (Double)value, (Integer)value1);
		}
		else if(value instanceof Integer && value1 instanceof Double){
			return processID(operation, (Integer)value, (Double)value1);
		}
		else{
			if(operation == OperationType.INCREMENT) return (Integer)value + (Integer)value1;
			if(operation == OperationType.DECREMENT) return (Integer)value - (Integer)value1;
			if(operation == OperationType.MULTIPLY) return (Integer)value * (Integer)value1;
			else{
				if((Integer)value1 == 0){
					throw new ArithmeticException("Cannot divide with zero!");
				}
				return (Integer)value / (Integer)value1;
			}
		}
		
	}
	
	/**
	 * Converts a String representation of a number to an Integer or Double.
	 * @param value String representation of a number.
	 * @return New Integer or Double.
	 */
	private Object processString(String value){
		try{
			if(value.contains("E") || value.contains(".")){
				return Double.parseDouble(value);
			}
			else{
				return Integer.parseInt(value);
			}
		}catch(NumberFormatException e){
			throw new RuntimeException("Cannot parse the given string!");
		}
	}

	/**
	 * Applies the given operation on an expression where the first operand is Double and the second one is Integer.
	 * @param operation Operation to execute.
	 * @param value First operand.
	 * @param value1 Second operand.
	 * @return Result of an operation.
	 */
	private Object processDI(OperationType operation, Double value, Integer value1) {
		Double doubleValue1 = value1.doubleValue();
		if(operation == OperationType.INCREMENT) return value + doubleValue1;
		if(operation == OperationType.DECREMENT) return value - doubleValue1;
		if(operation == OperationType.MULTIPLY) return value * doubleValue1;
		else{
			if(value1 == 0){
				throw new ArithmeticException("Cannot divide with zero!");
			}
			return value / doubleValue1;
		}
	}
	
	/**
	 * Applies the given operation on an expression where the first operand is Integer and the second one is Double.
	 * @param operation Operation to execute.
	 * @param value First operand.
	 * @param value1 Second operand.
	 * @return Result of an operation.
	 */
	private Object processID(OperationType operation, Integer value, Double value1) {
		Double doubleValue = value.doubleValue();
		if(operation == OperationType.INCREMENT) return doubleValue + value1;
		if(operation == OperationType.DECREMENT) return doubleValue - value1;
		if(operation == OperationType.MULTIPLY) return doubleValue * value1;
		else{
			if(value1 == 0){
				throw new ArithmeticException("Cannot divide with zero!");
			}
			return doubleValue / value1;
		}
	}
}
