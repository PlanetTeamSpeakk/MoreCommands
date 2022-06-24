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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class InfoHud extends DrawableHelper {

    public static final InfoHud instance = new InfoHud();
    private static final File file = MoreCommandsArch.getConfigDirectory().resolve("infoHud.txt").toFile();
    private static final List<StackTraceElement> printedExceptions = new ArrayList<>();
    private final Map<String, String> variables = new HashMap<>();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<String> lines = new ArrayList<>();
    private HitResult result;
    private int xOffset = 2;
    private int yOffset = 2;
    private long lastRead = 0;

    public void render(MatrixStack matrices, float tickDelta) {
        result = MoreCommands.getRayTraceTarget(client.player, 160f, false, true);
        if (System.currentTimeMillis() - lastRead >= 500) {
            try {
                loadLines();
                lastRead = System.currentTimeMillis();
            } catch (IOException e) {
                MoreCommands.LOG.catching(e);
                setupDefaultLines();
            }
        }
        if (variables.containsKey("xOffset") && MoreCommands.isInteger(variables.get("xOffset"))) xOffset = Integer.parseInt(variables.get("xOffset"));
        if (variables.containsKey("yOffset") && MoreCommands.isInteger(variables.get("yOffset"))) yOffset = Integer.parseInt(variables.get("yOffset"));
        int row = 0;
        for (String s : parseLines())
            drawString(matrices, s, row++);
    }

    private void drawString(MatrixStack matrices, String s, int row) {
        client.textRenderer.drawWithShadow(matrices, LiteralTextBuilder.builder(s).build(), xOffset, row * 10 + yOffset, 0xFFFFFF);
    }

    private void setupDefaultLines() {
        lines.clear();
        lines.add("// Have a look at https://github.com/PlanetTeamSpeakk/MoreCommands to see what variables you can use here.");
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
        variables.clear();
        List<String> output = new ArrayList<>();
        for (String line : lines)
            if (line.startsWith("var ")) {
                if (line.split(" ").length == 4) variables.put(line.split(" ")[1], line.split(" ")[3]);
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

}
