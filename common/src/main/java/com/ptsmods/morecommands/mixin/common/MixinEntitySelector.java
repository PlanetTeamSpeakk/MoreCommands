package com.ptsmods.morecommands.mixin.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.addons.EntitySelectorAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntitySelector.class)
public class MixinEntitySelector implements EntitySelectorAddon {
    private static final @Unique SimpleCommandExceptionType NO_TARGET = new SimpleCommandExceptionType(LiteralTextBuilder.literal("You're not looking at an entity."));
    private static final @Unique SimpleCommandExceptionType NO_PLAYER_TARGET = new SimpleCommandExceptionType(LiteralTextBuilder.literal("You're not looking at a player."));
    private @Unique boolean targetOnly;

    @Override
    public boolean isTargetOnly() {
        return targetOnly;
    }

    @Override
    public void setTargetOnly(boolean targetOnly) {
        this.targetOnly = targetOnly;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/command/EntitySelector;isLocalWorldOnly()Z"), method = "getEntities(Lnet/minecraft/server/command/ServerCommandSource;)Ljava/util/List;", cancellable = true)
    public void getEntities(ServerCommandSource source, CallbackInfoReturnable<List<? extends Entity>> cbi) throws CommandSyntaxException {
        if (!targetOnly) return;

        cbi.setReturnValue(Lists.newArrayList(getTarget(source, false)));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/command/EntitySelector;isLocalWorldOnly()Z"), method = "getPlayers", cancellable = true)
    public void getPlayers(ServerCommandSource source, CallbackInfoReturnable<List<ServerPlayerEntity>> cbi) throws CommandSyntaxException {
        if (!targetOnly) return;

        cbi.setReturnValue(Lists.newArrayList((ServerPlayerEntity) getTarget(source, true)));
    }

    private @Unique Entity getTarget(ServerCommandSource source, boolean playerOnly) throws CommandSyntaxException {
        Entity target;
        if (IMoreCommands.get().isServerOnly() || !(source.getEntityOrThrow() instanceof PlayerEntity)) {
            EntityHitResult hit = MoreCommands.getEntityRayTraceTarget(source.getEntityOrThrow(), 160);

            if (hit == null) throw NO_TARGET.create();
            target = hit.getEntity();
        } else target = MoreCommands.getTargetedEntity(source.getPlayerOrThrow());

        if (target == null) throw NO_TARGET.create();
        if (playerOnly && !(target instanceof ServerPlayerEntity)) throw NO_PLAYER_TARGET.create();

        return target;
    }
}
