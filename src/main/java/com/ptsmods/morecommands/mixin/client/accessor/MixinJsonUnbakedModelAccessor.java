package com.ptsmods.morecommands.mixin.client.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(JsonUnbakedModel.class)
public interface MixinJsonUnbakedModelAccessor {
    @Accessor @Mutable
    void setTransformations(ModelTransformation transformations);

    @Accessor("transformations")
    ModelTransformation getRawTransformations();

    @Accessor
    JsonUnbakedModel getParent();

    @Accessor
    Identifier getParentId();

    @Accessor
    Map<String, Either<SpriteIdentifier, String>> getTextureMap();
}
