package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.commands.fakePlayer.CommandfakePlayer;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class inspect {

	public inspect() {}

	public static class Commandinspect extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "inspect";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				String username = args[0];
				EntityPlayerMP player = null;
				UUID id = null;
				NBTTagCompound data = new NBTTagCompound();
				try {
					player = getPlayer(server, sender, username);
				} catch (CommandException e) {}
				if (player != null) id = player.getUniqueID();
				else if (Reference.isUUID(username)) id = UUIDTypeAdapter.fromString(username);
				else try {
					Map userIdData = new Gson().fromJson(Reference.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username), Map.class);
					if (userIdData == null) throw new IOException("The Mojang api returned invalid data.");
					id = UUIDTypeAdapter.fromString((String) userIdData.get("id"));
				} catch (IOException e) {
					e.printStackTrace();
					Reference.sendMessage(sender, TextFormatting.RED + "Could not find the player's UUID, did you make a typo?");
					return;
				}
				try {
					if (id == null) Reference.sendMessage(sender, TextFormatting.RED + "No UUID belonging to the given username could be found.");
					else if (!new File(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + id.toString() + ".dat").exists()) Reference.sendMessage(sender, TextFormatting.RED + "No file belonging to the found UUID could be found.");
					else data = CommandfakePlayer.getPlayerData(sender.getEntityWorld().getSaveHandler().getWorldDirectory(), id);
					if (data == null || data.isEmpty()) return;
				} catch (IOException e) {
					e.printStackTrace();
					Reference.sendMessage(sender, TextFormatting.RED + "An unknown error occurred while getting the player data.");
					return;
				}
				Reference.sendMessage(sender, "The following data was found for the given player:\n" + data.toString());
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "inspect", "Inspect a player's NBT data.", true);
		}

		private String usage = "/inspect <player> Inspect a player's NBT data.";

	}

}