package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Locale;
import java.util.Objects;

@Mixin(GuiMessage.class)
public abstract class MixinGuiMessage implements GuiMessageAddon {
    private static @Unique int nextId = 0;
    private @Unique String stringContent = null, stringContentStripped = null;
    private final @Unique int id = nextId++;

    @Override
    public void mc$setStringContent(String content) {
        this.stringContent = content;
        stringContentStripped = Objects.requireNonNull(ChatFormatting.stripFormatting(content)).toLowerCase(Locale.ROOT);
    }

    @Override
    public String mc$getStringContent() {
        return stringContent;
    }

    @Override
    public String mc$getStrippedContent() {
        return stringContentStripped;
    }

    @Override
    public int mc$getId() {
        return id;
    }
}
