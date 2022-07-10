package com.ptsmods.morecommands.mixin.client.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockModel.class)
public interface MixinJsonUnbakedModelAccessor {
    @Accessor @Mutable
    void setTransforms(ItemTransforms transformations);

    @Accessor("transforms")
    ItemTransforms getRawTransforms();

    @Accessor
    BlockModel getParent();

    @Accessor
    ResourceLocation getParentLocation();

    @Accessor
    Map<String, Either<Material, String>> getTextureMap();
}
