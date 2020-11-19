package com.ptsmods.morecommands.mixin.common;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Iterator;

@Mixin(EnchantCommand.class)
public class MixinEnchantCommand {

    @Shadow @Final private static DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION;
    @Shadow @Final private static DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION;
    @Shadow @Final private static SimpleCommandExceptionType FAILED_EXCEPTION;

    @Overwrite
    private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Enchantment enchantment, int level) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets)
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                ItemStack itemStack = livingEntity.getMainHandStack();
                if (!itemStack.isEmpty()) {
                    itemStack.addEnchantment(enchantment, level);
                    ++i;
                } else if (targets.size() == 1) throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.getName().getString());
            } else if (targets.size() == 1) throw FAILED_ENTITY_EXCEPTION.create(entity.getName().getString());
        if (i == 0) throw FAILED_EXCEPTION.create();
        if (targets.size() == 1) source.sendFeedback(new TranslatableText("commands.enchant.success.single", enchantment.getName(level), targets.iterator().next().getDisplayName()), true);
        else source.sendFeedback(new TranslatableText("commands.enchant.success.multiple", enchantment.getName(level), targets.size()), true);
        return i;
    }

}
