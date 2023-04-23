package com.ptsmods.morecommands.client.mixin;

import com.google.common.collect.Lists;
import com.mojang.math.Vector3f;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.client.mixin.accessor.MixinJsonUnbakedModelAccessor;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(ItemModelGenerator.class)
public abstract class MixinItemModelGenerator {
    private @Unique boolean ignoreNext = false;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemModelGenerator;processFrames(ILjava/lang/String;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)Ljava/util/List;"), method = "generateBlockModel")
    private List<BlockElement> create_addLayerElements(ItemModelGenerator itemModelGenerator, int layer, String key, TextureAtlasSprite sprite, Function<Material, TextureAtlasSprite> textureGetter, BlockModel blockModel) {
        ItemTransforms transformations = ((MixinJsonUnbakedModelAccessor) blockModel).getRawTransforms();

        for (ItemTransforms.TransformType mode : ItemTransforms.TransformType.values())
            if (transformations.hasTransform(mode)) {
                // Items that have a display tag in their model (generally items that look bigger in the hand)
                // completely break using my method, and I have no idea why.
                //
                // I can only assume it has something to do with them not being of a normal size or some kind
                // of atlas issue, but if anyone has any insight as to why it happens and how to approach a
                // fix, please let me know.
                // P.s. the problem looks like this: https://github.com/PlanetTeamSpeakk/MoreCommands/issues/35
                ignoreNext = true;
                break;
            }

        return !ignoreNext && ClientOptions.Rendering.fixItemSeams.getValue() && (ClientOptions.Rendering.fixAnimItemSeams.getValue() || sprite.getUniqueFrames().max().orElse(1) == 1) ?
                createSideElements(sprite, key, layer) : processFrames(layer, key, sprite); // Skip front and back layer, those are created as subcomponents now too.
    }

    /**
     * @reason Removes the seam found in item models generated from 2D images by making every pixel its own individual cube.
     * @author PlanetTeamSpeak
     */
    @Inject(at = @At("HEAD"), method = "createSideElements", cancellable = true)
    private void createSideElements(TextureAtlasSprite sprite, String key, int layer, CallbackInfoReturnable<List<BlockElement>> cbi) {
        if (!ignoreNext && ClientOptions.Rendering.fixItemSeams.getValue() && (ClientOptions.Rendering.fixAnimItemSeams.getValue() || sprite.getUniqueFrames().max().orElse(1) == 1)) {
            // Basically just does what the Vanilla Tweaks resource pack does programmatically.
            int width = sprite.getWidth();
            int height = sprite.getHeight();
            List<BlockElement> list = Lists.newArrayList();

            for (int frame = 0; frame < sprite.getUniqueFrames().max().orElse(1); frame++) {
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++) {
                        if (sprite.isTransparent(frame, x, y)) continue;
                        BlockElementFace face = new BlockElementFace(null, layer, key, new BlockFaceUV(new float[] {x, y, x + 1, y + 1}, 0));
                        int finalX = x, finalY = y;

                        // Transparency checks are sloooww
                        // Fast maps go brrr
                        Map<Pair<Integer, Pair<Integer, Integer>>, Boolean> map = new Object2BooleanOpenHashMap<>();
                        int finalFrame = frame;

                        list.add(new BlockElement(new Vector3f(x, height - y - 1, 7.5F), new Vector3f(x + 1, height - y, 8.5F),
                                Arrays.stream(Direction.values())
                                        // Transparency check is so it only renders sides that you can actually see.
                                        // Especially necessary in the case of transparent items like glass panes.
                                        .filter(d -> d == Direction.NORTH || d == Direction.SOUTH || map.computeIfAbsent(
                                                Pair.of(finalFrame, Pair.of(finalX + d.getStepX(), finalY - d.getStepY())),
                                                p -> finalX == 0 && d == Direction.WEST || finalX == width - 1 && d == Direction.EAST || finalY == 0 && d == Direction.UP || finalY == height - 1 && d == Direction.DOWN ||
                                                        sprite.isTransparent(finalFrame, Mth.clamp(p.getRight().getLeft(), 0, width - 1), Mth.clamp(p.getRight().getRight(), 0, height - 1))))
                                        .collect(Collectors.toMap(d -> d, d -> face)),
                                null, true));
                    }
            }

            cbi.setReturnValue(list);
        }
    }

    @Shadow protected abstract List<BlockElement> createSideElements(TextureAtlasSprite sprite, String key, int layer);

    @Shadow protected abstract List<BlockElement> processFrames(int layer, String key, TextureAtlasSprite sprite);
}
