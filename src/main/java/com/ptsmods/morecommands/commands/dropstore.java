package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class dropstore {

	public dropstore() {}

	public static class Commanddropstore extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "dropstore";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			BlockPos pos = player.getPosition();
			if (args.length >= 4) pos = new BlockPos(parseDouble(sender.getPositionVector().x, args[1], false), parseDouble(sender.getPositionVector().y, args[2], false), parseDouble(sender.getPositionVector().z, args[3], false));
			player.getEntityWorld().setBlockState(pos, Blocks.CHEST.getDefaultState());
			player.getEntityWorld().setBlockState(pos.add(1, 0, 0), Blocks.CHEST.getDefaultState());
			IItemHandler chest = ((TileEntityChest) player.getEntityWorld().getTileEntity(pos)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			int i0 = 0;
			int i = 0;
			for (i = 9; i < 36; i++, i0++)
				chest.insertItem(i0, player.inventory.getStackInSlot(i), false);
			for (i = 0; i < 9; i++, i0++)
				chest.insertItem(i0, player.inventory.getStackInSlot(i), false);
			List<ItemStack> armorInv = new ArrayList(player.inventory.armorInventory);
			Collections.reverse(armorInv);
			for (ItemStack stack : armorInv)
				chest.insertItem(i0++, stack, false);
			chest.insertItem(i0++, player.inventory.offHandInventory.get(0), false);
			if (args.length == 0 || Reference.isBoolean(args[0]) && Boolean.parseBoolean(args[0])) player.inventory.clear();
			Reference.sendMessage(sender, "Your inventory has been transferred into a double chest placed at X: " + pos.getX() + ", Y: " + pos.getY() + ", Z: " + pos.getZ() + ".");
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "dropstore", "Drop all your items in a chest.", true);
		}

		private String usage = "/dropstore [clear] [x] [y] [z] Drop all your items in a chest and clears your inventory afterwards by default, coordinates default to your location, clear defaults to true.";

	}

}