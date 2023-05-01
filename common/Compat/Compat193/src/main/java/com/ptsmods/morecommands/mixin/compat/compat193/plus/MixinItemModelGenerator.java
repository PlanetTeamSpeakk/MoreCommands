package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.google.common.collect.Lists;
import com.ptsmods.morecommands.api.addons.ItemModelGeneratorAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Seems like they split TextureAtlasSprite up into two classes in 1.19.3.
// Almost all of what we need of it was moved to a new class called SpriteContents.
@Mixin(ItemModelGenerator.class)
public abstract class MixinItemModelGenerator implements ItemModelGeneratorAddon {

    /**
     * @reason Removes the seam found in item models generated from 2D images by making every pixel its own individual cube.
     * @author PlanetTeamSpeak
     */
    @Inject(at = @At("HEAD"), method = "createSideElements", cancellable = true)
    private void createSideElements(SpriteContents sprite, String key, int layer, CallbackInfoReturnable<List<BlockElement>> cbi) {
        if (shouldIgnore()) {
            resetIgnore();
            return;
        }

        if (!ClientOption.getBoolean("fixItemSeams") || (!ClientOption.getBoolean("fixAnimItemSeams") &&
                sprite.getUniqueFrames().max().orElse(1) != 1)) return;

        // Basically just does what the Vanilla Tweaks resource pack does programmatically.
        int width = sprite.width();
        int height = sprite.height();
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
