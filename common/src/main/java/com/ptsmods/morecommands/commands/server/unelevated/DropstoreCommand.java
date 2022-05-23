package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DropstoreCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("dropstore").executes(ctx -> execute(ctx, null, true))
                .then(argument("pos", BlockPosArgumentType.blockPos()).executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), true))
                .then(argument("clear", BoolArgumentType.bool()).executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), ctx.getArgument("clear", Boolean.class)))))
        .then(argument("clear", BoolArgumentType.bool()).executes(ctx -> execute(ctx, null, ctx.getArgument("clear", Boolean.class)))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, BlockPos pos, boolean clear) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayer();
        if (pos == null) pos = player.getBlockPos();
        player.getEntityWorld().setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.LEFT));
        player.getEntityWorld().setBlockState(pos.add(1, 0, 0), Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.RIGHT));
        ChestBlockEntity chest = Objects.requireNonNull((ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos.add(1, 0, 0)));
        int i0 = 0;
        for (int i = 9; i < 36; i++, i0++)
            chest.setStack(i0, Compat.get().getInventory(player).getStack(i));
        i0 %= 27;
        chest = Objects.requireNonNull((ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos));
        for (int i = 0; i < 9; i++, i0++)
            chest.setStack(i0, Compat.get().getInventory(player).getStack(i));
        List<ItemStack> armorInv = new ArrayList<>(Compat.get().getInventory(player).armor);
        Collections.reverse(armorInv);
        for (ItemStack stack : armorInv)
            chest.setStack(i0++, stack);
        chest.setStack(i0++, Compat.get().getInventory(player).offHand.get(0));
        if (clear) Compat.get().getInventory(player).clear();
        sendMsg(ctx, "Your inventory has been transferred into a double chest placed at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
        return i0;
    }

}
