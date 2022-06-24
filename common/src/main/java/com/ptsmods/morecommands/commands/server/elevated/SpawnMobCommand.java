package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class SpawnMobCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("spawnmob")
                .then(argument("type", EntitySummonArgumentType.entitySummon())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(ctx -> execute(ctx, 1))
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> execute(ctx, ctx.getArgument("amount", Integer.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/spawn-mob";
    }

    private int execute(CommandContext<ServerCommandSource> ctx, int amount) throws CommandSyntaxException {
        BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), 160, true, false);
        Vec3d pos = result.getPos();
        NbtCompound tag = MoreCommands.getDefaultTag(Registry.ENTITY_TYPE.get(ctx.getArgument("type", Identifier.class)));
        for (int i = 0; i < amount; i++) MoreCommands.summon(tag, ctx.getSource().getWorld(), pos);
        return amount;
    }
}
