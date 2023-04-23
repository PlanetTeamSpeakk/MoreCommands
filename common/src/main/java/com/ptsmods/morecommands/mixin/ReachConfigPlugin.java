package com.ptsmods.morecommands.mixin;

import com.google.common.collect.ImmutableList;
import com.ptsmods.morecommands.api.MoreCommandsArch;
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
    private static final boolean pehkuiLoaded = MoreCommandsArch.isFabricModLoaded("pehkui");
    private static final List<String> incompatibleMixins = ImmutableList.<String>builder()
            .add("MixinServerPlayerInteractionManager")
            .add("MixinServerPlayNetworkHandler")
            .add("MixinScreenHandler")
            .add("MixinItem")
            .add("MixinForgingScreenHandler")
            .add("MixinLootableContainerBlockEntity")
            .add("MixinAbstractFurnaceBlockEntity")
            .add("MixinBrewingStandBlockEntity")
            .add("MixinClientPlayerInteractionManager")
            .add("MixinGameRenderer")
            .build();

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (isPehkuiLoaded() && (mixinClassName.contains("Furnace") || mixinClassName.contains("BrewingStand") || mixinClassName.contains("LootableContainer") ||
                mixinClassName.contains("ServerPlayNetworkHandler") || mixinClassName.contains("ServerPlayerInteractionManager"))) return false;
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
