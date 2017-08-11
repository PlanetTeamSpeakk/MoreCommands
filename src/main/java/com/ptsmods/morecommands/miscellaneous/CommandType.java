package com.ptsmods.morecommands.miscellaneous;

public enum CommandType {
	CLIENT(), SERVER(), UNKNOWN(); // Making the getCommandType() method return CommandType.CLIENT makes it load client sided, making it return CommandType.SERVER makes it load server sided, making it return CommandType.UNKOWN doesn't load it.
	
	/**
	 * CommandType.CLIENT returns "CLIENT", CommandType.SERVER returns "SERVER", CommandType.UNKNOWN returns "UNKNOWN" and if you somehow managed to get another value it returns "INVALID".
	 */
	@Override
	public String toString() {
		if (this == CommandType.CLIENT) return "CLIENT";
		else if (this == CommandType.SERVER) return "SERVER";
		else if (this == CommandType.UNKNOWN) return "UNKNOWN";
		else return "INVALID";
	}
}
