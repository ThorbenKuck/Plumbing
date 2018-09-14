package com.github.thorbenkuck.plumbing;

public class ParsingException extends RuntimeException {

	ParsingException(String message) {
		super(message);
	}

	ParsingException(Throwable cause) {
		super(cause);
	}

	ParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
