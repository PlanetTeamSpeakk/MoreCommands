package com.ptsmods.morecommands.asm;

import net.fabricmc.loader.api.FabricLoader;

public class ASMDump {
	private static final boolean notDevEnv = "intermediary".equals(FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace());

	public static String map(String name, String yarnName) {
		return notDevEnv ? name : yarnName;
	}
}
