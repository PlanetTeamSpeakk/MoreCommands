package com.ptsmods.morecommands.commands.server.unelevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.*;

public class DropstoreCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("dropstore")
                .executes(ctx -> execute(ctx, null, true))
                .then(argument("pos", BlockPosArgument.blockPos())
                        .requires(hasPermissionOrOp("morecommands.dropstore.elsewhere"))
                        .executes(ctx -> execute(ctx, BlockPosArgument.getSpawnablePos(ctx, "pos"), true))
                        .then(argument("clear", BoolArgumentType.bool())
                                .requires(hasPermissionOrOp("morecommands.dropstore.clear"))
                                .executes(ctx -> execute(ctx, BlockPosArgument.getSpawnablePos(ctx, "pos"), ctx.getArgument("clear", Boolean.class)))))
                .then(argument("clear", BoolArgumentType.bool())
                        .requires(hasPermissionOrOp("morecommands.dropstore.clear"))
                        .executes(ctx -> execute(ctx, null, ctx.getArgument("clear", Boolean.class)))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/dropstore";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, BlockPos pos, boolean clear) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        if (pos == null) pos = Compat.get().blockPosition(player);

        Inventory inventory = player.getInventory();
        boolean chests = hasPermissionOrOp("morecommands.dropstore.chests").test(ctx.getSource());

        if (!chests) {
            if (!inventory.contains(new ItemStack(Items.CHEST, 2))) {
                sendError(ctx, "You do not have two chests in your inventory.");
                return 0;
            }

            inventory.clearOrCountMatchingItems(stack -> stack.getItem() == Items.CHEST, 2, player.inventoryMenu.getCraftSlots());
        }

        player.getCommandSenderWorld().setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.LEFT));
        player.getCommandSenderWorld().setBlockAndUpdate(pos.offset(1, 0, 0), Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.RIGHT));

        ChestBlockEntity chest = Objects.requireNonNull((ChestBlockEntity) player.getCommandSenderWorld().getBlockEntity(pos.offset(1, 0, 0)));
        int i0 = 0;
        for (int i = 9; i < 36; i++, i0++)
            chest.setItem(i0, inventory.getItem(i));

        i0 %= 27;
        chest = Objects.requireNonNull((ChestBlockEntity) player.getCommandSenderWorld().getBlockEntity(pos));
        for (int i = 0; i < 9; i++, i0++)
            chest.setItem(i0, inventory.getItem(i));

        List<ItemStack> armorInv = new ArrayList<>(player.getInventory().armor);
        Collections.reverse(armorInv);
        for (ItemStack stack : armorInv)
            chest.setItem(i0++, stack);

        chest.setItem(i0++, inventory.offhand.get(0));

        if (clear) inventory.clearContent();
        sendMsg(ctx, "Your inventory has been transferred into a double chest placed at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
        return i0;
    }

    @Override
    public Map<String, Boolean> getExtraPermissions() {
        return ImmutableMap.of("morecommands.dropstore.chests", false);
    }
}
