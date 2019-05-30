package com.ptsmods.morecommands.miscellaneous;

public class AbstractItemException extends Exception {

	private static final long serialVersionUID = -1330985482819380420L;

	public AbstractItemException() {
		super();
	}

	public AbstractItemException(String message) {
		super(message);
	}

	public AbstractItemException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbstractItemException(Throwable cause) {
		super(cause);
	}

}
