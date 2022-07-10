package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ArgumentTypeInfos.class)
public interface MixinArgumentTypesAccessor {

    @Accessor("BY_CLASS")
    static Map<Class<?>, ArgumentTypeInfo<?, ?>> getClassMap() {
        throw new AssertionError("This shouldn't happen.");
    }
}
