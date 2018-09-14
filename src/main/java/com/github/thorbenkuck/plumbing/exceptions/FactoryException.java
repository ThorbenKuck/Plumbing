package com.github.thorbenkuck.plumbing.exceptions;

public class FactoryException extends Exception {

	public FactoryException() {
	}

	public FactoryException(String message) {
		super(message);
	}

	public FactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public FactoryException(Throwable cause) {
		super(cause);
	}

	public FactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
