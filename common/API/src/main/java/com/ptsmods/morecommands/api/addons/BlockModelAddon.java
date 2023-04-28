package com.ptsmods.morecommands.api.addons;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface BlockModelAddon {
    void setTransforms(ItemTransforms transformations);

    ItemTransforms getRawTransforms();

    BlockModel getParent();

    ResourceLocation getParentLocation();

    Map<String, Either<Material, String>> getTextureMap();
}
