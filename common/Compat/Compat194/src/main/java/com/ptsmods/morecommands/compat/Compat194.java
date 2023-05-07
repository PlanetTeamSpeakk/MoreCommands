package com.ptsmods.morecommands.compat;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;

public class Compat194 extends Compat193 {

    @Override
    public DamageSource getSuicideDamageSource(Entity entity) {
        return new DamageSource(entity.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(IMoreCommands.get().isServerOnly() ? DamageTypes.OUT_OF_WORLD :
                        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("morecommands:suicide"))));
    }

    @Override // Don't register the damage command as vanilla has one now.
    public void registerVersionSpecificCommands(CommandDispatcher<CommandSourceStack> dispatcher) {}

    @Override
    public MutableComponent buildText(TranslatableTextBuilder builder) {
        return buildText(builder.getArgs().length == 0 ? new TranslatableContents(builder.getKey(), null, new Object[0]) :
                new TranslatableContents(builder.getKey(), null, Arrays.stream(builder.getArgs())
                        .map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
                        .toArray(Object[]::new)), builder);
    }
}
