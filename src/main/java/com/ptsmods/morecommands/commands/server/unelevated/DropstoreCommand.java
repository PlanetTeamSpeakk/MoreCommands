package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropstoreCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("dropstore").executes(ctx -> execute(ctx, null, true))
                .then(argument("pos", BlockPosArgumentType.blockPos()).executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), true))
                .then(argument("clear", BoolArgumentType.bool()).executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), ctx.getArgument("clear", Boolean.class)))))
        .then(argument("clear", BoolArgumentType.bool()).executes(ctx -> execute(ctx, null, ctx.getArgument("clear", Boolean.class)))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, BlockPos pos, boolean clear) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayer();
        if (pos == null) pos = player.getBlockPos();
        player.getEntityWorld().setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.LEFT));
        player.getEntityWorld().setBlockState(pos.add(1, 0, 0), Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.RIGHT));
        ChestBlockEntity chest = (ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos.add(1, 0, 0));
        int i0 = 0;
        int i;
        for (i = 9; i < 36; i++, i0++)
            chest.setStack(i0, player.inventory.getStack(i));
        i0 %= 27;
        chest = (ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos);
        for (i = 0; i < 9; i++, i0++)
            chest.setStack(i0, player.inventory.getStack(i));
        List<ItemStack> armorInv = new ArrayList<>(player.inventory.armor);
        Collections.reverse(armorInv);
        for (ItemStack stack : armorInv)
            chest.setStack(i0++, stack);
        chest.setStack(i0++, player.inventory.offHand.get(0));
        if (clear) player.inventory.clear();
        sendMsg(ctx, "Your inventory has been transferred into a double chest placed at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
        return i0;
    }

}
