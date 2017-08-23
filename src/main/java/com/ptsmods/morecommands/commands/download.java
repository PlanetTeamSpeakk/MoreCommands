package com.ptsmods.morecommands.commands;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class download {

	public download() {
	}

	public static class Commanddownload extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "download";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else {
				String filename = args[0].split("/")[args[0].split("/").length-1];
				Map<String, String> downloaded = new HashMap<String, String>();
				downloaded.put("fileLocation", "");
				downloaded.put("success", "false");
				try {
					downloaded = Reference.downloadFile(args[0], "downloads/" + filename);
				} catch (NullPointerException e) {
					Reference.sendMessage(sender, "An unknown error occured while trying to download the file, please try again.");
					e.printStackTrace();
				} catch (MalformedURLException e) {
					Reference.sendMessage(sender, "The url was malformed, please try with a different url.");
				}
				Reference.sendMessage(sender, (Boolean.parseBoolean(downloaded.get("success")) ? "The file has been downloaded successfully to " + TextFormatting.GRAY + TextFormatting.ITALIC + new File(downloaded.get("fileLocation")).getAbsolutePath() + 
						TextFormatting.RESET + "." : "The file could not be downloaded."));
			}
		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}
		
		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}
		
		protected String usage = "/download <url> Downloads a file to your Minecraft directory. By default files are downloaded to appdata\\.minecraft\\downloads unless the Minecraft directory has been changed.";

	}

}