package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;

public class GetStrucCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        // Not viable anymore due to the heavy changes Mojang made to the structure feature registry.

//        dispatcher.register(literalReq("getstruc").executes(ctx -> {
//            if (!ctx.getSource().getServer().getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) sendError(ctx, "Structures are disabled in this world.");
//            else {
//                Registry<StructureType> featureRegistry = CompatHelper.getCompat().getStructureTypeRegistry(ctx.getSource().getRegistryManager());
//                for (Map.Entry<RegistryKey<StructureType>, StructureType> entry : featureRegistry.getEntrySet()) {
//
//                    BlockPos pos = CompatHelper.getCompat().locateStructure(ctx.getSource().getWorld().getChunkManager().getChunkGenerator(),
//                            ctx.getSource().getWorld(), entry.getValue(), new BlockPos(ctx.getSource().getPosition()), 0, false);
//
//                    if (pos != null && pos.isWithinDistance(ctx.getSource().getPosition(), 64)) {
//                        sendMsg(ctx, "A(n) " + SF + entry.getKey() + DF + " has been found here.");
//                        return featureRegistry.getRawId(entry.getValue()) + 1;
//                    }
//                }
//                sendError(ctx, "No known structure could be found at your position.");
//            }
//            return 0;
//        }));
    }

    @Override
    public String getDocsPath() {
        return null;
    }
}
