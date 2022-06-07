package com.ptsmods.morecommands.forge;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(MoreCommands.MOD_ID)
public class MoreCommandsForge {
    private static final Map<String, PermissionNode<Boolean>> permissionNodes = new LinkedHashMap<>();

    public MoreCommandsForge() {
        EventBuses.registerModEventBus(MoreCommands.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MoreCommands.init();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInitClient(FMLClientSetupEvent event) {
        MoreCommandsClient.init();
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        MoreCommands.getPermissions().forEach((permission, defaultValue) -> {
            PermissionNode<Boolean> permissionNode = new PermissionNode<>(new Identifier("morecommands:" + (permission.startsWith("morecommands.") ? permission.substring("morecommands.".length()) : permission)),
                    PermissionTypes.BOOLEAN, (player, uuid, permissionDynamicContexts) -> defaultValue);
            event.addNodes(permissionNode);
            permissionNodes.put(permission, permissionNode);
        });
    }

    public static PermissionNode<Boolean> getPermissionNode(String permission) {
        return permissionNodes.get(permission);
    }
}
