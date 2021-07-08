package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;

public class GetStrucCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literalReq("getstruc").executes(ctx -> {
            if (!ctx.getSource().getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) sendError(ctx, "Structures are disabled in this world.");
            else {
                for (Map.Entry<String, StructureFeature<?>> entry : StructureFeature.STRUCTURES.entrySet()) {
                    BlockPos pos = entry.getValue().locateStructure(ctx.getSource().getWorld(), ctx.getSource().getWorld().getStructureAccessor(), new BlockPos(ctx.getSource().getPosition()), 0, false, ctx.getSource().getWorld().getSeed(), ctx.getSource().getWorld().getChunkManager().getChunkGenerator().getStructuresConfig().getForType(entry.getValue()));
                    MoreCommands.log.info(pos);
                    if (pos != null && pos.isWithinDistance(ctx.getSource().getPosition(), 64)) {
                        sendMsg(ctx, "A(n) " + SF + entry.getKey() + DF + " has been found here.");
                        return Registry.STRUCTURE_FEATURE.getRawId(entry.getValue()) + 1;
                    }
                }
                sendError(ctx, "No known structure could be found at your position.");
            }
            return 0;
        }));
    }
}
