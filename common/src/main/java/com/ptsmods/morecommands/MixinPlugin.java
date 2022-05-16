package com.ptsmods.morecommands;

import com.ptsmods.morecommands.lib.com.chocohead.mm.EnumExtender;
import com.ptsmods.morecommands.lib.com.chocohead.mm.api.EnumAdder;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final boolean tweakerooLoaded = MoreCommandsArch.isFabricModLoaded("tweakeroo");
	private static final boolean originsLoaded = MoreCommandsArch.isFabricModLoaded("origins");
	private static final boolean carpetLoaded = MoreCommandsArch.isFabricModLoaded("carpet");

	@Override
	public void onLoad(String mixinPackage) {}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (originsLoaded && mixinClassName.contains("OriginsCompat")) return false;
		if (tweakerooLoaded && mixinClassName.contains("TweakerooCompat")) return false;
		if (carpetLoaded && mixinClassName.contains("CarpetCompat")) return false;

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		if (mixinClassName.endsWith("MixinFormatting"))
			EnumExtender.makeEnumExtender(new EnumAdder(targetClassName, String.class, char.class, boolean.class)
					.addEnum("RAINBOW", "RAINBOW", 'u', true)).accept(targetClass);
		else if (mixinClassName.endsWith("MixinClickEventAction"))
			EnumExtender.makeEnumExtender(new EnumAdder(targetClassName, String.class, boolean.class)
					.addEnum("SCROLL", "scroll", false)).accept(targetClass);
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
