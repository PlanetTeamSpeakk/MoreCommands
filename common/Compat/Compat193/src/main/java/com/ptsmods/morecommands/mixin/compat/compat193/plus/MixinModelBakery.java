package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.ptsmods.morecommands.api.addons.BlockModelAddon;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Shadow @Final private Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources;
    @Shadow @Final private Map<ResourceLocation, BlockModel> modelResources;
    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Unique private ItemTransforms defDisplay;
    @Unique private BlockModel missingModel;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadBlockModel(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/block/model/BlockModel;"), method = "loadModel")
    private BlockModel loadModel_loadModelFromJson(ModelBakery modelLoader, ResourceLocation id) throws IOException {
        if (ClientOption.getBoolean("overrideAirModels") && "minecraft".equals(id.getNamespace()) && id.getPath().contains("item/") && id.getPath().contains("air"))
            switch (id.getPath()) {
                case "item/air" -> {
                    return loadBlockModel(new ResourceLocation("morecommands:item/air"));
                }
                case "item/mcsynthetic_cave_air" -> {
                    return loadBlockModel(new ResourceLocation("morecommands:item/cave_air"));
                }
                case "item/mcsynthetic_void_air" -> {
                    return loadBlockModel(new ResourceLocation("morecommands:item/void_air"));
                }
                default -> {}
            }

        try {
            return loadBlockModel(id);
        } catch (IOException e) {
            if (id.getPath().startsWith("item/"))
                try {
                    JsonObject blockstate = blockStateResources.get(new ResourceLocation(id.getNamespace(), id.getPath()
                                    .replace("mcsynthetic_", "").replace("item/", "blockstates/") + ".json"))
                            .stream()
                            .findFirst()
                            .map(ModelBakery.LoadedJson::data)
                            .filter(JsonElement::isJsonObject)
                            .map(JsonElement::getAsJsonObject)
                            .orElse(null);
                    if (blockstate == null || !blockstate.has("variants") && !blockstate.has("multiplart"))
                        return (BlockModel) unbakedCache.get(ModelBakery.MISSING_MODEL_LOCATION);

                    String modelId = blockstate.has("variants") ? blockstate.getAsJsonObject("variants")
                            .entrySet()
                            .stream()
                            .map(Map.Entry::getValue)
                            .map(JsonElement::getAsJsonObject)
                            .findFirst()
                            .map(m -> m.get("model").getAsString())
                            .orElseThrow(() -> new IOException("Couldn't find block model for block " + id.toString().replace("item/", "blockstates/"))) :
                            // FIXME it's not always an array, sometimes it's an object.
                            blockstate.getAsJsonArray("multipart").asList().stream()
                                    .map(JsonElement::getAsJsonObject)
                                    .map(m -> m.getAsJsonArray("apply"))
                                    .findFirst()
                                    .map(l -> l.asList().stream()
                                            .map(JsonElement::getAsJsonObject)
                                            .toList())
                                    .filter(l -> l.size() > 0 && l.stream()
                                            .anyMatch(m -> m.has("model")))
                                    .map(l -> l.stream()
                                            .findFirst()
                                            .map(m -> m.get("model").getAsString())
                                            .orElseThrow(() -> new AssertionError("This shouldn't happen.")))
                                    .orElseThrow(() -> new IOException("Couldn't find model for block " + id.toString().replace("item/", "blockstates/")));

                    if (defDisplay == null) {
                        defDisplay = new ItemTransforms(
                                new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 0.15625F, 0), new Vector3f(0.375F, 0.375F, 0.375F)),
                                new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 0.15625F, 0), new Vector3f(0.375F, 0.375F, 0.375F)),
                                new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4F, 0.4F, 0.4F)),
                                new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4F, 0.4F, 0.4F)),
                                new ItemTransform(new Vector3f(1F, 1F, 1F), new Vector3f(0.0625F, 0.0625F, 0.0625F), new Vector3f(1F, 1F, 1F)),
                                new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625F, 0.625F, 0.62F)),
                                new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0.1875F, 0), new Vector3f(0.25F, 0.25F, 0.25F)),
                                new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5F, 0.5F, 0.5F))
                        );
                        missingModel = loadBlockModel(ModelBakery.MISSING_MODEL_LOCATION);
                        ((BlockModelAddon) missingModel).setTransforms(defDisplay);
                    }

                    BlockModel model = modelResources.get(new ResourceLocation(id.getNamespace(), "models/block/" +
                            new ResourceLocation(modelId).getPath().substring(6) + ".json"));

                    BlockModel parent = model;
                    boolean cross = false;
                    boolean empty = true;
                    Map<String, Either<Material, String>> textureMap = null;

                    while (parent != null) {
                        if (Optional.ofNullable(((BlockModelAddon) model).getParentLocation()).map(pid -> pid.equals(new ResourceLocation("minecraft:block/cross")) ||
                                pid.equals(new ResourceLocation("minecraft:block/tinted_cross"))).orElse(false)) cross = true;
                        if (!model.getElements().isEmpty()) empty = false;
                        if (textureMap == null && ((BlockModelAddon) model).getTextureMap() != null) textureMap = ((BlockModelAddon) model).getTextureMap();
                        if (parent == ((BlockModelAddon) model).getParent()) break;
                        parent = ((BlockModelAddon) model).getParent();
                    }

                    // Thanks to Draydenspace_FS#0001 on Discord for this json model.
                    // It is what's responsible for the water block having colour.
                    if (empty) return textureMap != null && textureMap.containsKey("particle") && textureMap.get("particle").left().isPresent() ? BlockModel.fromString("{\"parent\":\"block/block\",\"textures\":{\"tex\":\"" + textureMap.get("particle").left().get().texture() + "\",\"particle\":\"#tex\"},\"elements\":[{\"from\":[0,0,0],\"to\":[16,16,16],\"faces\":{\"down\":{\"texture\":\"#tex\",\"cullface\":\"down\",\"tintindex\":0},\"up\":{\"texture\":\"#tex\",\"cullface\":\"up\",\"tintindex\":0},\"north\":{\"texture\":\"#tex\",\"cullface\":\"north\",\"tintindex\":0},\"south\":{\"texture\":\"#tex\",\"cullface\":\"south\",\"tintindex\":0},\"west\":{\"texture\":\"#tex\",\"cullface\":\"west\",\"tintindex\":0},\"east\":{\"texture\":\"#tex\",\"cullface\":\"east\",\"tintindex\":0}}}]}") : missingModel;

                    if (cross) {
                        if (textureMap != null && textureMap.containsKey("cross") && textureMap.get("cross").left().isPresent()) return BlockModel.fromString("{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"" + textureMap.get("cross").left().get().texture() + "\"}}");
                    } else {
                        parent = model;
                        while (parent != null) {
                            ((BlockModelAddon) parent).setTransforms(defDisplay);
                            if (parent == ((BlockModelAddon) model).getParent()) break;
                            parent = ((BlockModelAddon) model).getParent();
                        }
                    }

                    return model;
                } catch (Exception e0) {
                    // IOExceptions get caught by the parent method, so it'll just get printed to the console as a warning.
                    throw e0 instanceof IOException ? (IOException) e0 : new IOException(e0);
                }
            else throw e;
        }
    }

    @Shadow protected abstract BlockModel loadBlockModel(ResourceLocation identifier) throws IOException;
}
