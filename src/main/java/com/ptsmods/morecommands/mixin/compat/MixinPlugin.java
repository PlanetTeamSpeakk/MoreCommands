package com.ptsmods.morecommands.mixin.compat;

import com.ptsmods.morecommands.compat.Compat;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.indexOf(".compat") == mixinClassName.lastIndexOf(".compat")) return true; // E.g. c.p.m.m.compat.MixinPlayerEntityAccessor, should load on all versions.
        int iVer = Compat.getIVer();
        int i = mixinClassName.lastIndexOf(".compat") + 7;
        int iVerMixin = Integer.parseInt(mixinClassName.substring(i, i + 2));
        Boolean support = mixinClassName.charAt(i + 2) == 'm' ? Boolean.FALSE : mixinClassName.charAt(i + 2) == 'p' ? Boolean.TRUE : null;
        return iVer == iVerMixin || iVer < iVerMixin && support == Boolean.FALSE || iVer > iVerMixin && support == Boolean.TRUE;
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
