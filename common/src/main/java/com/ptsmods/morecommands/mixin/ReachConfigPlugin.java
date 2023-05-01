package com.ptsmods.morecommands.mixin;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.Version;
import dev.architectury.platform.Platform;
import lombok.Getter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ReachConfigPlugin implements IMixinConfigPlugin {
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

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (Version.getCurrent().isOlderThan(Version.V1_19_4) && mixinClassName.contains("MixinContainer")) return false;
        if (Version.getCurrent().isNewerThanOrEqual(Version.V1_19_4) && obsoleteMixins.stream().anyMatch(mixinClassName::contains)) return false;
        if (mixinClassName.contains("17") && Version.getCurrent().isNewerThanOrEqual(Version.V1_19) ||
            mixinClassName.contains("19") && Version.getCurrent().isOlderThan(Version.V1_19)) return false;
        if (isPehkuiLoaded() && (mixinClassName.contains("Furnace") || mixinClassName.contains("BrewingStand") || mixinClassName.contains("RandomizableContainer") ||
                mixinClassName.contains("ServerGamePacketListenerImpl") || mixinClassName.contains("ServerPlayerGameMode"))) return false;
        if (isReachAttributesLoaded() && incompatibleMixins.contains(mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1))) return false;
        if (Platform.isForge() && (mixinClassName.contains("MixinServerPlayerGameMode") || mixinClassName.contains("MixinServerGamePacketListenerImpl"))) return false;
        return isReachAttributesLoaded() || !mixinClassName.endsWith("MixinReachEntityAttributes");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
