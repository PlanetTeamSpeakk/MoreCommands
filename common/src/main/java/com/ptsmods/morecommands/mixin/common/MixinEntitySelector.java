package com.ptsmods.morecommands.mixin.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.EntitySelectorAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntitySelector.class)
public class MixinEntitySelector implements EntitySelectorAddon {
    private static final @Unique SimpleCommandExceptionType NO_TARGET = new SimpleCommandExceptionType(LiteralTextBuilder.builder("You're not looking at an entity.").build());
    private static final @Unique SimpleCommandExceptionType NO_PLAYER_TARGET = new SimpleCommandExceptionType(LiteralTextBuilder.builder("You're not looking at a player.").build());
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

        HitResult hit = MoreCommands.getRayTraceTarget(source.getEntityOrThrow(), source.getWorld(), 160, false, true);

        if (hit.getType() != HitResult.Type.ENTITY) throw NO_TARGET.create();
        Entity target = ((EntityHitResult) hit).getEntity();

        cbi.setReturnValue(Lists.newArrayList(target));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/command/EntitySelector;isLocalWorldOnly()Z"), method = "getPlayers", cancellable = true)
    public void getPlayers(ServerCommandSource source, CallbackInfoReturnable<List<ServerPlayerEntity>> cbi) throws CommandSyntaxException {
        if (!targetOnly) return;

        HitResult hit = MoreCommands.getRayTraceTarget(source.getEntityOrThrow(), source.getWorld(), 160, false, true);

        if (hit.getType() != HitResult.Type.ENTITY) throw NO_PLAYER_TARGET.create();
        Entity target = ((EntityHitResult) hit).getEntity();
        if (!(target instanceof ServerPlayerEntity)) throw NO_PLAYER_TARGET.create();

        cbi.setReturnValue(Lists.newArrayList((ServerPlayerEntity) target));
    }
}
