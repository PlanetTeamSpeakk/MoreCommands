package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.ptsmods.morecommands.miscellaneous.ChatHudLineWithContent;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import sun.reflect.ConstructorAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchCommand extends ClientCommand {

    public static Map<Integer, ChatHudLineWithContent> lines = new HashMap<>();
    public static ClickEvent.Action SCROLL_ACTION = null;

    private void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (SCROLL_ACTION == null) {
            Class<ClickEvent.Action> c = ClickEvent.Action.class;
            Constructor<ClickEvent.Action> con = (Constructor<ClickEvent.Action>) c.getDeclaredConstructors()[0];
            con.setAccessible(true);
            Field constructorAccessorField = Constructor.class.getDeclaredField("constructorAccessor");
            constructorAccessorField.setAccessible(true);
            ConstructorAccessor ca = (ConstructorAccessor) constructorAccessorField.get(con);
            if (ca == null) {
                Method acquireConstructorAccessorMethod = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
                acquireConstructorAccessorMethod.setAccessible(true);
                ca = (ConstructorAccessor) acquireConstructorAccessorMethod.invoke(con);
            }
            SCROLL_ACTION = (ClickEvent.Action) ca.newInstance(new Object[] {"SCROLL", ClickEvent.Action.values().length, "scroll", false});
        }
    }

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        // This class is just the tip of the iceberg of this feature. Have a look at MixinChatHud and MixinChatScreen to see the rest.
        dispatcher.register(cLiteral("search").then(cArgument("query", StringArgumentType.greedyString()).executes(ctx -> {
            String query = ctx.getArgument("query", String.class).toLowerCase();
            List<ChatHudLineWithContent> results = new ArrayList<>();
            for (ChatHudLineWithContent line : lines.values()) {
                if (line.getContent() != null && line.getContentStripped().contains(query))
                    results.add(line);
            }
            if (results.isEmpty()) sendMsg(Formatting.RED + "No results could be found for the given query.");
            else {
                sendMsg("Found " + SF + results.size() + DF + " result" + (results.size() == 1 ? "" : "s") + " (click on one to scroll to it):");
                AtomicInteger i = new AtomicInteger(1);
                results.forEach(line -> sendMsg(new LiteralText("  " + i.getAndIncrement() + ". " + line.getContent()).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(SCROLL_ACTION, String.valueOf(line.getId()))))));
            }
            return results.size();
        })));
    }
}
