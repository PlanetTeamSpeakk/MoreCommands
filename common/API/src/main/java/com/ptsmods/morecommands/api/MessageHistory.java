package com.ptsmods.morecommands.api;

import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@UtilityClass
public class MessageHistory {
    private final IntObjectMap<GuiMessageAddon> messages = new IntObjectHashMap<>();

    public boolean contains(int id) {
        return messages.containsKey(id);
    }

    public GuiMessageAddon getMessage(int id) {
        return messages.get(id);
    }

    public void putMessage(GuiMessageAddon msg) {
        if (msg != null && !messages.containsKey(msg.mc$getId()))
            messages.put(msg.mc$getId(), msg);
    }

    public void removeMessage(int id) {
        messages.remove(id);
    }

    public List<GuiMessageAddon> search(String query) {
        String q = query.toLowerCase(Locale.ROOT);

        return messages.values().stream()
                .filter(msg -> msg.mc$getStrippedContent().contains(q))
                .collect(Collectors.toList());
    }

    public void clear() {
        messages.clear();
    }
}
