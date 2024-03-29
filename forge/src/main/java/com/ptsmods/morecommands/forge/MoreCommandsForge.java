package com.ptsmods.morecommands.forge;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.forge.compat.Compat;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoreCommands.MOD_ID)
public class MoreCommandsForge {
    public MoreCommandsForge() {
        EventBuses.registerModEventBus(MoreCommands.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        MoreCommands.registerStuff(); // Make sure reachAttribute and swimSpeedAttribute are initialised.

        Compat.INSTANCE.registerListeners();
    }

    @SubscribeEvent
    public void onInit(FMLCommonSetupEvent event) {
        MoreCommands.init();
    }

    @SubscribeEvent
    public void onInitClient(FMLClientSetupEvent event) {
        MoreCommandsClient.init();
    }

    @SubscribeEvent
    public void onPostInit(FMLLoadCompleteEvent event) {
        MoreCommands.getPermissions().forEach((permission, defaultValue) -> Compat.INSTANCE.registerPermission(permission, defaultValue ? 0 : 2, ""));
        PostInitEvent.EVENT.invoker().postInit();
    }

    public static boolean checkPermission(ServerPlayer player, String permission) {
        return Compat.INSTANCE.checkPermission(player, permission);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        MoreCommands.INSTANCE.registerAttributes(true);
    }
}
