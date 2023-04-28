package com.ptsmods.morecommands.forge.compat;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.ForgeCompatAdapter;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

public class ForgeCompat19 extends ForgeCompatAdapter {

    @Override
    public boolean shouldRegisterListeners() {
        return Version.getCurrent().isNewerThanOrEqual(Version.V1_19);
    }

    @Override
    public void registerListeners() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Listener listener = new Listener();
        modEventBus.addListener(listener::onRegisterKeyMappings);
        modEventBus.addListener(listener::onRegister);
    }

    private static class Listener {
        private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            IMoreCommandsClient.get().getKeyMappings().forEach(event::register);
        }

        private void onRegister(RegisterEvent event) {
            if (!event.getRegistryKey().registry().toString().equals("minecraft:attribute")) return;
            IMoreCommands.get().registerAttributes(false);
        }
    }
}
