package com.ptsmods.morecommands.forge.compat;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.ForgeCompatAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ForgeCompat17 extends ForgeCompatAdapter {
    private boolean initialised = false;
    private final Queue<Triple<String, Integer, String>> permissionQueue = new ConcurrentLinkedQueue<>();

    @Override
    public boolean shouldRegisterListeners() {
        return Version.getCurrent().isOlderThanOrEqual(Version.V1_18);
    }

    @Override
    public void registerListeners() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Listener listener = new Listener();
        modEventBus.addGenericListener(Attribute.class, listener::registerAttributes);
        modEventBus.addListener(listener::onInit);
        modEventBus.addListener(listener::onClientInit);
    }

    @Override
    public void registerPermission(String permission, int defaultLevel, String desc) {
        if (initialised)
            PermissionAPI.registerNode(permission, DefaultPermissionLevel.values()[defaultLevel], desc);
        else permissionQueue.add(Triple.of(permission, defaultLevel, desc));
    }

    @Override
    public boolean checkPermission(ServerPlayer player, String permission) {
        return PermissionAPI.hasPermission(player, permission);
    }

    private class Listener {
        public void registerAttributes(RegistryEvent.Register<Attribute> event) {
            Attribute reachAttribute = IMoreCommands.get().getReachAttribute().get();
            if (reachAttribute.getRegistryName() == null)
                reachAttribute.setRegistryName(new ResourceLocation("morecommands", "reach"));

            Attribute swimSpeedAttribute = IMoreCommands.get().getSwimSpeedAttribute().get();
            if (swimSpeedAttribute.getRegistryName() == null)
                swimSpeedAttribute.setRegistryName(new ResourceLocation("morecommands", "swim_speed"));

            event.getRegistry().register(reachAttribute);
            event.getRegistry().register(swimSpeedAttribute);
        }

        public void onInit(FMLCommonSetupEvent event) {
            initialised = true;

            while (!permissionQueue.isEmpty()) {
                Triple<String, Integer, String> permission = permissionQueue.remove();
                registerPermission(permission.getLeft(), permission.getMiddle(), permission.getRight());
            }
        }

        public void onClientInit(FMLClientSetupEvent event) {
            IMoreCommandsClient.get().getKeyMappings().forEach(ClientRegistry::registerKeyBinding);
        }
    }
}
