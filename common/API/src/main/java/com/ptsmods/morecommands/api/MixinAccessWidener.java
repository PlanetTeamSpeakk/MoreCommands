package com.ptsmods.morecommands.api;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

// Utility class so Compat subprojects may use certain mixins from the main project.
public interface MixinAccessWidener {

    static MixinAccessWidener get() {
        return Holder.getMixinAccessWidener();
    }

    Map<Class<?>, ?> argumentTypes$getClassMap();

    void serverPlayerEntity$setSyncedExperience(ServerPlayerEntity player, int experience);

    char serverPlayNetworkHandler$gameMsgCharAt(ServerPlayNetworkHandler thiz, String string, int index, ServerPlayerEntity player, MinecraftServer server);

    Text[] signBlockEntity$getTexts(SignBlockEntity sbe);

    void doMultiDoorInteract(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cbi);
}
