package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class WhereAmICommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("whereami")
                .executes(ctx -> {
                    Level world = ctx.getSource().getEntityOrException().getCommandSenderWorld();
                    BlockPos pos = Compat.get().blockPosition(ctx.getSource().getEntityOrException());
                    sendMsg(ctx, "You're currently in world " + SF + world.dimension().location().toString() + DF + " at " +
                            SF + pos.getX() + DF + ", " + SF + pos.getY() + DF + ", " + SF + pos.getZ() + DF + " in biome " + SF +
                            Compat.get().getRegistry(world.registryAccess(), ResourceKey.createRegistryKey(new ResourceLocation("biome")))
                                    .getKey(Compat.get().getBiome(world, pos)) + DF + ".");
                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/where-am-i";
    }
}