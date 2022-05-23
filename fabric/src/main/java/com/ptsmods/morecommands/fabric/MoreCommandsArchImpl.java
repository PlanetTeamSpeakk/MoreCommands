package com.ptsmods.morecommands.fabric;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.extensions.URLExtensions;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;

@ExtensionMethod(URLExtensions.class)
public class MoreCommandsArchImpl {
    @Getter(lazy = true, onMethod_ = @SneakyThrows)
    private static final Path jar = Optional.of(new File(((ModContainerImpl) FabricLoader.getInstance().getModContainer(MoreCommands.MOD_ID)
                    .orElseThrow(NullPointerException::new)).getOriginUrl().toURISneaky().getPath()).toPath())
            .map(jar -> Files.isDirectory(jar) && jar.getFileName().toString().equals("main") ?
                    Paths.get(String.join(File.separator, jar.getParent().getParent().getParent().getParent()
                            .toAbsolutePath().toString(), "common", "build", "classes", "java", "main", "")) : jar)
            .orElseThrow(NullPointerException::new);

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve("MoreCommands");
    }

    public static boolean isFabricModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static boolean checkPermission(CommandSource source, String permission) {
        return !isFabricModLoaded("fabric-permissions-api-v0") || Permissions.check(source, permission);
    }

    public static boolean checkPermission(CommandSource source, String permission, boolean fallback) {
        return !isFabricModLoaded("fabric-permissions-api-v0") || Permissions.check(source, permission, fallback);
    }

    public static void doMLSpecificClientInit() {
    }

    public static Predicate<ServerCommandSource> requirePermission(String permission, int defaultRequiredLevel) {
        return isFabricModLoaded("fabric-permissions-api-v0") ? Permissions.require(permission, defaultRequiredLevel) : source -> source.hasPermissionLevel(defaultRequiredLevel);
    }

    public static void addJarToClassPath(Path jar) {
        FabricLauncherBase.getLauncher().addToClassPath(jar);
    }
}
