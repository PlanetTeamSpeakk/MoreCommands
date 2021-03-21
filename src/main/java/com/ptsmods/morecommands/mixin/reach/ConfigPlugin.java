package com.ptsmods.morecommands.mixin.reach;

import com.google.common.collect.ImmutableList;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ConfigPlugin implements IMixinConfigPlugin {
    private static final boolean reachAttributesLoaded = FabricLoader.getInstance().isModLoaded("reach-entity-attributes");
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
        return (!reachAttributesLoaded || !incompatibleMixins.contains(mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1))) && (reachAttributesLoaded || !mixinClassName.endsWith("MixinReachEntityAttributes"));
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
