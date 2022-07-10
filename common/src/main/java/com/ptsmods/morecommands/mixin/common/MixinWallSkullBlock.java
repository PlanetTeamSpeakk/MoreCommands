package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WallSkullBlock.class)
public class MixinWallSkullBlock {
    private String mc_translationKey;

    /**
     * @author PlanetTeamSpeak
     * @reason Causes StackOverflowErrors otherwise
     */
    @Overwrite
    public String getDescriptionId() {
        if (mc_translationKey == null) {
            ResourceLocation id = Registry.BLOCK.getKey(ReflectionHelper.<WallSignBlock>cast(this));
            mc_translationKey = Registry.BLOCK.get(new ResourceLocation(id.getNamespace(), id.getPath().replace("wall_", ""))).getDescriptionId();
        }
        return mc_translationKey;
    }
}
