package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

public class LightCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        if (!Version.getCurrent().isNewerThanOrEqual(Version.V1_17)) return; // No light blocks in 1.16

        dispatcher.register(literalReqOp("light")
                .executes(ctx -> execute(ctx, 1, 1))
                .then(argument("level", IntegerArgumentType.integer(0, 15))
                        .executes(ctx -> execute(ctx, ctx.getArgument("level", int.class), 1))
                        .then(argument("count", IntegerArgumentType.integer(1))
                                .executes(ctx -> execute(ctx, ctx.getArgument("level", int.class), ctx.getArgument("count", int.class))))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, int level, int count) throws CommandSyntaxException {
        ItemStack stack = new ItemStack(Items.LIGHT, count);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("level", "" + level);
        stack.setSubNbt("BlockStateTag", nbt);

        ServerCommandSource source = ctx.getSource();
        source.getPlayer().getInventory().insertStack(stack);
        Random random = new Random();
        source.getWorld().playSound(null, source.getPosition().getX(), source.getPosition().getY(), source.getPosition().getZ(),
                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((random.nextFloat() - random.nextFloat()) * 0.7f + 1.0f) * 2.0f);

        return count * 10 + level;
    }
}
