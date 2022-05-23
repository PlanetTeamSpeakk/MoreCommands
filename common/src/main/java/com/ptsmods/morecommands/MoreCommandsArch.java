package com.ptsmods.morecommands;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.nio.file.Path;
import java.util.function.Predicate;

public class MoreCommandsArch {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static void doMLSpecificClientInit() {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean isFabricModLoaded(String id) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean checkPermission(CommandSource source, String permission) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static boolean checkPermission(CommandSource source, String permission, boolean fallback) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static Predicate<ServerCommandSource> requirePermission(String permission, int defaultRequiredLevel) {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static Path getJar() {
        throw new AssertionError("This shouldn't happen.");
    }

    @ExpectPlatform
    public static void addJarToClassPath(Path jar) {
        throw new AssertionError("This shouldn't happen.");
    }
}
