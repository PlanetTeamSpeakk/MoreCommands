// THIS IS A DUMMY CLASS MEANING IT WON'T BE LOADED INTO THE GAME.
// THIS CLASS IS MEANT TO COPY AND PASTE TO MAKE NEW COMMANDS.

package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class breakBlock {

	public static Object instance;

	public breakBlock() {
	}

	public static class Commandbreak extends CommandBase {
		public boolean isUsernameIndex(int sender) {
			return false;
		}

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "break";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			BlockPos block = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
			EntityPlayer player = (EntityPlayer) sender;
			player.getEntityWorld().setBlockToAir(block);
			Reference.sendMessage(player, "The block at X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ() + " has been broken.");

		}
		
		protected String usage = "/break Breaks the block you're looking at.";

	}

}