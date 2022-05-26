package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class DropstoreCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("dropstore")
                .executes(ctx -> execute(ctx, null, true))
                .then(argument("pos", BlockPosArgumentType.blockPos())
                        .requires(hasPermissionOrOp("morecommands.dropstore.elsewhere"))
                        .executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), true))
                        .then(argument("clear", BoolArgumentType.bool())
                                .executes(ctx -> execute(ctx, BlockPosArgumentType.getBlockPos(ctx, "pos"), ctx.getArgument("clear", Boolean.class)))))
                .then(argument("clear", BoolArgumentType.bool())
                        .executes(ctx -> execute(ctx, null, ctx.getArgument("clear", Boolean.class)))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, BlockPos pos, boolean clear) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        if (pos == null) pos = player.getBlockPos();

        PlayerInventory inventory = Compat.get().getInventory(player);
        boolean chests = hasPermissionOrOp("morecommands.dropstore.chests").test(ctx.getSource());

        if (!chests) {
            if (!inventory.contains(new ItemStack(Items.CHEST, 2))) {
                sendError(ctx, "You do not have two chests in your inventory.");
                return 0;
            }

            inventory.remove(stack -> stack.getItem() == Items.CHEST, 2, player.playerScreenHandler.getCraftingInput());
        }

        player.getEntityWorld().setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.LEFT));
        player.getEntityWorld().setBlockState(pos.add(1, 0, 0), Blocks.CHEST.getDefaultState().with(ChestBlock.CHEST_TYPE, ChestType.RIGHT));

        ChestBlockEntity chest = Objects.requireNonNull((ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos.add(1, 0, 0)));
        int i0 = 0;
        for (int i = 9; i < 36; i++, i0++)
            chest.setStack(i0, inventory.getStack(i));

        i0 %= 27;
        chest = Objects.requireNonNull((ChestBlockEntity) player.getEntityWorld().getBlockEntity(pos));
        for (int i = 0; i < 9; i++, i0++)
            chest.setStack(i0, inventory.getStack(i));

        List<ItemStack> armorInv = new ArrayList<>(Compat.get().getInventory(player).armor);
        Collections.reverse(armorInv);
        for (ItemStack stack : armorInv)
            chest.setStack(i0++, stack);

        chest.setStack(i0++, inventory.offHand.get(0));

        if (clear) inventory.clear();
        sendMsg(ctx, "Your inventory has been transferred into a double chest placed at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
        return i0;
    }

    @Override
    public Map<String, Boolean> getExtraPermissions() {
        return ImmutableMap.of("morecommands.dropstore.chests", false);
    }
}
