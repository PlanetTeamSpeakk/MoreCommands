package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpawnMobCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("spawnmob")
                .then(argument("type", EntitySummonArgument.id())
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(ctx -> execute(ctx, 1))
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> execute(ctx, ctx.getArgument("amount", Integer.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/spawn-mob";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), 160, true, false);
        Vec3 pos = result.getLocation();
        CompoundTag tag = MoreCommands.getDefaultTag(Registry.ENTITY_TYPE.get(ctx.getArgument("type", ResourceLocation.class)));
        for (int i = 0; i < amount; i++) MoreCommands.summon(tag, ctx.getSource().getLevel(), pos);
        return amount;
    }
}
