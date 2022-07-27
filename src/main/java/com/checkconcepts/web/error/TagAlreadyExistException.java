package com.checkconcepts.web.error;

public class TagAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3645885021721960627L;

	public TagAlreadyExistException() {
		super();
	}

	public TagAlreadyExistException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public TagAlreadyExistException(final String message) {
		super(message);
	}

	public TagAlreadyExistException(final Throwable cause) {
		super(cause);
	}



}
