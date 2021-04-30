package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
    public String getTranslationKey() {
        if (mc_translationKey == null) {
            Identifier id = Registry.BLOCK.getId(ReflectionHelper.<WallSignBlock>cast(this));
            mc_translationKey = Registry.BLOCK.get(new Identifier(id.getNamespace(), id.getPath().replace("wall_", ""))).getTranslationKey();
        }
        return mc_translationKey;
    }
}
