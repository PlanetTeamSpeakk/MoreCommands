package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Util;

import java.util.Locale;

public class DocsCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
        LiteralArgumentBuilder<ClientCommandSource> clientCommand = cLiteral("clientcommand");
        MoreCommandsClient.getNodes().forEach((cmd, nodes) -> nodes.forEach(node -> clientCommand
                .then(cLiteral(node.getName())
                        .executes(ctx -> open("/commands/client" + (cmd.getDocsPath().startsWith("/") ? "" : "/") + cmd.getDocsPath())))));

        LiteralArgumentBuilder<ClientCommandSource> command = cLiteral("command");
        MoreCommands.getNodes().forEach((cmd, nodes) -> nodes.forEach(node -> command
                .then(cLiteral(node.getName())
                        .executes(ctx -> open("/commands/server" + (cmd.getDocsPath().startsWith("/") ? "" : "/") + cmd.getDocsPath())))));

        LiteralArgumentBuilder<ClientCommandSource> gamerule = cLiteral("gamerule");
        IMoreGameRules.get().allRules().forEach((name, key) -> gamerule
                .then(cLiteral(name)
                        .executes(ctx -> open("/gamerules/" + hyphenCase(name)))));

        LiteralArgumentBuilder<ClientCommandSource> clientOption = cLiteral("clientoption");
        ClientOption.getKeyMappedOptions().forEach((key, option) -> clientOption
                .then(cLiteral(key)
                        .executes(ctx -> open("/client-options/" + option.getCategory().name().toLowerCase(Locale.ROOT).replace('_', '-') + '/' +
                                option.getName().toLowerCase(Locale.ROOT).replace(' ', '-')))));

        dispatcher.register(cLiteral("mcdocs")
                .then(clientCommand)
                .then(command)
                .then(gamerule)
                .then(clientOption));
    }

    @Override
    public String getDocsPath() {
        return "/docs";
    }

    @Override
    public boolean doLateInit() {
        // This has to be registered after all commands have been registered.
        return true;
    }

    private static int open(String path) {
        Util.getOperatingSystem().open("https://morecommands.ptsmods.com" + path);
        return 1;
    }

    private static String hyphenCase(String camelCase) {
        StringBuilder sb = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c))
                sb.append('-').append(Character.toLowerCase(c));
            else sb.append(c);
        }

        return sb.toString();
    }
}
