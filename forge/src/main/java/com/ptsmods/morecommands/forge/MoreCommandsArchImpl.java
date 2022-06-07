package com.ptsmods.morecommands.forge;

import lombok.SneakyThrows;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.permission.PermissionAPI;

import java.nio.file.Path;
import java.util.function.Predicate;

public class MoreCommandsArchImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve("MoreCommands");
    }

    public static boolean isFabricModLoaded(String id) {
        return false;
    }

    @SneakyThrows
    public static boolean checkPermission(CommandSource source, String permission) {
        return !(source instanceof ServerCommandSource) || !(((ServerCommandSource) source).getEntity() instanceof ServerPlayerEntity) ||
                MoreCommandsForge.getPermissionNode(permission) == null || PermissionAPI.getPermission(((ServerCommandSource) source).getPlayerOrThrow(), MoreCommandsForge.getPermissionNode(permission));
    }

    public static boolean checkPermission(CommandSource source, String permission, boolean fallback) {
        return fallback;
    }

    public static void doMLSpecificClientInit() {
    }

    public static Predicate<ServerCommandSource> requirePermission(String permission, int defaultRequiredLevel) {
        return source -> source.hasPermissionLevel(defaultRequiredLevel);
    }
}
