package com.ptsmods.morecommands.fabric;

import com.ptsmods.morecommands.MoreCommands;
import net.fabricmc.api.ModInitializer;

public class MoreCommandsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MoreCommands.init();
        MoreCommands.INSTANCE.registerAttributes(true);
    }
}
