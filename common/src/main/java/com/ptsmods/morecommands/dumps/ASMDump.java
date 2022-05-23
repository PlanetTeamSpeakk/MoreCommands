package com.ptsmods.morecommands.dumps;

import dev.architectury.platform.Platform;

public class ASMDump {
    private static final boolean notDevEnv = !Platform.isDevelopmentEnvironment();

    public static String map(String intermediary, String yarn, String moj) {
        return Platform.isForge() ? moj : notDevEnv ? intermediary : yarn;
    }
}
