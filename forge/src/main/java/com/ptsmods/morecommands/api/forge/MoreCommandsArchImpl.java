package com.ptsmods.morecommands.api.forge;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.forge.MoreCommandsForge;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Predicate;

public class MoreCommandsArchImpl {
    @Getter(lazy = true)
    private static final Path jar = ModList.get().getModContainerById(MoreCommands.MOD_ID)
            .orElseThrow(NullPointerException::new)
            .getModInfo()
            .getOwningFile()
            .getFile()
            .getFilePath();

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve("MoreCommands");
    }

    public static boolean isFabricModLoaded(String id) {
        return false;
    }

    @SneakyThrows
    public static boolean checkPermission(SharedSuggestionProvider source, String permission) {
        return !(source instanceof CommandSourceStack) || !(((CommandSourceStack) source).getEntity() instanceof ServerPlayer) ||
                MoreCommandsForge.checkPermission(((CommandSourceStack) source).getPlayerOrException(), permission);
    }

    public static boolean checkPermission(SharedSuggestionProvider source, String permission, boolean fallback) {
        return checkPermission(source, permission);
    }

    public static Predicate<CommandSourceStack> requirePermission(String permission, int defaultRequiredLevel) {
        return source -> source.hasPermission(defaultRequiredLevel);
    }

    public static Path getMinecraftJar() {
        return FMLLoader.getLoadingModList().getModFileById("minecraft")
                .getFile()
                .getFilePath();
    }

    // The dev env should never be run via Forge anyway.
    // This because Forge uses modules to load mods and every module
    // of this mod becomes its own Java module in Forge env.
    // This doesn't work, however, because all compat modules share the
    // same package name which Java modules don't like.
    public static Path getClientJar() {
        return getJar();
    }
}
