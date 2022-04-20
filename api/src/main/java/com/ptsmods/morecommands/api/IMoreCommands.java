package com.ptsmods.morecommands.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IMoreCommands {
	Logger LOG = LogManager.getLogger("MoreCommands");

	static IMoreCommands get() {
		return Holder.getMoreCommands();
	}

	boolean isServerOnly();
}
