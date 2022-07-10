package com.ptsmods.morecommands.api;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

// Utility class so Compat subprojects may use certain mixins from the main project.
public interface MixinAccessWidener {

    static MixinAccessWidener get() {
        return Holder.getMixinAccessWidener();
    }

    Map<Class<?>, ?> argumentTypes$getClassMap();

    void serverPlayerEntity$setSyncedExperience(ServerPlayer player, int experience);

    char serverPlayNetworkHandler$gameMsgCharAt(ServerGamePacketListenerImpl thiz, String string, int index, ServerPlayer player, MinecraftServer server);

    Component[] signBlockEntity$getTexts(SignBlockEntity sbe);

    void doMultiDoorInteract(MultiPlayerGameMode interactionManager, LocalPlayer player, ClientLevel world, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cbi);
}
