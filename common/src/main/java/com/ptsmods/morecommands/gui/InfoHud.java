package com.ptsmods.morecommands.gui;

import com.google.common.base.MoreObjects;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.PatternSyntaxException;

public class InfoHud extends DrawableHelper {
    public static final InfoHud INSTANCE = new InfoHud();
    private static final File file = MoreCommandsArch.getConfigDirectory().resolve("infoHud.txt").toFile();
    private static final List<StackTraceElement> printedExceptions = new ArrayList<>();
    private static final Map<String, Variable<?>> variables = new HashMap<>();
    private static final Map<String, Object> variableValues = new HashMap<>();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<String> lines = new ArrayList<>();
    private static HitResult result;
    private static long lastRead = 0;
    private static int width = 0, height = 0;

    static {
        registerVariable(new IntVariable("xOffset", 2, (matrixStack, val) -> matrixStack.translate(val, 0, 0)));
        registerVariable(new IntVariable("yOffset", 2, (matrixStack, val) -> matrixStack.translate(0, val, 0)));
        registerVariable(new DoubleVariable("scale", 1.0, (matrixStack, val) -> matrixStack.scale(val.floatValue(), val.floatValue(), val.floatValue())));

        AtomicInteger backgroundOpacity = new AtomicInteger();
        registerVariable(new IntVariable("backgroundOpacity", 0, ((matrixStack, val) -> backgroundOpacity.set(val))).clamped(0, 100));
        registerVariable(new ColourVariable("backgroundColour", Color.BLACK, (matrixStack, val) -> fill(matrixStack, -2, -2, width + 2, height,
                new Color(val.getRed(), val.getGreen(), val.getBlue(), (int) (backgroundOpacity.get() / 100f * 255)).getRGB())));
    }

    private static void registerVariable(Variable<?> variable) {
        variables.put(variable.getName(), variable);
    }

    public void render(MatrixStack matrices, float tickDelta) {
        matrices.push();

        result = MoreCommands.getRayTraceTarget(client.player, 160f, false, true);
        if (System.currentTimeMillis() - lastRead >= 500) try {
            if (file.lastModified() > lastRead) loadLines();
            lastRead = System.currentTimeMillis();
        } catch (IOException e) {
            MoreCommands.LOG.catching(e);
            setupDefaultLines();
        }

        List<String> parsedLines = parseLines();
        width = parsedLines.stream()
                .mapToInt(client.textRenderer::getWidth)
                .max()
                .orElse(0);
        height = parsedLines.size() * 10;

        variables.forEach((name, var) -> {
            if (variableValues.containsKey(name))
                var.apply(matrices, var.upcast(variableValues.get(name)));
            else var.applyDefault(matrices);
        });

        int row = 0;
        for (String line : parsedLines)
            drawString(matrices, line, row++);

        matrices.pop();
    }

    private void drawString(MatrixStack matrices, String s, int row) {
        client.textRenderer.drawWithShadow(matrices, LiteralTextBuilder.literal(s), 0, row * 10, 0xFFFFFF);
    }

    private void setupDefaultLines() {
        lines.clear();
        lines.add("// Have a look at https://morecommands.ptsmods.com/misc/info-hud to see what variables you can use here.");
        lines.add("{DF}Player: {SF}{playerName}");
        lines.add("{DF}FPS: {SF}{fps}");
        lines.add("{DF}X: {SF}{x}");
        lines.add("{DF}Y: {SF}{y}");
        lines.add("{DF}Z: {SF}{z}");
        lines.add("{DF}Pitch: {SF}{pitch}");
        lines.add("{DF}Yaw: {SF}{yaw}");
        lines.add("{DF}Facing: {SF}{facing}");
        lines.add("{DF}Biome: {SF}{biome}");
        lines.add("{DF}Speed: {SF}{avgSpeed}");
        try {
            saveLines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadLines() throws IOException {
        if (!file.exists()) {
            setupDefaultLines();
        } else {
            lines.clear();
            lines.addAll(Files.readAllLines(file.toPath()));
        }
        if (lines.isEmpty()) setupDefaultLines();
    }

    private void saveLines() throws FileNotFoundException {
        ;
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            for (String line : lines)
                writer.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> parseLines() {
        variableValues.clear();
        List<String> output = new ArrayList<>();
        for (String line : lines)
            if (line.startsWith("var ")) {
                String[] lineParts = line.split(" ");
                if (lineParts.length != 4) continue;

                String name = lineParts[1];
                if (!variables.containsKey(name)) continue;

                try {
                    variableValues.put(name, variables.get(name).fromString(lineParts[3]));
                } catch (Exception ignored) {}
            } else {
                StringBuilder s = new StringBuilder();
                int index = -1;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '{') index = i;
                    else if (line.charAt(i) == '}' && index >= 0) {
                        try {
                            s.append(translate(line.substring(index + 1, i)));
                            index = -1;
                        } catch (PatternSyntaxException e) {
                            s.replace(0, s.length(), "Error parsing line, please make sure all regex characters are escaped.");
                            break;
                        }
                    } else if (index == -1) s.append(line.charAt(i));
                }
                line = s.toString();
                if (line.equals("") || !line.split("//")[0].equals("")) output.add(line.split("//")[0]); // handling comments in the config, this should be exactly the same as how
                // normal, non-multiline Java comments work.
            }
        return output;
    }

    private String translate(String key) {
        String output = "{" + key + "}";
        BlockHitResult bResult = result instanceof BlockHitResult ? (BlockHitResult) result : null;
        EntityHitResult eResult = result instanceof EntityHitResult ? (EntityHitResult) result : null;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.world != null)
            try {
                switch (key) {
                    case "DF": {
                        output = MoreCommands.DF.toString();
                        break;
                    }
                    case "SF": {
                        output = MoreCommands.SF.toString();
                        break;
                    }
                    case "playerName": {
                        output = IMoreCommands.get().textToString(mc.player.getName(), null, true);
                        break;
                    }
                    case "x": {
                        output = MoreCommands.formatDouble(mc.player.getPos().x);
                        break;
                    }
                    case "y": {
                        output = MoreCommands.formatDouble(mc.player.getPos().y);
                        break;
                    }
                    case "z": {
                        output = MoreCommands.formatDouble(mc.player.getPos().z);
                        break;
                    }
                    case "chunkX": {
                        output = "" + (mc.player.getBlockPos().getX() >> 4);
                        break;
                    }
                    case "chunkY": {
                        output = "" + (mc.player.getBlockPos().getY() >> 4);
                        break;
                    }
                    case "chunkZ": {
                        output = "" + (mc.player.getBlockPos().getZ() >> 4);
                        break;
                    }
                    case "yaw": {
                        output = "" + MathHelper.wrapDegrees(((MixinEntityAccessor) mc.player).getYaw_());
                        break;
                    }
                    case "pitch": {
                        output = "" + MathHelper.wrapDegrees(((MixinEntityAccessor) mc.player).getPitch_());
                        break;
                    }
                    case "biome": {
                        output = Objects.requireNonNull(Compat.get().getRegistry(mc.world.getRegistryManager(), Registry.BIOME_KEY)
                                .getId(Compat.get().getBiome(mc.world, mc.player.getBlockPos()))).toString();
                        break;
                    }
                    case "difficulty": {
                        output = mc.world.getLevelProperties().getDifficulty().name();
                        break;
                    }
                    case "blocksPerSec": {
                        output = MoreCommands.formatDouble(MoreCommandsClient.getSpeed()) + " blocks/sec";
                        break;
                    }
                    case "avgSpeed": {
                        output = MoreCommands.formatDouble(MoreCommandsClient.getAvgSpeed()) + " blocks/sec";
                        break;
                    }
                    case "toggleKey": {
                        output = IMoreCommands.get().textToString(MoreCommandsClient.toggleInfoHudBinding.getBoundKeyLocalizedText(), null, true);
                        break;
                    }
                    case "configFile": {
                        output = file.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
                        break;
                    }
                    case "facing": {
                        output = MoreCommands.getLookDirection(MathHelper.wrapDegrees(((MixinEntityAccessor) mc.player).getYaw_()), ((MixinEntityAccessor) mc.player).getPitch_());
                        break;
                    }
                    case "time": {
                        output = MoreCommands.parseTime(mc.world.getTime() % 24000L, false);
                        break;
                    }
                    case "time12": {
                        output = MoreCommands.parseTime(mc.world.getTime() % 24000L, true);
                        break;
                    }
                    case "UUID": {
                        output = mc.player.getUuidAsString();
                        break;
                    }
                    case "holding": {
                        output = I18n.translate(mc.player.getMainHandStack().getItem().getTranslationKey());
                        break;
                    }
                    case "xp": {
                        output = "" + mc.player.totalExperience;
                        break;
                    }
                    case "xpLevel": {
                        output = "" + mc.player.experienceLevel;
                        break;
                    }
                    case "gamemode": {
                        output = Objects.requireNonNull(mc.interactionManager).getCurrentGameMode().name();
                        break;
                    }
                    case "fps": {
                        output = "" + mc.fpsDebugString.split(" ", 2)[0];
                        break;
                    }
                    case "blockLight": {
                        output = "" + mc.world.getChunkManager().getLightingProvider().get(LightType.BLOCK).getLightLevel(mc.player.getBlockPos());
                        break;
                    }
                    case "skyLight": {
                        output = "" + mc.world.getChunkManager().getLightingProvider().get(LightType.SKY).getLightLevel(mc.player.getBlockPos());
                        break;
                    }
                    case "lookingAtX": {
                        output = "" + (bResult != null ? bResult.getBlockPos().getX() : Objects.requireNonNull(eResult).getPos().x);
                        break;
                    }
                    case "lookingAtY": {
                        output = "" + (bResult != null ? bResult.getBlockPos().getY() : Objects.requireNonNull(eResult).getPos().y);
                        break;
                    }
                    case "lookingAtZ": {
                        output = "" + (bResult != null ? bResult.getBlockPos().getZ() : Objects.requireNonNull(eResult).getPos().z);
                        break;
                    }
                    case "lookingAt": {
                        output = IMoreCommands.get().textToString(bResult != null ? MoreObjects.firstNonNull(mc.world.getBlockState(bResult.getBlockPos()).getBlock().getPickStack(mc.world,
                                bResult.getBlockPos(), mc.world.getBlockState(bResult.getBlockPos())), ItemStack.EMPTY).getName() : Objects.requireNonNull(eResult).getEntity().getName(), null, true);
                        break;
                    }
                    case "language": {
                        output = mc.options.language;
                        break;
                    }
                    case "lookingVecX": {
                        output = "" + result.getPos().x;
                        break;
                    }
                    case "lookingVecY": {
                        output = "" + result.getPos().y;
                        break;
                    }
                    case "lookingVecZ": {
                        output = "" + result.getPos().z;
                        break;
                    }
                    case "lookingAtSide": {
                        output = result.getType() == HitResult.Type.BLOCK ? Objects.requireNonNull(bResult).getSide().getName() : "none";
                        break;
                    }
                    case "entities": {
                        output = "" + new ArrayList<Entity>((Collection<? extends Entity>) mc.world.getEntities()).size();
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                if (e.getStackTrace().length != 0) {

                    StackTraceElement element = e.getStackTrace()[0];
                    if (!printedExceptions.contains(element)) {
                        MoreCommands.LOG.catching(e);
                        printedExceptions.add(element);
                    }
                }
                output = "ERROR";
            }
        return output;
    }

    public interface Variable<T> {
        String getName();
        T getDefaultValue();
        T fromString(String val);
        void apply(MatrixStack matrixStack, Object value);
        void applyDefault(MatrixStack matrixStack);
        T upcast(Object value);
    }

    private abstract static class AbstractVariable<T> implements Variable<T> {
        protected final String name;
        protected final T defaultValue;
        private final BiConsumer<MatrixStack, T> applicator;

        public AbstractVariable(String name, T defaultValue, BiConsumer<MatrixStack, T> applicator) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.applicator = applicator;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public T getDefaultValue() {
            return defaultValue;
        }

        @Override
        public void apply(MatrixStack matrixStack, Object value) {
            applicator.accept(matrixStack, upcast(value));
        }

        @Override
        public void applyDefault(MatrixStack matrixStack) {
            applicator.accept(matrixStack, getDefaultValue());
        }
    }

    public static class IntVariable extends AbstractVariable<Integer> {
        private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

        public IntVariable(String name, Integer defaultValue, BiConsumer<MatrixStack, Integer> applicator) {
            super(name, defaultValue, applicator);
        }

        @Override
        public Integer fromString(String val) {
            return MathHelper.clamp(Integer.parseInt(val), min, max);
        }

        @Override
        public Integer upcast(Object value) {
            return (Integer) value;
        }

        public IntVariable clamped(int min, int max) {
            this.min = min;
            this.max = max;

            return this;
        }
    }

    public static class DoubleVariable extends AbstractVariable<Double> {
        public DoubleVariable(String name, Double defaultValue, BiConsumer<MatrixStack, Double> applicator) {
            super(name, defaultValue, applicator);
        }

        @Override
        public Double fromString(String val) {
            return Double.parseDouble(val);
        }

        @Override
        public Double upcast(Object value) {
            return (Double) value;
        }
    }

    public static class ColourVariable extends AbstractVariable<Color> {

        public ColourVariable(String name, Color defaultValue, BiConsumer<MatrixStack, Color> applicator) {
            super(name, defaultValue, applicator);
        }

        @Override
        public Color fromString(String val) {
            return new Color(Integer.parseInt(val.startsWith("#") ? val.substring(1) : val, 16));
        }

        @Override
        public Color upcast(Object value) {
            return (Color) value;
        }
    }
}
