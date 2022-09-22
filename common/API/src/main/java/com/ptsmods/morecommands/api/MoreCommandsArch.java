package com.ptsmods.morecommands.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.nio.file.Path;
import java.util.function.Predicate;

public class MoreCommandsArch {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean isFabricModLoaded(String id) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean checkPermission(SharedSuggestionProvider source, String permission) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean checkPermission(SharedSuggestionProvider source, String permission, boolean fallback) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static Predicate<CommandSourceStack> requirePermission(String permission, int defaultRequiredLevel) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static Path getJar() {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static Path getMinecraftJar() {
        throw new AssertionError("This shouldn't happen.");
    }
}
