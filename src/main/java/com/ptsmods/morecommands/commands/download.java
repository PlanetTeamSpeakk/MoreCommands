package com.ptsmods.morecommands.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Downloader;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class download {

	public download() {
	}

	public static class Commanddownload extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "download";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else
				new Thread(new Runnable() {
					@Override
					public void run() {
						Reference.sendMessage(sender, "Calculating file size, please wait...");
						try {
							Reference.sendMessage(sender, "Downloading " + Downloader.formatFileSize(Downloader.getWebFileSize(args[0])) + " , please wait...");
						} catch (Throwable e1) {
							Reference.sendMessage(sender, "Could not get the file size, downloading the file, please wait...");
							e1.printStackTrace();
						}
						String filename = args[0].split("/")[args[0].split("/").length-1];
						Map<String, String> downloaded = new HashMap<>();
						downloaded.put("fileLocation", "");
						downloaded.put("success", "false");
						downloaded.put("bytes", "0");
						Long milis1 = System.currentTimeMillis();
						try {
							downloaded = args[0].contains("youtu") ? Downloader.downloadYoutubeVideo(args[0], "downloads/" + filename) : Downloader.downloadFile(args[0], "downloads/" + filename);
						} catch (NullPointerException e) {
							Reference.sendMessage(sender, "An unknown error occured while trying to download the file, please try again.");
							e.printStackTrace();
						} catch (IOException e) {
							Reference.sendMessage(sender, "The url was malformed or an unknown error occured, please try with a different url.");
							e.printStackTrace();
						}
						Long milis2 = System.currentTimeMillis();
						Reference.sendMessage(sender, Boolean.parseBoolean(downloaded.get("success")) ? "The file has been downloaded successfully to " + TextFormatting.GRAY + TextFormatting.ITALIC + new File(downloaded.get("fileLocation")).getAbsolutePath() +
								TextFormatting.RESET + ", downloaded " + Downloader.formatFileSize(Integer.parseInt(downloaded.get("bytes"))) + " in " + (milis2-milis1) + " miliseconds." : "The file could not be downloaded.");
					}
				}).start();
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		protected String usage = "/download <url> Downloads a file to your Minecraft directory. By default files are downloaded to appdata\\.minecraft\\downloads unless the Minecraft directory has been changed. Can also download (most, except for music vids) YouTube videos.";

	}

}