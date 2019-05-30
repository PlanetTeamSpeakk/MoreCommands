package com.ptsmods.morecommands.miscellaneous;

public class IncorrectCommandType extends Exception {

	private static final long serialVersionUID = -2367881981193502642L;

	public IncorrectCommandType() {
		super();
	}

	public IncorrectCommandType(String message) {
		super(message);
	}

	public IncorrectCommandType(String message, Throwable cause) {
		super(message, cause);
	}

	public IncorrectCommandType(Throwable cause) {
		super(cause);
	}

}
