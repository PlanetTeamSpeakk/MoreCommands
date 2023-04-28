package com.ptsmods.morecommands.api.addons;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;

public interface EntitySelectorAddon {
    boolean isTargetOnly();

    void setTargetOnly(boolean targetOnly);

    Entity getTarget(CommandSourceStack source, boolean playerOnly) throws CommandSyntaxException;
}
