package com.ptsmods.morecommands.client.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public class UrbanCommand extends ClientCommand {
    private static final Map<String, List<Map<String, ?>>> cache = new HashMap<>();
    private static final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("MMMM d yyyy");
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        parseFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dispatcher.register(cLiteral("urban")
                .then(cArgument("query", StringArgumentType.greedyString())
                        .executes(this::execute)));
    }

    @Override
    public String getDocsPath() {
        return "/urban";
    }

    @SuppressWarnings("unchecked")
    private int execute(CommandContext<ClientSuggestionProvider> ctx) {
        MoreCommands.execute(() -> {
            int result = 0;
            String query = ctx.getArgument("query", String.class);
            if (MoreCommands.isInteger(query.split(" ")[0])) {
                result = Integer.parseInt(query.split(" ")[0]);
                query = query.substring(query.indexOf(' ')+1);
            }
            List<Map<String, ?>> results;
            try {
                results = cache.containsKey(query.toLowerCase()) ? cache.get(query.toLowerCase()) : (List<Map<String, ?>>) new Gson().fromJson(MoreCommands.getHTML("https://api.urbandictionary.com/v0/define?term=" +
                        query.replace(' ', '+')), Map.class).get("list");
                cache.put(query.toLowerCase(), results);
            } catch (IOException e) {
                log.catching(e);
                sendMsg(ChatFormatting.RED + "An unknown error occurred while trying to get the search results from urban dictionary.");
                return;
            }
            if (result >= results.size()) sendError("Only found %s%d %sresults for query %s%s %swhile result %s%d %swas requested.", ChatFormatting.DARK_RED, results.size(), ChatFormatting.RED, ChatFormatting.DARK_RED, query,
                    ChatFormatting.RED, ChatFormatting.DARK_RED, result, ChatFormatting.RED);
            else {
                Map<String, ?> data = results.get(result);
                sendMsg("Result for query " + SF + query + DF + ":");
                sendMsg(parseText(cleanString(data.get("definition").toString())));
                String ex = data.get("example") == null || data.get("example").toString().isEmpty() ? null : data.get("example").toString();
                if (ex != null) {
                    sendMsg("Example:");
                    sendMsg(parseText(cleanString(ex)));
                }
                sendMsg("\u00A0");
                sendMsg(ChatFormatting.GREEN + "Thumbs up: " + ((Double) data.get("thumbs_up")).intValue());
                sendMsg(ChatFormatting.RED + "Thumbs down: " + ((Double) data.get("thumbs_down")).intValue());
                LiteralTextBuilder link = literalText("Click ");
                link.withStyle(DS);
                LiteralTextBuilder linkChild = literalText("here");
                linkChild.withStyle(Style.EMPTY.applyFormats(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, data.get("permalink").toString())));
                link.append(linkChild);
                linkChild = literalText(" to view this result in the browser.");
                linkChild.withStyle(DS);
                link.append(linkChild);
                sendMsg(link);
                sendMsg("\u00A0");
                Date date = null;
                try {
                    date = parseFormat.parse(data.get("written_on").toString());
                } catch (ParseException e) {
                    log.catching(e);
                }
                sendMsg("Written by " + SF + data.get("author") + DF + " on " + SF + (date == null ? "UNKNOWN" : formatDate.format(date) + DF + " at " + SF + formatTime.format(date)) + DF + ".");

                LiteralTextBuilder pager = literalText("");
                LiteralTextBuilder prev = literalText("[<<<]");
                if (result > 0) prev.withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result-1) + " " + query)));
                else prev.withStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY));
                pager.append(prev);
                pager.append(literalText("  "));
                LiteralTextBuilder next = literalText("[>>>]");

                if (result < results.size()-1) next.withStyle(Style.EMPTY.applyFormat(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + (result+1) + " " + query)));
                else next.withStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY));
                pager.append(next);
                sendMsg(pager);
            }
        });
        return 1;
    }

    private String cleanString(String s) {
        s = s.replaceAll("\r", "");
        while (s.contains("\n\n")) s = s.replaceAll("\n\n", "\n");
        return s.trim();
    }

    private TextBuilder<?> parseText(String s) {
        s += "[]";
        LiteralTextBuilder text = literalText("  ");
        text.withStyle(DS);
        while (s.contains("[")) {
            text.append(literalText(s.substring(0, s.indexOf('['))));
            String term = s.substring(s.indexOf('[')+1, s.indexOf(']'));
            s = s.substring(s.indexOf(']')+1);
            if (term.isEmpty()) continue;

            LiteralTextBuilder child = literalText(term);

            LiteralTextBuilder hoverText = literalText("Click to look up ");
            hoverText.withStyle(DS);

            LiteralTextBuilder hoverTextChild = literalText(term);
            hoverTextChild.withStyle(SS);
            hoverText.append(hoverTextChild);

            hoverTextChild = literalText(".");
            hoverTextChild.withStyle(DS);
            hoverText.append(hoverTextChild);

            child.withStyle(SS
                    .applyFormat(ChatFormatting.UNDERLINE)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/urban " + term))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.build())));
            text.append(child);
        }

        return text;
    }
}
