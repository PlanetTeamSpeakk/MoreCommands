package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.*;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import oshi.SystemInfo;

public class sysinfo {

	public sysinfo() {}

	public static class Commandsysinfo extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "sysinfo";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			VMManagement vmm = VMManagement.getVMM();
			//@formatter:off
			// Copied from my Discord bot Impulse at https://github.com/PlanetTeamSpeakk/Impulse
			// Copied from Owner#sysinfo.
			String output = "Host PC OS:";
			output += 	"\n    Name: " + TextFormatting.YELLOW + vmm.getOsName() +
						"\n    Version: " + TextFormatting.YELLOW + vmm.getOsVersion() +
						"\n    Architecture: " + TextFormatting.YELLOW + vmm.getOsArch();
			output += 	"\n\nJVM:" +
						"\n    Loaded classes: " + TextFormatting.YELLOW + vmm.getLoadedClassCount() +
						"\n    Unloaded classes: " + TextFormatting.YELLOW + vmm.getUnloadedClassCount() +
						"\n    Total classes: " + TextFormatting.YELLOW + vmm.getTotalClassCount() +
						"\n    Classes initialized: " + TextFormatting.YELLOW + vmm.getInitializedClassCount() +
						"\n    Live threads: " + TextFormatting.YELLOW + vmm.getLiveThreadCount() +
						"\n    Daemon threads: " + TextFormatting.YELLOW + vmm.getDaemonThreadCount() +
						"\n    Total threads: " + TextFormatting.YELLOW + vmm.getTotalThreadCount() +
						"\n    Peak livethreadcount: " + TextFormatting.YELLOW + vmm.getPeakThreadCount();
			output += "\n\nDrives:";
			for (Path root : FileSystems.getDefault().getRootDirectories())
				try {
					FileStore store = Files.getFileStore(root);
					output += "\n    " + root +
							"\n        Left: " + TextFormatting.YELLOW + Reference.formatFileSize(store.getUsableSpace()).split("\\.")[0] + "." + Reference.formatFileSize(store.getUsableSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(store.getUsableSpace()).split(" ")[1] +
							"\n        Used: " + TextFormatting.YELLOW + Reference.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split("\\.")[0] + "." + Reference.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split(" ")[1] +
							"\n        Total: " + TextFormatting.YELLOW + Reference.formatFileSize(store.getTotalSpace()).split("\\.")[0] + "." + Reference.formatFileSize(store.getTotalSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(store.getTotalSpace()).split(" ")[1];
				} catch (IOException e) {
				}
			output += 	"\n\n    Total left: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getFreeSpace()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getFreeSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getFreeSpace()).split(" ")[1] +
						"\n    Total used: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getUsedSpace()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getUsedSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getUsedSpace()).split(" ")[1] +
						"\n    Total: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getTotalSpace()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getTotalSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getTotalSpace()).split(" ")[1];
			output += "\n\nRAM:" +
					"\n    Used by process: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getProcessRamUsage()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getProcessRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getProcessRamUsage()).split(" ")[1] +
					"\n    Allocated to process: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getProcessRamMax()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getProcessRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getProcessRamMax()).split(" ")[1] +
					"\n    Used by host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getSystemRamUsage()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getSystemRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getSystemRamUsage()).split(" ")[1] +
					"\n    RAM of host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getSystemRamMax()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getSystemRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getSystemRamMax()).split(" ")[1] +
					"\n    Swap used by host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split(" ")[1] +
					"\n    Swap of host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getSystemSwapMax()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getSystemSwapMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getSystemSwapMax()).split(" ")[1] +
					"\n    Total RAM used by host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split(" ")[1] +
					"\n    Total RAM of host pc: " + TextFormatting.YELLOW + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split("\\.")[0] + "." + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + Reference.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split(" ")[1];
			output += "\n\nCPU:" +
					"\n    Unit: " + TextFormatting.YELLOW + new SystemInfo().getHardware().getProcessors()[0] +
					"\n    Cores: " + TextFormatting.YELLOW + UsageMonitorer.getProcessorCount() +
					"\n    Used by process: " + TextFormatting.YELLOW + UsageMonitorer.getProcessCpuLoad() +
					"\n    Used by system: " + TextFormatting.YELLOW + UsageMonitorer.getSystemCpuLoad();
			Reference.sendMessage(sender, output);
			//@formatter:on
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "sysinfo", "Shows you geeky stuff like ram usage, cpu usage, etc.", true);
		}

		private String usage = "/sysinfo Shows you geeky stuff like ram usage, cpu usage, etc.";

	}

}