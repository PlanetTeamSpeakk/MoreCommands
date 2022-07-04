package com.ptsmods.morecommands.mixin.client;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Either;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.mixin.client.accessor.MixinJsonUnbakedModelAccessor;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(ModelLoader.class)
public abstract class MixinModelLoader {
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private ResourceManager resourceManager;
    @Unique private final Gson gson = new Gson();
    @Unique private ModelTransformation defDisplay;
    @Unique private JsonUnbakedModel missingModel;

    @SuppressWarnings("unchecked")
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"), method = "loadModel")
    private JsonUnbakedModel loadModel_loadModelFromJson(ModelLoader modelLoader, Identifier id) throws IOException {
        if (ClientOptions.Tweaks.overrideAirModels.getValue() && "minecraft".equals(id.getNamespace()) && id.getPath().contains("item/") && id.getPath().contains("air")) {
            MoreCommandsClient.LOG.info(id);
            switch (id.getPath()) {
                case "item/air":
                    MoreCommandsClient.LOG.info("Loading air model");
                    return loadModelFromJson(new Identifier("morecommands:item/air"));
                case "item/mcsynthetic_cave_air":
                    return loadModelFromJson(new Identifier("morecommands:item/cave_air"));
                case "item/mcsynthetic_void_air":
                    return loadModelFromJson(new Identifier("morecommands:item/void_air"));
                default:
                    break;
            }
        }

        try {
            return loadModelFromJson(id);
        } catch (IOException e) {
            if (id.getPath().startsWith("item/"))
                try {
                    Map<String, Object> blockstate = gson.fromJson(new BufferedReader(new InputStreamReader(ClientCompat.get().getResourceStream(resourceManager,
                            new Identifier(id.getNamespace(), id.getPath().replace("mcsynthetic_", "").replace("item/", "blockstates/") + ".json")))), Map.class);
                    if (!blockstate.containsKey("variants") && !blockstate.containsKey("multipart")) return (JsonUnbakedModel) unbakedModels.get(ModelLoader.MISSING_ID);

                    String modelId = blockstate.containsKey("variants") ? (String) ((Map<String, Map<String, Object>>) blockstate.get("variants")).values().stream()
                            .findFirst()
                            .map(m -> m.get("model"))
                            .orElseThrow(() -> new IOException("Couldn't find block model for block " + id.toString().replace("item/", "blockstates/"))) :
                            ((List<Map<String, Object>>) blockstate.get("multipart")).stream()
                                    .map(m -> (List<Map<String, String>>) m.get("apply"))
                                    .findFirst()
                                    .filter(l -> l.size() > 0 && l.stream()
                                            .anyMatch(m -> m.containsKey("model")))
                                    .map(l -> l.stream()
                                            .findFirst()
                                            .map(m -> m.get("model"))
                                            .orElseThrow(() -> new AssertionError("This shouldn't happen.")))
                                    .orElseThrow(() -> new IOException("Couldn't find model for block " + id.toString().replace("item/", "blockstates/")));

                    Map<String, Object> modelRaw = gson.fromJson(new BufferedReader(new InputStreamReader(ClientCompat.get().getResourceStream(resourceManager,
                            new Identifier(id.getNamespace(), "models/block/" + new Identifier(modelId).getPath().substring(6) + ".json")))), Map.class);
                    if (defDisplay == null) {
                        defDisplay = new ModelTransformation(
                                new Transformation(new Vec3f(75, 45, 0), new Vec3f(0, 0.15625F, 0), new Vec3f(0.375F, 0.375F, 0.375F)),
                                new Transformation(new Vec3f(75, 45, 0), new Vec3f(0, 0.15625F, 0), new Vec3f(0.375F, 0.375F, 0.375F)),
                                new Transformation(new Vec3f(0, 225, 0), new Vec3f(0, 0, 0), new Vec3f(0.4F, 0.4F, 0.4F)),
                                new Transformation(new Vec3f(0, 45, 0), new Vec3f(0, 0, 0), new Vec3f(0.4F, 0.4F, 0.4F)),
                                new Transformation(new Vec3f(1F, 1F, 1F), new Vec3f(0.0625F, 0.0625F, 0.0625F), new Vec3f(1F, 1F, 1F)),
                                new Transformation(new Vec3f(30, 225, 0), new Vec3f(0, 0, 0), new Vec3f(0.625F, 0.625F, 0.62F)),
                                new Transformation(new Vec3f(0, 0, 0), new Vec3f(0, 0.1875F, 0), new Vec3f(0.25F, 0.25F, 0.25F)),
                                new Transformation(new Vec3f(0, 0, 0), new Vec3f(0, 0, 0), new Vec3f(0.5F, 0.5F, 0.5F))
                        );
                        missingModel = loadModelFromJson(ModelLoader.MISSING_ID);
                        ((MixinJsonUnbakedModelAccessor) missingModel).setTransformations(defDisplay);
                    }

                    JsonUnbakedModel model = JsonUnbakedModel.deserialize(gson.toJson(modelRaw));
                    model.getTextureDependencies(id0 -> ReflectionHelper.<ModelLoader>cast(this).getOrLoadModel(id0), new LinkedHashSet<>());
                    JsonUnbakedModel parent = model;
                    boolean cross = false;
                    boolean empty = true;
                    Map<String, Either<SpriteIdentifier, String>> textureMap = null;

                    while (parent != null) {
                        if (Optional.ofNullable(((MixinJsonUnbakedModelAccessor) model).getParentId()).map(pid -> pid.equals(new Identifier("minecraft:block/cross")) ||
                                pid.equals(new Identifier("minecraft:block/tinted_cross"))).orElse(false)) cross = true;
                        if (!model.getElements().isEmpty()) empty = false;
                        if (textureMap == null && ((MixinJsonUnbakedModelAccessor) model).getTextureMap() != null) textureMap = ((MixinJsonUnbakedModelAccessor) model).getTextureMap();
                        if (parent == ((MixinJsonUnbakedModelAccessor) model).getParent()) break;
                        parent = ((MixinJsonUnbakedModelAccessor) model).getParent();
                    }

                    // Thanks to Draydenspace_FS#0001 on Discord for this json model.
                    // It is what's responsible for the water block having colour.
                    if (empty) return textureMap != null && textureMap.containsKey("particle") && textureMap.get("particle").left().isPresent() ? JsonUnbakedModel.deserialize("{\"parent\":\"block/block\",\"textures\":{\"tex\":\"" + textureMap.get("particle").left().get().getTextureId() + "\",\"particle\":\"#tex\"},\"elements\":[{\"from\":[0,0,0],\"to\":[16,16,16],\"faces\":{\"down\":{\"texture\":\"#tex\",\"cullface\":\"down\",\"tintindex\":0},\"up\":{\"texture\":\"#tex\",\"cullface\":\"up\",\"tintindex\":0},\"north\":{\"texture\":\"#tex\",\"cullface\":\"north\",\"tintindex\":0},\"south\":{\"texture\":\"#tex\",\"cullface\":\"south\",\"tintindex\":0},\"west\":{\"texture\":\"#tex\",\"cullface\":\"west\",\"tintindex\":0},\"east\":{\"texture\":\"#tex\",\"cullface\":\"east\",\"tintindex\":0}}}]}") : missingModel;

                    if (cross) {
                        if (textureMap != null && textureMap.containsKey("cross") && textureMap.get("cross").left().isPresent()) return JsonUnbakedModel.deserialize("{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"" + textureMap.get("cross").left().get().getTextureId() + "\"}}");
                    } else {
                        parent = model;
                        while (parent != null) {
                            ((MixinJsonUnbakedModelAccessor) parent).setTransformations(defDisplay);
                            if (parent == ((MixinJsonUnbakedModelAccessor) model).getParent()) break;
                            parent = ((MixinJsonUnbakedModelAccessor) model).getParent();
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

    @Shadow protected abstract JsonUnbakedModel loadModelFromJson(Identifier identifier) throws IOException;
}
