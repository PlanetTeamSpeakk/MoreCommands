package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class SmiteCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("smite").requires(IS_OP).then(argument("player", EntityArgumentType.player()).executes(ctx -> {
            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            for (int i = 0; i < 3; i++) {
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, ctx.getSource().getWorld());
                bolt.setPos(player.getX(), player.getY(), player.getZ());
                ctx.getSource().getWorld().spawnEntity(bolt);
            }
            return 1;
        })));
    }
}
