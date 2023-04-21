package com.ptsmods.morecommands.fabric;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.api.Version;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private static final List<String> postSplitMixins = Lists.newArrayList(
            "MixinCreativeInventoryScreen",
            "MixinItemGroupButtonWidget",
            "MixinItemGroupButtonWidgetAccessor"
    );
    // Whether the fabric item groups API is present and its version is greater than or equal to the version
    // where the classes were split.
    @Getter(lazy = true)
    private static final boolean isPostSplit = FabricLoader.getInstance().getModContainer("fabric-item-groups-v0")
            .map(c -> c.getMetadata().getVersion())
            .filter(v -> v instanceof SemanticVersion sv && sv.getVersionComponentCount() >= 3)
            .map(v -> (SemanticVersion) v)
            .filter(v -> v.getVersionComponent(2) >= 34 || v.getVersionComponent(1) > 3 || v.getVersionComponent(0) > 0)
            .isPresent();

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // In Fabric API 0.67.0, all classes were split in client-only and common which also changed packages.
        // This mod has no compat stuff for a specific Minecraft version or a specific API version, so
        // getting these mixins to still somehow work on pre-0.67.0 while building the mod with 0.67.0+
        // is too big of a feat for me to want to implement. Hence, the features that involve these mixins
        // (that being the creative inventory key pager and the big pager buttons tweaks) will just simply
        // only work on Fabric API 0.67.0+ (and only the one for 1.19.2 and above, they did not make this
        // change on 1.18.2).
        if (postSplitMixins.stream().anyMatch(mixinClassName::contains)) return isPostSplit();

        return !mixinClassName.contains("MixinHopperBlockEntity") || Version.getCurrent().isNewerThanOrEqual(Version.V1_17);
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
