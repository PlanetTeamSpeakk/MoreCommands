package com.ptsmods.morecommands.client.fabric;

import com.ptsmods.morecommands.client.MoreCommandsClient;
import net.fabricmc.api.ClientModInitializer;

public class MoreCommandsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoreCommandsClient.init();
    }
}
