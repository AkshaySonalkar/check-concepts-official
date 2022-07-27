package com.checkconcepts.web.error;

public class PostAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1581658886932427502L;

	public PostAlreadyExistException() {
		super();
	}

	public PostAlreadyExistException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PostAlreadyExistException(final String message) {
		super(message);
	}

	public PostAlreadyExistException(final Throwable cause) {
		super(cause);
	}

}
