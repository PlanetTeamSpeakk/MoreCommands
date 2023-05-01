package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.addons.BlockModelAddon;
import com.ptsmods.morecommands.api.addons.ItemModelGeneratorAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Function;

@Mixin(ItemModelGenerator.class)
public abstract class MixinItemModelGenerator implements ItemModelGeneratorAddon {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ItemModelGenerator;processFrames(ILjava/lang/String;Lnet/minecraft/client/renderer/texture/SpriteContents;)Ljava/util/List;"), method = "generateBlockModel")
    private List<BlockElement> create_addLayerElements(ItemModelGenerator instance, int layer, String key, SpriteContents sprite, Function<Material, TextureAtlasSprite> function, BlockModel blockModel) {
        ItemTransforms transformations = ((BlockModelAddon) blockModel).getRawTransforms();

        // They moved ItemTransforms.TransformType to ItemDisplayContext
        for (ItemDisplayContext ctx : ItemDisplayContext.values())
            if (transformations.hasTransform(ctx)) {
                // Items that have a display tag in their model (generally items that look bigger in the hand)
                // completely break using my method, and I have no idea why.
                //
                // I can only assume it has something to do with them not being of a normal size or some kind
                // of atlas issue, but if anyone has any insight as to why it happens and how to approach a
                // fix, please let me know.
                // P.s. the problem looks like this: https://github.com/PlanetTeamSpeakk/MoreCommands/issues/35
                ignoreNext();
                break;
            }

        return !shouldIgnore() && ClientOption.getBoolean("fixItemSeams") && (ClientOption.getBoolean("fixAnimItemSeams") || sprite.getUniqueFrames().max().orElse(1) == 1) ?
                createSideElements(sprite, key, layer) : processFrames(layer, key, sprite); // Skip front and back layer, those are created as subcomponents now too.
    }

    @Shadow protected abstract List<BlockElement> createSideElements(SpriteContents arg, String string, int i);

    @Shadow public abstract List<BlockElement> processFrames(int i, String string, SpriteContents arg);
}
