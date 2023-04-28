package com.ptsmods.morecommands.mixin.compat.compat192.min;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.EntitySelectorAddon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelector;isWorldLimited()Z"), method = "findEntities", cancellable = true)
    public void getEntities(CommandSourceStack source, CallbackInfoReturnable<List<? extends Entity>> cbi) throws CommandSyntaxException {
        EntitySelectorAddon addon = ReflectionHelper.cast(this);

        if (!addon.isTargetOnly()) return;

        cbi.setReturnValue(Lists.newArrayList(addon.getTarget(source, false)));
    }
}
