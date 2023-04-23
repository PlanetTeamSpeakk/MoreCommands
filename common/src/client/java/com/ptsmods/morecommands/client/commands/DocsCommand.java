package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import java.util.Locale;

public class DocsCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        LiteralArgumentBuilder<ClientSuggestionProvider> clientCommand = cLiteral("clientcommand");
        MoreCommandsClient.getNodes().forEach((cmd, nodes) -> nodes.forEach(node -> clientCommand
                .then(cLiteral(node.getName())
                        .executes(ctx -> open("/commands/client" + (cmd.getDocsPath().startsWith("/") ? "" : "/") + cmd.getDocsPath())))));

        LiteralArgumentBuilder<ClientSuggestionProvider> command = cLiteral("command");
        MoreCommands.getNodes().forEach((cmd, nodes) -> nodes.forEach(node -> command
                .then(cLiteral(node.getName())
                        .executes(ctx -> open("/commands/server" + (cmd.getDocsPath().startsWith("/") ? "" : "/") + cmd.getDocsPath())))));

        LiteralArgumentBuilder<ClientSuggestionProvider> gamerule = cLiteral("gamerule");
        IMoreGameRules.get().allRules().forEach((name, key) -> gamerule
                .then(cLiteral(name)
                        .executes(ctx -> open("/gamerules/" + hyphenCase(name)))));

        LiteralArgumentBuilder<ClientSuggestionProvider> clientOption = cLiteral("clientoption");
        ClientOption.getKeyMappedOptions().forEach((key, option) -> clientOption
                .then(cLiteral(key)
                        .executes(ctx -> open("/client-options/" + option.getCategory().name().toLowerCase(Locale.ROOT).replace('_', '-') + '/' +
                                option.getName().toLowerCase(Locale.ROOT).replace(' ', '-')))));

        dispatcher.register(cLiteral("mcdocs")
                .executes(ctx -> open(""))
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
        Util.getPlatform().openUri("https://morecommands.ptsmods.com" + path);
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
