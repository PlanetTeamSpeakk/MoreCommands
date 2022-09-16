package com.ptsmods.morecommands.forge;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onPermissionGather);
        MinecraftForge.EVENT_BUS.addListener(this::onCreateFluidSource);
        MoreCommands.init();
    }

    @SubscribeEvent
    public void onInitClient(FMLClientSetupEvent event) {
        MoreCommandsClient.init();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegister(RegisterEvent event) {
        if (event.getRegistryKey() != Registry.ATTRIBUTE_REGISTRY) return;
        MoreCommands.registerAttributes(false);
    }

    public static PermissionNode<Boolean> getPermissionNode(String permission) {
        return permissionNodes.get(permission);
    }

    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        MoreCommands.getPermissions().forEach((permission, defaultValue) -> {
            PermissionNode<Boolean> permissionNode = new PermissionNode<>(new ResourceLocation("morecommands:" +
                    (permission.startsWith("morecommands.") ? permission.substring("morecommands.".length()) : permission)),
                    PermissionTypes.BOOLEAN, (player, uuid, permissionDynamicContexts) -> defaultValue);
            event.addNodes(permissionNode);
            permissionNodes.put(permission, permissionNode);
        });
    }

    public void onCreateFluidSource(BlockEvent.CreateFluidSourceEvent event) {
        if (event.getLevel() instanceof Level && ((Level) event.getLevel()).getGameRules().getBoolean(MoreGameRules.get().fluidsInfiniteRule()))
            event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        MoreCommands.registerAttributes(true);
    }
}
