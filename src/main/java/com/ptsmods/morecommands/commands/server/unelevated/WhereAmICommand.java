package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class WhereAmICommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("whereami").executes(ctx -> {
            World world = ctx.getSource().getEntityOrThrow().getEntityWorld();
            BlockPos pos = ctx.getSource().getEntityOrThrow().getBlockPos();
            sendMsg(ctx, "You're currently in world " + SF + world.getRegistryKey().getValue().toString() + DF + " at " + SF + pos.getX() + DF + ", " + SF + pos.getY() + DF + ", " + SF + pos.getZ() + DF + " in biome " + SF + Registry.BIOME.getId(world.getBiome(pos)) + DF + ".");
            return 1;
        }));
    }
}
