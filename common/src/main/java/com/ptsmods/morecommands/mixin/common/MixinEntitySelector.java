package com.ptsmods.morecommands.mixin.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.addons.EntitySelectorAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelector;isWorldLimited()Z"), method = "findPlayers", cancellable = true)
    public void getPlayers(CommandSourceStack source, CallbackInfoReturnable<List<ServerPlayer>> cbi) throws CommandSyntaxException {
        if (!targetOnly) return;

        cbi.setReturnValue(Lists.newArrayList((ServerPlayer) getTarget(source, true)));
    }

    public Entity getTarget(CommandSourceStack source, boolean playerOnly) throws CommandSyntaxException {
        Entity target;
        if (IMoreCommands.get().isServerOnly() || !(source.getEntityOrException() instanceof Player)) {
            EntityHitResult hit = MoreCommands.getEntityRayTraceTarget(source.getEntityOrException(), 160);

            if (hit == null) throw NO_TARGET.create();
            target = hit.getEntity();
        } else target = MoreCommands.getTargetedEntity(source.getPlayerOrException());

        if (target == null) throw NO_TARGET.create();
        if (playerOnly && !(target instanceof ServerPlayer)) throw NO_PLAYER_TARGET.create();

        return target;
    }
}
