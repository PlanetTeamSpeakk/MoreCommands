package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.util.UsageMonitorer;
import net.minecraft.server.command.ServerCommandSource;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class SysInfoCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("sysinfo").executes(SysInfoCommand::sendSysInfo));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true; // It is the exact same as /csysinfo on singleplayer anyway.
    }

    public static int sendSysInfo(CommandContext<ServerCommandSource> ctx) {
        // Copied from my Discord bot Impulse at https://github.com/PlanetTeamSpeakk/Impulse
        // Copied from Owner#sysinfo.
        String output = "Host PC OS:";
        output += "\n    Name: " + SF + UsageMonitorer.getOSName() +
                "\n    Version: " + SF + UsageMonitorer.getOSVersion() +
                "\n    Architecture: " + SF + UsageMonitorer.getOSArch();
        output +=     "\n\nJVM:" +
                "\n    Loaded classes: " + SF + UsageMonitorer.getCurrentLoadedClassCount() +
                "\n    Unloaded classes: " + SF + UsageMonitorer.getUnloadedClassCount() +
                "\n    Total classes: " + SF + UsageMonitorer.getTotalLoadedClassCount() +
                "\n    Live threads: " + SF + UsageMonitorer.getLiveThreadCount() +
                "\n    Daemon threads: " + SF + UsageMonitorer.getDaemonThreadCount() +
                "\n    Total threads: " + SF + UsageMonitorer.getTotalThreadCount() +
                "\n    Peak livethreadcount: " + SF + UsageMonitorer.getPeakLiveThreadCount();
        output += "\n\nDrives:";
        for (Path root : FileSystems.getDefault().getRootDirectories())
            try {
                FileStore store = Files.getFileStore(root);
                output += "\n    " + root +
                        "\n        Left: " + SF + MoreCommands.formatFileSize(store.getUsableSpace()) +
                        "\n        Used: " + SF + MoreCommands.formatFileSize(store.getTotalSpace() - store.getUsableSpace()) +
                        "\n        Total: " + SF + MoreCommands.formatFileSize(store.getTotalSpace());
            } catch (IOException ignored) {}
        output +=     "\n\n    Total left: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getFreeSpace()) +
                "\n    Total used: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getUsedSpace()) +
                "\n    Total: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSpace());
        output += "\n\nRAM:" +
                "\n    Used by process: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamUsage()) +
                "\n    Allocated to process: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getProcessRamMax()) +
                "\n    Used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamUsage()) +
                "\n    RAM of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemRamMax()) +
                "\n    Swap used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapUsage()) +
                "\n    Swap of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapMax()) +
                "\n    Total RAM used by host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()) +
                "\n    Total RAM of host pc: " + SF + MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamMax());
        output += "\n\nCPU:" +
                "\n    Unit: " + SF + Compat.get().getProcessorString() +
                "\n    Cores: " + SF + UsageMonitorer.getProcessorCount() +
                "\n    Used by process: " + SF + UsageMonitorer.getProcessCpuLoad()*100 +
                "\n    Used by system: " + SF + UsageMonitorer.getSystemCpuLoad()*100;
        if (ctx != null) sendMsg(ctx, output);
        else ClientCommand.sendMsg(output);
        return 1;
    }
}
