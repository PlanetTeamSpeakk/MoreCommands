package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSignBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(WallBannerBlock.class)
public class MixinWallBannerBlock {
    private String mc_translationKey;

    /**
     * @author PlanetTeamSpeak
     * @reason Causes StackOverflowErrors otherwise
     */
    @Overwrite
    public String getDescriptionId() {
        if (mc_translationKey == null) {
            ResourceLocation id = Compat.get().<Block>getBuiltInRegistry("block").getKey(ReflectionHelper.<WallSignBlock>cast(this));
            if (id == null) return mc_translationKey;

            mc_translationKey = Optional.ofNullable(Compat.get().<Block>getBuiltInRegistry("block").get(
                    new ResourceLocation(id.getNamespace(), id.getPath().replace("wall_", ""))))
                    .map(Block::getDescriptionId)
                    .orElse(null);
        }
        return mc_translationKey;
    }
}
