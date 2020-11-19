package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.UsageMonitorer;
import com.ptsmods.morecommands.miscellaneous.VMManagement;
import net.minecraft.server.command.ServerCommandSource;
import oshi.SystemInfo;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class SysInfoCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("sysinfo").requires(IS_OP).executes(SysInfoCommand::sendSysInfo));
    }

    @Override
    public boolean forDedicated() {
        return true; // It is the exact same as /csysinfo on singleplayer anyway.
    }

    public static int sendSysInfo(CommandContext<ServerCommandSource> ctx) {
        VMManagement vmm = VMManagement.getVMM();
        // Copied from my Discord bot Impulse at https://github.com/PlanetTeamSpeakk/Impulse
        // Copied from Owner#sysinfo.
        String output = "Host PC OS:";
        output += 	"\n    Name: " + SF + vmm.getOsName() +
                "\n    Version: " + SF + vmm.getOsVersion() +
                "\n    Architecture: " + SF + vmm.getOsArch();
        output += 	"\n\nJVM:" +
                "\n    Loaded classes: " + SF + vmm.getLoadedClassCount() +
                "\n    Unloaded classes: " + SF + vmm.getUnloadedClassCount() +
                "\n    Total classes: " + SF + vmm.getTotalClassCount() +
                "\n    Classes initialized: " + SF + vmm.getInitializedClassCount() +
                "\n    Live threads: " + SF + vmm.getLiveThreadCount() +
                "\n    Daemon threads: " + SF + vmm.getDaemonThreadCount() +
                "\n    Total threads: " + SF + vmm.getTotalThreadCount() +
                "\n    Peak livethreadcount: " + SF + vmm.getPeakThreadCount();
        output += "\n\nDrives:";
        for (Path root : FileSystems.getDefault().getRootDirectories())
            try {
                FileStore store = Files.getFileStore(root);
                output += "\n    " + root +
                        "\n        Left: " + SF + MoreCommands.formatFileSize(store.getUsableSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(store.getUsableSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(store.getUsableSpace()).split(" ")[1] +
                        "\n        Used: " + SF + MoreCommands.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(store.getTotalSpace() - store.getUsableSpace()).split(" ")[1] +
                        "\n        Total: " + SF + MoreCommands.formatFileSize(store.getTotalSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(store.getTotalSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(store.getTotalSpace()).split(" ")[1];
            } catch (IOException ignored) {}
        output += 	"\n\n    Total left: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getFreeSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getFreeSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getFreeSpace()).split(" ")[1] +
                "\n    Total used: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getUsedSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getUsedSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getUsedSpace()).split(" ")[1] +
                "\n    Total: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSpace()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getTotalSpace()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getTotalSpace()).split(" ")[1];
        output += "\n\nRAM:" +
                "\n    Used by process: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamUsage()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamUsage()).split(" ")[1] +
                "\n    Allocated to process: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamMax()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamMax()).split(" ")[1] +
                "\n    Used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamUsage()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamUsage()).split(" ")[1] +
                "\n    RAM of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamMax()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamMax()).split(" ")[1] +
                "\n    Swap used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapUsage()).split(" ")[1] +
                "\n    Swap of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapMax()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapMax()).split(" ")[1] +
                "\n    Total RAM used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()).split(" ")[1] +
                "\n    Total RAM of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split("\\.")[0] + "." + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split("\\.")[1].substring(0, 3).split(" ")[0] + " " + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamMax()).split(" ")[1];
        output += "\n\nCPU:" +
                "\n    Unit: " + SF + new SystemInfo().getHardware().getProcessors()[0] +
                "\n    Cores: " + SF + UsageMonitorer.getProcessorCount() +
                "\n    Used by process: " + SF + UsageMonitorer.getProcessCpuLoad()*100 +
                "\n    Used by system: " + SF + UsageMonitorer.getSystemCpuLoad()*100;
        if (ctx != null) sendMsg(ctx, output);
        else ClientCommand.sendMsg(output);
        return 1;
    }
}
