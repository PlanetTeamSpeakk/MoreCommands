package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBox.class)
public interface MixinBlockBoxAccessor {
    // According to this comment: https://www.curseforge.com/minecraft/mc-mods/morecommands?comment=296
    // these aren't public in some versions or something like that.
    @Accessor("minX") int getMinX_();
    @Accessor("minY") int getMinY_();
    @Accessor("minZ") int getMinZ_();
    @Accessor("maxX") int getMaxX_();
    @Accessor("maxY") int getMaxY_();
    @Accessor("maxZ") int getMaxZ_();
}
