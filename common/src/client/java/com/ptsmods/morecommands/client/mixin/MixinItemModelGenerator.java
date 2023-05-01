package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.ItemModelGeneratorAddon;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemModelGenerator.class)
public class MixinItemModelGenerator implements ItemModelGeneratorAddon {
    private @Unique boolean ignoreNext;

    @Override
    public boolean shouldIgnore() {
        return ignoreNext;
    }

    @Override
    public void ignoreNext() {
        ignoreNext = true;
    }

    @Override
    public void resetIgnore() {
        ignoreNext = false;
    }
}
