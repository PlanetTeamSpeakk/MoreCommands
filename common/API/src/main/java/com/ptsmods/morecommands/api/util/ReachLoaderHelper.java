package com.ptsmods.morecommands.api.util;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.Version;
import dev.architectury.platform.Platform;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ReachLoaderHelper {
    @Getter(lazy = true)
    private static final boolean reachAttributesLoaded = MoreCommandsArch.isFabricModLoaded("reach-entity-attributes");
    @Getter(lazy = true)
    private static final boolean pehkuiLoaded = Platform.isModLoaded("pehkui");
    private static final List<String> incompatibleMixins = ImmutableList.<String>builder()
            .add("MixinServerPlayerInteractionManager")
            .add("MixinServerPlayNetworkHandler")
            .add("MixinScreenHandler")
            .add("MixinItem")
            .add("MixinForgingScreenHandler")
            .add("MixinRandomizableContainerBlockEntity")
            .add("MixinAbstractFurnaceBlockEntity")
            .add("MixinBrewingStandBlockEntity")
            .add("MixinClientPlayerInteractionManager")
            .add("MixinGameRenderer")
            .build();
    // These are no longer required after 1.19.4
    private static final List<String> obsoleteMixins = ImmutableList.<String>builder()
            .add("MixinAbstractFurnaceBlockEntity")
            .add("MixinBrewingStandBlockEntity")
            .add("MixinRandomizableContainerBlockEntity")
            .build();

    public static boolean shouldApplyReachMixin(String mixinClassName) {
        if (Version.getCurrent().isOlderThan(Version.V1_19_4) && mixinClassName.contains("MixinContainer")) return false;
        if (Version.getCurrent().isNewerThanOrEqual(Version.V1_19_4) && obsoleteMixins.stream().anyMatch(mixinClassName::contains)) return false;
        if (isPehkuiLoaded() && (mixinClassName.contains("Furnace") || mixinClassName.contains("BrewingStand") || mixinClassName.contains("RandomizableContainer") ||
                mixinClassName.contains("ServerGamePacketListenerImpl") || mixinClassName.contains("ServerPlayerGameMode"))) return false;
        if (isReachAttributesLoaded() && incompatibleMixins.contains(mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1))) return false;
        if (Platform.isForge() && (mixinClassName.contains("MixinServerPlayerGameMode") || mixinClassName.contains("MixinServerGamePacketListenerImpl"))) return false;
        return isReachAttributesLoaded() || !mixinClassName.endsWith("MixinReachEntityAttributes");
    }
}
