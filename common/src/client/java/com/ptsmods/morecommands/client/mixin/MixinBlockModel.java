package com.ptsmods.morecommands.client.mixin;

import com.mojang.datafixers.util.Either;
import com.ptsmods.morecommands.api.addons.BlockModelAddon;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(BlockModel.class)
public class MixinBlockModel implements BlockModelAddon {

    @Shadow @Final @Mutable
    private ItemTransforms transforms;

    @Shadow @Nullable
    protected BlockModel parent;

    @Shadow @Nullable protected ResourceLocation parentLocation;

    @Shadow @Final
    protected Map<String, Either<Material, String>> textureMap;

    @Override
    public void setTransforms(ItemTransforms transformations) {
        transforms = transformations;
    }

    @Override
    public ItemTransforms getRawTransforms() {
        return transforms;
    }

    @Override
    public @Nullable BlockModel getParent() {
        return parent;
    }

    @Override
    public @Nullable ResourceLocation getParentLocation() {
        return parentLocation;
    }

    @Override
    public Map<String, Either<Material, String>> getTextureMap() {
        return textureMap;
    }
}
