package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Ticker;
import com.ptsmods.morecommands.miscellaneous.Ticker.TickRunnable;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class god {

	public god() {}

	public static class Commandgod extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		public Commandgod() {
			TickRunnable runnable = extraArgs -> {
				for (EntityPlayer playermp : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
					if (playermp.getIsInvulnerable() && !invinciblePlayers.contains(playermp.getUniqueID().toString())) invinciblePlayers.add(playermp.getUniqueID().toString());
			};
			Ticker.INSTANCE.addRunnable(Type.SERVER, runnable.setRemoveWhenRan(false));
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				ArrayList options = new ArrayList();
				options.add("on");
				options.add("off");
				return options;
			} else if (args.length == 2) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			else return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "god";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		public static List<String> invinciblePlayers = new ArrayList();

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = (EntityPlayer) sender;
			if (args.length == 2) try {
				player = getPlayer(server, sender, args[1]);
			} catch (PlayerNotFoundException e) {
				Reference.sendMessage(sender, "The given player could not be found.");
				return;
			}
			boolean on = args.length > 0 ? args[0].equals("on") : !invinciblePlayers.contains(player.getUniqueID().toString());
			if (on) {
				player.setEntityInvulnerable(true);
				invinciblePlayers.add(player.getUniqueID().toString());
				if (player == (EntityPlayer) sender) Reference.sendMessage(sender, "You're now " + TextFormatting.GREEN + "invulnerable" + Reference.dtf + ".");
				else {
					Reference.sendMessage(sender, player.getName() + " is now " + TextFormatting.GREEN + "invulnerable" + Reference.dtf + ".");
					Reference.sendMessage(player, sender.getName() + " has made you " + TextFormatting.GREEN + "invulnerable" + Reference.dtf + ".");
				}
			} else {
				player.setEntityInvulnerable(false);
				invinciblePlayers.remove(player.getUniqueID().toString());
				if (player == (EntityPlayer) sender) Reference.sendMessage(sender, "You're now " + TextFormatting.RED + "vulnerable" + Reference.dtf + ".");
				else {
					Reference.sendMessage(sender, player.getName() + " is now " + TextFormatting.RED + "vulnerable" + Reference.dtf + ".");
					Reference.sendMessage(player, sender.getName() + " has made you " + TextFormatting.RED + "vulnerable" + Reference.dtf + ".");
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "god", "Permission to use the god command.", true);
		}

		protected String usage = "/god [on/off] [player] You'll never get damage again and you'll never be hungry anymore.";

	}

}