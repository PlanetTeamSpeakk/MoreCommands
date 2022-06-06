package com.ptsmods.morecommands.api.clientoptions;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@ExtensionMethod(ObjectExtensions.class)
public enum ClientOptionCategory {
    RENDERING("Rendering", "These options change or add things regarding rendering."),
    TWEAKS("Tweaks", "Some tweaks to change your game.", "These are harmless, but can be very useful."),
    CHEATS("Cheats", () -> (BooleanClientOption) ClientOption.getOptions().get(TWEAKS).get("Hidden Options"), "Some less harmless tweaks.", "All of them are set to mimic the default behaviour of Minecraft,",
            "meaning that their default values don't change anything.", "", "\u00A7cTo prevent you getting an unfair advantage,", "\u00A7cthese options only affect singleplayer worlds."),
    CHAT("Chat", "Chat related tweaks.", "Most of these are enabled by default."),
    EASTER_EGGS("Easter Eggs", () -> (BooleanClientOption) ClientOption.getOptions().get(TWEAKS).get("Hidden Options"), "Don't look in here.", "Stay away.", "", "Keep \u00A7c\u00A7lOUT\u00A7r!! >:c");

    private final String name;
    @Nullable
    private final Supplier<BooleanClientOption> hidden;
    private final List<String> comments;

    ClientOptionCategory(@NonNull String name, String... comments) {
        this(name, null, comments);
    }

    ClientOptionCategory(@NonNull String name, @Nullable Supplier<BooleanClientOption> hidden, String... comments) {
        this.name = name;
        this.hidden = hidden;
        this.comments = comments.ifNonNull(ImmutableList::copyOf);
    }

    public String getName() {
        return name;
    }

    @Nullable
    public BooleanClientOption getHidden() {
        return hidden.ifNonNull(Supplier::get);
    }

    public List<String> getComments() {
        return comments;
    }
}
