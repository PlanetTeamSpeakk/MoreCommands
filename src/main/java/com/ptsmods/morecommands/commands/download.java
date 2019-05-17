package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Downloader;
import com.ptsmods.morecommands.miscellaneous.Downloader.DownloadResult;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class download {

	public download() {}

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
			if (args.length == 0) ;// Reference.sendCommandUsage(sender, usage);
			else if ("cancel".equals(args[0]) && args.length >= 2 && Reference.isInteger(args[1])) {
				int id = Integer.parseInt(args[1]);
				if (Reference.getNextDownloadThreadId() < id) Reference.sendMessage(sender, "That id does not seem to exist.");
				else {
					Reference.interruptDownloadThread(id);
					Reference.sendMessage(sender, "The download has been canceled.");
				}
			} else {
				Thread downloadThread = new Thread(() -> {
					String url = Reference.joinCustomChar("", args);
					Reference.sendMessage(sender, "Calculating file size, please wait...");
					try {
						Reference.sendMessage(sender, "Downloading " + Downloader.formatFileSize(Downloader.getWebFileSize(Downloader.convertUrl(url))) + ", please wait... To cancel the download run /download cancel " + (Reference.getNextDownloadThreadId() - 1) + ".");
					} catch (Throwable e1) {
						Reference.sendMessage(sender, "Could not get the file size, downloading the file, please wait...");
						e1.printStackTrace();
					}
					String filename = "downloads/" + url.split("/")[url.split("/").length - 1];
					if (url.contains("youtu") && url.split("v=").length == 2) filename = filename.split("v=")[1] + ".mp4";
					DownloadResult downloaded = null;
					Long milis1 = System.currentTimeMillis();
					try {
						downloaded = Downloader.downloadFileOrVideo(url, filename);
					} catch (NullPointerException e2) {
						Reference.sendMessage(sender, "An unknown error occured while trying to download the file, please try again.");
						e2.printStackTrace();
					} catch (IOException e3) {
						Reference.sendMessage(sender, "The url was malformed or an unknown error occured, please try with a different url.");
						e3.printStackTrace();
					}
					Long milis2 = System.currentTimeMillis();
					if (!Thread.currentThread().isInterrupted()) Reference.sendMessage(sender, downloaded.succeeded() ? "The file has been downloaded successfully to " + TextFormatting.GRAY + TextFormatting.ITALIC + downloaded.getFileLocation() + TextFormatting.RESET + ", downloaded " + Downloader.formatFileSize(downloaded.getFileSize()) + " in " + (milis2 - milis1) + " miliseconds." : "The file could not be downloaded.");
				});
				downloadThread.start();
				Reference.addDownloadThread(downloadThread);

			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.CLIENT;
		}

		protected String usage = "/download <url> Downloads a file to your Minecraft directory. " + "By default files are downloaded to appdata\\.minecraft\\downloads unless the Minecraft directory has been changed. " + "Can also download (most, except for music vids) YouTube videos and Vimeo videos.";

	}

}