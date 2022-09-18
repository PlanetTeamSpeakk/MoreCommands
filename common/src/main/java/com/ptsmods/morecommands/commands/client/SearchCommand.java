package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.MessageHistory;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchCommand extends ClientCommand {
    public static ClickEvent.Action SCROLL_ACTION = null;

    public void preinit() {
        if (SCROLL_ACTION == null) SCROLL_ACTION = Holder.getScrollAction();
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        // This class is just the tip of the iceberg of this feature. Have a look at MixinChatHud and MixinChatScreen to see the rest.
        dispatcher.register(cLiteral("search")
                .then(cArgument("query", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            List<GuiMessageAddon> results = MessageHistory.search(ctx.getArgument("query", String.class).toLowerCase());
                            if (results.isEmpty()) return sendError("No results could be found for the given query.");

                            sendMsg("Found " + SF + results.size() + DF + " result" + (results.size() == 1 ? "" : "s") + " (click on one to scroll to it):");
                            AtomicInteger i = new AtomicInteger(1);
                            results.forEach(line -> sendMsg(emptyText()
                                    .append(literalText("  " + i.getAndIncrement() + ". "))
                                    .append(Compat.get().builderFromText(line.mc$getRichContent()))
                                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(SCROLL_ACTION, String.valueOf(line.mc$getId()))))));

                            return results.size();
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/search";
    }
}
