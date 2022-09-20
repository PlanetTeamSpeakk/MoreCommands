package com.ptsmods.morecommands.fabric;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class MoreCommandsFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        MoreCommands.init();
        MoreCommands.INSTANCE.registerAttributes(true);
    }

    @Override
    public void onInitializeClient() {
        MoreCommandsClient.init();
    }
}
