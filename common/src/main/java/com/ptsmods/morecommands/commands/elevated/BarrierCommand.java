package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BarrierCommand extends Command {
    private static final Random random = new Random();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("barrier")
                .executes(ctx -> execute(ctx, ctx.getSource().getPlayerOrException(), 1))
                .then(argument("amount", IntegerArgumentType.integer(0))
                        .executes(ctx -> execute(ctx, ctx.getSource().getPlayerOrException(), ctx.getArgument("count", Integer.class)))
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> execute(ctx, EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("amount", Integer.class))))));
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Player player, int count) {
        if (player.getInventory().add(new ItemStack(Items.BARRIER, count)))
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);

        sendMsg(ctx, (player == ctx.getSource().getEntity() ? "You have" : SF + IMoreCommands.get().textToString(player.getDisplayName(), SS, true) + ChatFormatting.RESET + " has") +
                " been given " + SF + count + " barrier" + (count == 1 ? "" : "s") + ChatFormatting.RESET + ".");
        return count;
    }

    @Override
    public String getDocsPath() {
        return "/elevated/barrier";
    }
}
