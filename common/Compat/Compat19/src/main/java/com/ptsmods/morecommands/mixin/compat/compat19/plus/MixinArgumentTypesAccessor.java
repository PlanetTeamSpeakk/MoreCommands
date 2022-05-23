package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ArgumentTypes.class)
public interface MixinArgumentTypesAccessor {

    @Accessor("CLASS_MAP")
    static Map<Class<?>, ArgumentSerializer<?, ?>> getClassMap() {
        throw new AssertionError("This shouldn't happen.");
    }
}
