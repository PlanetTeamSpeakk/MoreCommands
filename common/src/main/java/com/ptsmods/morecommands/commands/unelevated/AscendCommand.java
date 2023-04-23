package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class AscendCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("ascend")
                .executes(AscendCommand::execute));
    }

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = ctx.getSource().getEntityOrException();
        Level world = entity.getCommandSenderWorld();
        Vec3 pos = entity.position();
        double x = pos.x;
        double y = pos.y + 2;
        double z = pos.z;
        for (; y < world.getHeight(); y++) {
            Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            Block tpblock2 = world.getBlockState(new BlockPos(x, y + 1, z)).getBlock();
            if (!MoreCommands.blockBlacklist.contains(block) && MoreCommands.blockWhitelist.contains(tpblock) && MoreCommands.blockWhitelist.contains(tpblock2)) {
                entity.teleportToWithTicket(x + 0.5, y, z + 0.5);
                sendMsg(ctx, "You have been teleported through the roof.");
                return 1;
            }
        }
        sendMsg(ctx, "No free spot found above you.");
        return 0;
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/ascend";
    }
}
