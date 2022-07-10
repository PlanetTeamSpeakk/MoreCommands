package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpawnCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReq("spawn")
                .executes(ctx -> execute(ctx, null))
                .then(argument("player", EntityArgument.player())
                        .requires(hasPermissionOrOp("morecommands.spawn.others"))
                        .executes(ctx -> execute(ctx, EntityArgument.getPlayer(ctx, "player")))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/spawn";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer player) throws CommandSyntaxException {
        Entity entity = player == null ? ctx.getSource().getEntityOrException() : player;
        ServerLevel world = Objects.requireNonNull(ctx.getSource().getServer().getLevel(Level.OVERWORLD));
        MoreCommands.teleport(entity, world, Vec3.atCenterOf(Compat.get().getWorldSpawnPos(world)), entity.getYRot(), entity.getXRot());
        if (player != null) sendMsg(player, literalText("You have been teleported to spawn by ", DS)
                .append(Compat.get().builderFromText(ctx.getSource().getDisplayName()))
                .append("."));
        return 1;
    }
}
