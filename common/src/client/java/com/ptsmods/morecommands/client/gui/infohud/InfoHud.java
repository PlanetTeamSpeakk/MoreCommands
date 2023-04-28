package com.ptsmods.morecommands.client.gui.infohud;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.client.gui.infohud.variables.*;
import com.ptsmods.morecommands.client.mixin.accessor.MixinMinecraftClientAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.StreamSupport;

public class InfoHud extends GuiComponent {
    public static final InfoHud INSTANCE = new InfoHud();
    private static final File file = MoreCommandsArch.getConfigDirectory().resolve("infoHud.txt").toFile();
    private static final Pattern varPattern = Pattern.compile("var\\s(?<key>[A-Za-z]*?)\\s*?=\\s*(?<value>\".*\"|\\S*)");
    private static final List<StackTraceElement> printedExceptions = new ArrayList<>();
    private static final Map<String, Function<KeyContext, Object>> keys;
    private static final Map<String, Variable<?>> variables = new HashMap<>();
    private static final Map<String, Object> variableValues = new HashMap<>();
    private static final Minecraft client = Minecraft.getInstance();
    private static final List<String> lines = new ArrayList<>();
    private static List<Pair<Integer, String>> parsedLines = Collections.emptyList();
    private static HitResult result;
    private static long lastRead = -1;
    private static int width = 0, height = 0;
    private static int decimals = 2;

    static {
        registerVariable(new IntVariable("xOffset", 2, (matrixStack, val) -> matrixStack.translate(val, 0, 0)));
        registerVariable(new IntVariable("yOffset", 2, (matrixStack, val) -> matrixStack.translate(0, val, 0)));
        registerVariable(new DoubleVariable("scale", 0.75, (matrixStack, val) -> matrixStack.scale(val.floatValue(), val.floatValue(), val.floatValue())));
        registerVariable(new IntVariable("decimals", 2, (matrixStack, val) -> decimals = val).clamped(0, 7));

        AtomicInteger backgroundOpacity = new AtomicInteger();
        AtomicBoolean perLineBackground = new AtomicBoolean();
        registerVariable(new IntVariable("backgroundOpacity", 25, (matrixStack, val) -> backgroundOpacity.set(val)).clamped(0, 100));
        registerVariable(new BooleanVariable("perLineBackground", true, (matrixStack, val) -> perLineBackground.set(val)));
        registerVariable(new ColourVariable("backgroundColour", Color.BLACK, (matrixStack, val) -> {
            if (backgroundOpacity.get() == 0) return;
            int c = new Color(val.getRed(), val.getGreen(), val.getBlue(), (int) (backgroundOpacity.get() / 100f * 255)).getRGB();

            if (!perLineBackground.get()) {
                fill(matrixStack, -2, -2, width + 2, height, c);
                return;
            }

            for (int i = 0; i < parsedLines.size(); i++) {
                Pair<Integer, String> line = parsedLines.get(i);
                if (line.getFirst() == 0) continue;
                boolean topPadding = i == 0 || parsedLines.get(i - 1).getFirst() == 0; // First line or above line is empty.

                fill(matrixStack, -2, topPadding ? i * 10 - 2 : i * 10, line.getFirst() + 2, (i + 1) * 10, c);
                if (topPadding) continue;

                int prevWidth = parsedLines.get(i - 1).getFirst();
                if (prevWidth < line.getFirst())
                    fill(matrixStack, prevWidth + 2, i * 10 - 2, line.getFirst() + 2, i * 10, c); // Draw rest of top padding if above line is shorter than this one.
            }
        }));

        keys = registerKeys();
    }

    private static void registerVariable(Variable<?> variable) {
        variables.put(variable.getName(), variable);
    }

    private static Map<String, Function<KeyContext, Object>> registerKeys() {
        ImmutableMap.Builder<String, Function<KeyContext, Object>> keysBuilder = ImmutableMap.builder();
        keysBuilder.put("DF", ctx -> MoreCommands.DF);
        keysBuilder.put("SF", ctx -> MoreCommands.SF);
        keysBuilder.put("playerName", ctx -> IMoreCommands.get().textToString(ctx.getPlayer().getName(), null, true));
        keysBuilder.put("x", ctx -> MoreCommands.formatDouble(ctx.getPlayer().position().x(), decimals));
        keysBuilder.put("y", ctx -> MoreCommands.formatDouble(ctx.getPlayer().position().y(), decimals));
        keysBuilder.put("z", ctx -> MoreCommands.formatDouble(ctx.getPlayer().position().z(), decimals));
        keysBuilder.put("chunkX", ctx -> (Compat.get().blockPosition(ctx.getPlayer()).getX()) >> 4);
        keysBuilder.put("chunkY", ctx -> (Compat.get().blockPosition(ctx.getPlayer()).getY()) >> 4);
        keysBuilder.put("chunkZ", ctx -> (Compat.get().blockPosition(ctx.getPlayer()).getZ()) >> 4);
        keysBuilder.put("yaw", ctx -> MoreCommands.formatDouble(Mth.wrapDegrees(((MixinEntityAccessor) ctx.getPlayer()).getYRot_()), decimals));
        keysBuilder.put("pitch", ctx -> MoreCommands.formatDouble(Mth.wrapDegrees(((MixinEntityAccessor) ctx.getPlayer()).getXRot_()), decimals));
        keysBuilder.put("biome", ctx -> Objects.requireNonNull(Compat.get().getRegistry(ctx.getWorld().registryAccess(),"worldgen/biome")
                .getKey(Compat.get().getBiome(ctx.getWorld(), Compat.get().blockPosition(ctx.getPlayer())))));
        keysBuilder.put("difficulty", ctx -> ctx.getWorld().getLevelData().getDifficulty().name());
        keysBuilder.put("blocksPerSec", ctx -> MoreCommands.formatDouble(MoreCommandsClient.getSpeed(), decimals) + " blocks/sec");
        keysBuilder.put("speed", ctx -> MoreCommands.formatDouble(MoreCommandsClient.getSpeed(), decimals) + " blocks/sec");
        keysBuilder.put("avgSpeed", ctx -> MoreCommands.formatDouble(MoreCommandsClient.getAvgSpeed(), decimals) + " blocks/sec");
        keysBuilder.put("toggleKey", ctx -> IMoreCommands.get().textToString(MoreCommandsClient.TOGGLE_INFO_HUD_BINDING.getTranslatedKeyMessage(), null, true));
        keysBuilder.put("configFile", ctx -> file.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
        keysBuilder.put("facing", ctx -> MoreCommands.getLookDirection(Mth.wrapDegrees(((MixinEntityAccessor) ctx.getPlayer()).getYRot_()), ((MixinEntityAccessor) ctx.getPlayer()).getXRot_()));
        keysBuilder.put("time", ctx -> MoreCommands.parseTime(ctx.getWorld().getGameTime() % 24000L, false));
        keysBuilder.put("time12", ctx -> MoreCommands.parseTime(ctx.getWorld().getGameTime() % 24000L, true));
        keysBuilder.put("UUID", ctx -> ctx.getPlayer().getStringUUID());
        keysBuilder.put("holding", ctx -> I18n.get(ctx.getPlayer().getMainHandItem().getItem().getDescriptionId()));
        keysBuilder.put("xp", ctx -> ctx.getPlayer().totalExperience);
        keysBuilder.put("xpLevel", ctx -> ctx.getPlayer().experienceLevel);
        keysBuilder.put("gamemode", ctx -> ctx.getInteractionManager().getPlayerMode().name());
        keysBuilder.put("fps", ctx -> MixinMinecraftClientAccessor.getFps());
        keysBuilder.put("blockLight", ctx -> ctx.getWorld().getChunkSource().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(Compat.get().blockPosition(ctx.getPlayer())));
        keysBuilder.put("skyLight", ctx -> ctx.getWorld().getChunkSource().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(Compat.get().blockPosition(ctx.getPlayer())));
        keysBuilder.put("lookingAtX", ctx -> ctx.hit().map(bHit -> bHit.getBlockPos().getX(), eHit -> eHit.getLocation().x()));
        keysBuilder.put("lookingAtY", ctx -> ctx.hit().map(bHit -> bHit.getBlockPos().getY(), eHit -> eHit.getLocation().y()));
        keysBuilder.put("lookingAtZ", ctx -> ctx.hit().map(bHit -> bHit.getBlockPos().getZ(), eHit -> eHit.getLocation().z()));
        keysBuilder.put("lookingAt", ctx -> IMoreCommands.get().textToString(ctx.hit().map(bHit -> MoreObjects.firstNonNull(ctx.getWorld()
                .getBlockState(bHit.getBlockPos()).getBlock().getCloneItemStack(ctx.getWorld(), bHit.getBlockPos(), ctx.getWorld().getBlockState(bHit.getBlockPos())), ItemStack.EMPTY).getHoverName(),
                eHit -> eHit.getEntity().getName()), null, true));
        keysBuilder.put("lookingAtSide", ctx -> ctx.hit().map(bHit -> bHit.getDirection().getName(), eHit -> "none"));
        keysBuilder.put("language", ctx -> ctx.client().options.languageCode);
        keysBuilder.put("lookingVecX", ctx -> result.getLocation().x());
        keysBuilder.put("lookingVecY", ctx -> result.getLocation().y());
        keysBuilder.put("lookingVecZ", ctx -> result.getLocation().z());
        keysBuilder.put("entities", ctx -> StreamSupport.stream(ctx.getWorld().entitiesForRendering().spliterator(), false).count());

        return keysBuilder.build();
    }

    public void render(PoseStack matrices) {
        matrices.pushPose();

        result = MoreCommands.getRayTraceTarget(client.player, 160f, false, true);
        if (System.currentTimeMillis() - lastRead >= 500) try {
            if (file.lastModified() > lastRead) loadLines();
            lastRead = System.currentTimeMillis();
        } catch (IOException e) {
            MoreCommands.LOG.catching(e);
            setupDefaultLines();
        }

        parsedLines = parseLines();
        width = parsedLines.stream()
                .mapToInt(Pair::getFirst)
                .max()
                .orElse(0);
        height = parsedLines.size() * 10;

        variables.forEach((name, var) -> {
            if (variableValues.containsKey(name))
                var.apply(matrices, var.upcast(variableValues.get(name)));
            else var.applyDefault(matrices);
        });

        int row = 0;
        for (Pair<Integer, String> line : parsedLines)
            drawString(matrices, line.getSecond(), row++);

        matrices.popPose();
    }

    private void drawString(PoseStack matrices, String s, int row) {
        client.font.drawShadow(matrices, LiteralTextBuilder.literal(s), 0, row * 10, 0xFFFFFF);
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
            MoreCommandsClient.LOG.catching(e);
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
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8))) {
            for (String line : lines)
                writer.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Pair<Integer, String>> parseLines() {
        variableValues.clear();

        List<Pair<Integer, String>> output = new ArrayList<>();
        for (String line : lines) {
            Matcher varMatcher = varPattern.matcher(line);
            if (varMatcher.matches()) {
                String key = varMatcher.group("key");
                if (!variables.containsKey(key)) continue;

                try {
                    variableValues.put(key, variables.get(key).fromString(varMatcher.group("value")));
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

                String parsedLine = s.toString();
                line = Arrays.stream(s.toString().split("//")).findFirst().orElse(""); // Handling comments in the config, this should be exactly the same as how
                if (parsedLine.equals("") && !output.isEmpty() || !line.equals("")) output.add(Pair.of(client.font.width(line), line)); // normal, non-multiline Java comments work.
            }
        }
        return output;
    }

    private String translate(String key) {
        String output = "{" + key + "}";
        if (!keys.containsKey(key)) return output;

        BlockHitResult bHit = result instanceof BlockHitResult ? (BlockHitResult) result : null;
        EntityHitResult eHit = result instanceof EntityHitResult ? (EntityHitResult) result : null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || bHit == null && eHit == null) return output;

        KeyContext ctx = new KeyContext(mc, bHit != null ? Either.left(bHit) : Either.right(eHit));
        try {
            output = String.valueOf(keys.get(key).apply(ctx));
        } catch (Exception e) {
            if (e.getStackTrace().length == 0) {
                return "ERROR";
            }

            StackTraceElement element = e.getStackTrace()[0];
            if (!printedExceptions.contains(element)) {
                MoreCommands.LOG.error("An error occurred while translating key " + key, e);
                printedExceptions.add(element);
            }
            return "ERROR";
        }

        return output;
    }
}
