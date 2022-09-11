package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;

public class ServerOnlyCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literal("serveronly").requires(IS_OP)
                .executes(ctx -> {
                    File file = MoreCommandsArch.getConfigDirectory().resolve("SERVERONLY.txt").toFile();

                    Properties props = new Properties();
                    try {
                        if (file.exists()) props.load(new FileReader(file));
                        props.setProperty("serverOnly", "" + !Boolean.parseBoolean(props.getProperty("serverOnly", "false")));
                        props.store(new PrintWriter(file), "Set the below value to true to enable server-only mode for MoreCommands.\n" +
                                "Clients will not need the mod to join the server when enabled.");
                    } catch (IOException e) {
                        sendError(ctx, "An unknown error occurred while setting server-only mode.");
                        return 0;
                    }

                    boolean b = Boolean.parseBoolean(props.getProperty("serverOnly"));
                    sendMsg(ctx, "Server-only mode has been " + Util.formatFromBool(b, "enabled", "disabled") + DF + ". Clients will " + (b ? "now" : "no longer") +
                            " need to have MoreCommands installed." + (b != MoreCommands.SERVER_ONLY ? "\n" + ChatFormatting.RED + "A server restart is required." : ""));
                    return b ? 2 : 1;
                }));
    }

    @Override
    public boolean isDedicatedOnly() {
        return true;
    }

    @Override
    public String getDocsPath() {
        return "/elevated/server-only";
    }
}
