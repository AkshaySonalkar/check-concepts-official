package com.checkconcepts.web.error;

public class CategoryAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4011904416146696834L;
	
	public CategoryAlreadyExistException() {
        super();
    }

    public CategoryAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CategoryAlreadyExistException(final String message) {
        super(message);
    }

    public CategoryAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
