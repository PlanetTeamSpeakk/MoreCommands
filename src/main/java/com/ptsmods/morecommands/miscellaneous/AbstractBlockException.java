package com.ptsmods.morecommands.miscellaneous;

public class AbstractBlockException extends Exception {

	private static final long serialVersionUID = 9012639089826307308L;

	public AbstractBlockException() {
		super();
	}

	public AbstractBlockException(String message) {
		super(message);
	}

	public AbstractBlockException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbstractBlockException(Throwable cause) {
		super(cause);
	}

}
