package com.ptsmods.morecommands.commands.client;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;

public class DefineCommand extends ClientCommand {
    private static final List<String> languages = ImmutableList.of("en", "hi", "es", "fr", "ja", "ru", "de", "it", "ko", "pt-BR", "ar", "tr");

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<ClientCommandSource> cmd = cLiteral("define");
        for (String lang : languages) {
            cmd.then(cLiteral(lang)
                    .then(cArgument("query", StringArgumentType.word())
                            .executes(ctx -> {
                                MoreCommands.execute(() -> {
                                    List<Map<String, Object>> data;
                                    try {
                                        data = MoreCommands.gson.fromJson(MoreCommands.getHTML("https://api.dictionaryapi.dev/api/v2/entries/" + lang + "/" + ctx.getArgument("query", String.class)),
                                                new TypeToken<List<Map<String, Object>>>(){}.getType());
                                    } catch (Exception e) {
                                        if (!e.getClass().getSimpleName().toLowerCase().contains("json")) log.catching(e);
                                        sendMsg(Formatting.RED + "No results were found for the given query.");
                                        return;
                                    }

                                    for (Map<String, Object> map : data) {
                                        List<Map<String, String>> phonetics = (List<Map<String, String>>) map.get("phonetics");
                                        sendMsg(map.get("word").toString() + (phonetics.size() == 0 || phonetics.get(0).size() == 0 ? "" : " " + SF + "(" + phonetics.get(0).get("text") + ")"));
                                        List<Map<String, Object>> meanings = (List<Map<String, Object>>) map.get("meanings");
                                        for (Map<String, Object> meaning : meanings) {
                                            sendMsg("  " + MoreCommands.pascalCase(meaning.get("partOfSpeech").toString(), true));
                                            List<Map<String, String>> definitions = (List<Map<String, String>>) meaning.get("definitions");
                                            for (int i = 0; i < definitions.size(); i++) {
                                                Map<String, String> definition = definitions.get(i);
                                                sendMsg("    " + definition.get("definition"));
                                                if (definition.containsKey("example") && definition.get("example") != null)
                                                    sendMsg("    " + SF + Formatting.ITALIC + definition.get("example"));
                                                if (i != definitions.size() - 1) sendMsg("\u00A0");
                                            }
                                        }
                                    }
                                });
                                return 1;
                            })));
        }
        dispatcher.register(cmd);
    }

    @Override
    public String getDocsPath() {
        return "/define";
    }
}
