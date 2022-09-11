package com.ptsmods.morecommands.api.forge;

import com.ptsmods.morecommands.forge.MoreCommandsForge;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
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
    public static boolean checkPermission(SharedSuggestionProvider source, String permission) {
        return !(source instanceof CommandSourceStack) || !(((CommandSourceStack) source).getEntity() instanceof ServerPlayer) ||
                MoreCommandsForge.getPermissionNode(permission) == null || PermissionAPI.getPermission(((CommandSourceStack) source).getPlayerOrException(), MoreCommandsForge.getPermissionNode(permission));
    }

    public static boolean checkPermission(SharedSuggestionProvider source, String permission, boolean fallback) {
        return fallback;
    }

    public static void doMLSpecificClientInit() {
    }

    public static Predicate<CommandSourceStack> requirePermission(String permission, int defaultRequiredLevel) {
        return source -> source.hasPermission(defaultRequiredLevel);
    }

    public static Path getJar() {
        return null;
//        ModList.get().getModContainerById("morecommands")
//                .orElseThrow(NullPointerException::new)
//                .getModInfo().;// TODO
    }
}
