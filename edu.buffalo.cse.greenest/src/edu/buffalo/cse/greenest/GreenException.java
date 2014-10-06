package edu.buffalo.cse.greenest;



public class GreenException extends Throwable {
	private static final long serialVersionUID = -294569740687861727L;

	/**
	 * Warns the user that a critical error has occurred. This type of error
	 * normally crashes Green.
	 */
	private static final String GRERR_REPORT_CRITICAL =
		"A critical error has occurred. You should copy this message along "
		+ "with the stack trace below and send it to the contact address "
		+ "provided with this software.";

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

	/**
	 * This method should be called when an unexpected behavior is encountered
	 * during Green's execution; no warning messages like these should ever be
	 * seen by the user; the presence of one indicates that there is a flaw in
	 * existing code.
	 * 
	 * @param message - The message to be displayed in the console.
	 */
	public static void warn(String message) {
		System.err.println("GREEN WARNING: " + message);
	}

	public static void critical(Throwable t) {
		warn(GRERR_REPORT_CRITICAL);
		t.printStackTrace();
		
	}

	public static void illegalOperation(String description) throws Exception {
		if (description == null) return;
		
		throw new Exception(description);
	}
	// This gives an error for some
//	public GreenException(String message, Throwable cause,
//			boolean enableSuppression, boolean writeStackTrace) {
//		super(message, cause, enableSuppression, writeStackTrace);
//	}
}
