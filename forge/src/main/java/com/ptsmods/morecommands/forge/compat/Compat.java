package com.ptsmods.morecommands.forge.compat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.ForgeCompat;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// (Probably) due to an Architectury limitation, Forge projects cannot be implemented in any other project,
// so the ForgeCompat classes can only be referenced via reflection.
// This also means that running Forge in a dev env of this mod will never work, but it already didn't work because
// every compat project shares the com.ptsmods.morecommands.compat package which modules don't like.
public enum Compat implements ForgeCompat {
    INSTANCE;

    private final LoadingCache<String, Optional<ForgeCompat>> compats = CacheBuilder.newBuilder().build(CacheLoader.from(version ->
            Optional.ofNullable(ReflectionHelper.newInstance(ReflectionHelper.getCtor(ReflectionHelper.getClass("com.ptsmods.morecommands.forge.compat.ForgeCompat" + version))))));

    @Override
    public boolean shouldRegisterListeners() {
        return true;
    }

    @Override
    public void registerListeners() {
        List<Version> versions = ImmutableList.of(Version.V1_19, Version.V1_18_2, Version.V1_18, Version.V1_17);
        versions.stream()
                .map(this::getCompat)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ForgeCompat::shouldRegisterListeners)
                .forEach(ForgeCompat::registerListeners);
    }

    @Override
    public void registerPermission(String permission, int defaultLevel, String desc) {
        getEitherCompat(Version.V1_17, Version.V1_18_2).registerPermission(permission, defaultLevel, desc);
    }

    @Override
    public boolean checkPermission(ServerPlayer player, String permission) {
        return getEitherCompat(Version.V1_17, Version.V1_18_2).checkPermission(player, permission);
    }

    private Optional<ForgeCompat> getCompat(Version version) {
        return Version.getCurrent().isNewerThanOrEqual(version) ? compats.getUnchecked("" + version.minor +
                (version.revision == null ? "" : version.revision)) : Optional.empty();
    }

    private ForgeCompat getEitherCompat(Version version1, Version version2) {
        return getCompat(version2).orElseGet(() -> getCompat(version1).orElseThrow(NoSuchElementException::new));
    }
}
