package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.util.UsageMonitorer;
import net.minecraft.commands.CommandSourceStack;
import oshi.SystemInfo;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class SysInfoCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("sysinfo")
                .executes(ctx -> sendSysInfo(s -> sendMsg(ctx, s))));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true; // It is the exact same as /csysinfo on singleplayer anyway.
    }

    @Override
    public String getDocsPath() {
        return "/elevated/sys-info";
    }

    public static int sendSysInfo(Consumer<String> sender) {
        // Copied from my Discord bot Impulse at https://github.com/PlanetTeamSpeakk/Impulse
        // Copied from Owner#sysinfo.
        StringBuilder output = new StringBuilder("Host PC OS:");
        output
                .append("\n    Name: ")
                .append(SF).append(UsageMonitorer.getOSName())
                .append("\n    Version: ")
                .append(SF).append(UsageMonitorer.getOSVersion())
                .append("\n    Architecture: ")
                .append(SF).append(UsageMonitorer.getOSArch());

        output
                .append("\n\nJVM:" + "\n    Loaded classes: ")
                .append(SF).append(UsageMonitorer.getCurrentLoadedClassCount())
                .append("\n    Unloaded classes: ")
                .append(SF).append(UsageMonitorer.getUnloadedClassCount())
                .append("\n    Total classes: ")
                .append(SF).append(UsageMonitorer.getTotalLoadedClassCount())
                .append("\n    Live threads: ")
                .append(SF).append(UsageMonitorer.getLiveThreadCount())
                .append("\n    Daemon threads: ")
                .append(SF).append(UsageMonitorer.getDaemonThreadCount())
                .append("\n    Total threads: ")
                .append(SF).append(UsageMonitorer.getTotalThreadCount())
                .append("\n    Peak livethreadcount: ")
                .append(SF).append(UsageMonitorer.getPeakLiveThreadCount());

        output.append("\n\nDrives:");
        for (Path root : FileSystems.getDefault().getRootDirectories())
            try {
                FileStore store = Files.getFileStore(root);
                output
                        .append("\n    ")
                        .append(root)
                        .append("\n        Left: ")
                        .append(SF).append(MoreCommands.formatFileSize(store.getUsableSpace()))
                        .append("\n        Used: ")
                        .append(SF).append(MoreCommands.formatFileSize(store.getTotalSpace() - store.getUsableSpace()))
                        .append("\n        Total: ")
                        .append(SF).append(MoreCommands.formatFileSize(store.getTotalSpace()));
            } catch (IOException ignored) {}

        output
                .append("\n\n    Total left: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getFreeSpace()))
                .append("\n    Total used: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getUsedSpace()))
                .append("\n    Total: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getTotalSpace()));

        output
                .append("\n\nRAM:" + "\n    Used by process: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getProcessRamUsage()))
                .append("\n    Allocated to process: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getProcessRamMax()))
                .append("\n    Used by host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getSystemRamUsage()))
                .append("\n    RAM of host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getSystemRamMax()))
                .append("\n    Swap used by host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapUsage()))
                .append("\n    Swap of host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getSystemSwapMax()))
                .append("\n    Total RAM used by host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamUsage()))
                .append("\n    Total RAM of host pc: ")
                .append(SF).append(MoreCommands.formatFileSize(UsageMonitorer.getTotalSystemRamMax()));

        output
                .append("\n\nCPU:" + "\n    Unit: ")
                .append(SF).append(new SystemInfo().getHardware().getProcessor())
                .append("\n    Cores: ")
                .append(SF).append(UsageMonitorer.getProcessorCount())
                .append("\n    Used by process: ")
                .append(SF).append(UsageMonitorer.getProcessCpuLoad() * 100)
                .append("\n    Used by system: ")
                .append(SF).append(UsageMonitorer.getSystemCpuLoad() * 100);

        sender.accept(output.toString());
        return 1;
    }
}
