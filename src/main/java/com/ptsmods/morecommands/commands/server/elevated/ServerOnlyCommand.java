package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class ServerOnlyCommand extends Command {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literal("serveronly").requires(IS_OP)
                .executes(ctx -> {
                    File file = new File("config/MoreCommands/SERVERONLY.txt");

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
                    sendMsg(ctx, "Server-only mode has been " + formatFromBool(b, "enabled", "disabled") + DF + ". Clients will " + (b ? "now" : "no longer") + " need to have MoreCommands installed." + (b != MoreCommands.SERVER_ONLY ? "\n" + Formatting.RED + "A server restart is required." : ""));
                    return b ? 2 : 1;
                }));
    }

    @Override
    public boolean forDedicated() {
        return true;
    }
}
