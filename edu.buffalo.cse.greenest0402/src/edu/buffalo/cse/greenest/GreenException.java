package edu.buffalo.cse.greenest;

public class GreenException extends Throwable {
	private static final long serialVersionUID = -294569740687861727L;

	public GreenException() {}
	
	public GreenException(String message) {
		super(message);
	}
	
	public GreenException(Throwable cause) {
		super(cause);
	}
	
	public GreenException(String message, Throwable cause) {
		super(message, cause);
	}
	
	// This gives an error for some
//	public GreenException(String message, Throwable cause,
//			boolean enableSuppression, boolean writeStackTrace) {
//		super(message, cause, enableSuppression, writeStackTrace);
//	}
}
