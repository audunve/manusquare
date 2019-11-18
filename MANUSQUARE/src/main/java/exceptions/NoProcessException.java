package exceptions;

/**
 * This exception arises when there are no processes specified in a ConsumerQuery
 * @author audunvennesland
 *
 */
public class NoProcessException extends RuntimeException { 
	
	public NoProcessException (String error) {
		super(error);
	}

}
