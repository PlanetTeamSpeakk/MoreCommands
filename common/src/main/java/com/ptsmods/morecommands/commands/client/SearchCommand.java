package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.miscellaneous.ChatHudLineWithContent;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchCommand extends ClientCommand {
    public static Map<Integer, ChatHudLineWithContent<Text>> lines = new HashMap<>();
    public static ClickEvent.Action SCROLL_ACTION = null;

    public void preinit() {
        if (SCROLL_ACTION == null) SCROLL_ACTION = ClickEvent.Action.valueOf("SCROLL"); // Should've been registered in EarlyRiser.
    }

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        // This class is just the tip of the iceberg of this feature. Have a look at MixinChatHud and MixinChatScreen to see the rest.
        dispatcher.register(cLiteral("search")
                .then(cArgument("query", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            Map<Integer, ChatHudLineWithContent<Text>> linesCopy = new HashMap<>(lines);
                            String query = ctx.getArgument("query", String.class).toLowerCase();
                            List<ChatHudLineWithContent<Text>> results = new ArrayList<>();
                            for (ChatHudLineWithContent<Text> line : lines.values())
                                if (line.getContent() != null && line.getContentStripped().contains(query))
                                    results.add(line);
                            if (results.isEmpty()) sendMsg(Formatting.RED + "No results could be found for the given query.");
                            else {
                                sendMsg("Found " + SF + results.size() + DF + " result" + (results.size() == 1 ? "" : "s") + " (click on one to scroll to it):");
                                AtomicInteger i = new AtomicInteger(1);
                                results.forEach(line -> sendMsg(literalText("  " + i.getAndIncrement() + ". " + line.getContent())
                                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(SCROLL_ACTION, String.valueOf(line.getId()))))));
                            }
                            lines = linesCopy;
                            return results.size();
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/search";
    }
}
