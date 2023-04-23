package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Random;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LightCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        if (!Version.getCurrent().isNewerThanOrEqual(Version.V1_17)) return; // No light blocks in 1.16

        dispatcher.register(literalReqOp("light")
                .executes(ctx -> execute(ctx, 15, 1))
                .then(argument("level", IntegerArgumentType.integer(0, 15))
                        .executes(ctx -> execute(ctx, ctx.getArgument("level", int.class), 1))
                        .then(argument("count", IntegerArgumentType.integer(1))
                                .executes(ctx -> execute(ctx, ctx.getArgument("level", int.class), ctx.getArgument("count", int.class))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/light";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, int level, int count) throws CommandSyntaxException {
        ItemStack stack = new ItemStack(Items.LIGHT, count);
        CompoundTag nbt = new CompoundTag();
        nbt.putString("level", "" + level);
        stack.addTagElement("BlockStateTag", nbt);

        CommandSourceStack source = ctx.getSource();
        source.getPlayerOrException().getInventory().add(stack);
        Random random = new Random();
        source.getLevel().playSound(null, source.getPosition().x(), source.getPosition().y(), source.getPosition().z(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((random.nextFloat() - random.nextFloat()) * 0.7f + 1.0f) * 2.0f);

        return count * 10 + level;
    }
}
