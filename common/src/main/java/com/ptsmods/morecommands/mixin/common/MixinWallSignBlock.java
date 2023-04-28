package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WallSignBlock.class)
public class MixinWallSignBlock {
    private String mc_translationKey;

    /**
     * @author PlanetTeamSpeak
     * @reason Causes StackOverflowErrors otherwise
     */
    @Overwrite
    public String getDescriptionId() {
        if (mc_translationKey == null) {
            ResourceLocation id = Compat.get().<Block>getBuiltInRegistry("block").getKey(ReflectionHelper.<WallSignBlock>cast(this));
            mc_translationKey = Compat.get().<Block>getBuiltInRegistry("block").get(new ResourceLocation(id.getNamespace(), id.getPath().replace("wall_", ""))).getDescriptionId();
        }
        return mc_translationKey;
    }
}
