package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InfoOverlay extends Gui {

	private Minecraft mc;

	public InfoOverlay() {
		mc = Minecraft.getMinecraft();
		zLevel = Float.MAX_VALUE;
		try {
			Reference.loadInfoOverlayConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		drawStrings(parseInfoOverlayConfig(Reference.getInfoOverlayConfig()).toArray(new String[0]));
	}

	private void drawString(String string, int row) {
		int defaultHeight = 2;
		int defaultWidth = 2;
		if (Reference.setVariables.containsKey("defaultHeight") && Reference.isInteger(Reference.setVariables.get("defaultHeight"))) defaultHeight = Integer.parseInt(Reference.setVariables.get("defaultHeight"));
		if (Reference.setVariables.containsKey("defaultWidth") && Reference.isInteger(Reference.setVariables.get("defaultWidth"))) defaultWidth = Integer.parseInt(Reference.setVariables.get("defaultWidth"));
		drawString(mc.fontRenderer, string, defaultWidth, row*10 + defaultHeight, Integer.parseInt("FFFFFF", 16));
	}

	private void drawStrings(String... strings) {
		for (int x = 0; x < strings.length; x++)
			drawString(strings[x], x);
	}

	private TextFormatting getRandomColor() {
		if (Reference.Random.randInt(101) == 0)
			Reference.lastColor = Reference.getRandomColor("WHITE");
		return Reference.lastColor;
	}

	private List<String> parseInfoOverlayConfig(List<String> config) {
		Minecraft mc = Minecraft.getMinecraft();
		List<String> output = new ArrayList<>();
		Reference.setVariables = new HashMap<>();
		try {
			for (String line : config)
				if (line.startsWith("var ")) {
					if (line.split(" ").length == 4) Reference.setVariables.put(line.split(" ")[1], line.split(" ")[3]);
				} else {
					line = line.replaceAll("\\{playerName\\}", mc.player.getName())
							.replaceAll("\\{x\\}", String.format("%f", mc.player.getPositionVector().x))
							.replaceAll("\\{y\\}", String.format("%f", mc.player.getPositionVector().y))
							.replaceAll("\\{z\\}", String.format("%f", mc.player.getPositionVector().z))
							.replaceAll("\\{chunkX\\}", "" + mc.player.chunkCoordX)
							.replaceAll("\\{chunkY\\}", "" + mc.player.chunkCoordY)
							.replaceAll("\\{chunkZ\\}", "" + mc.player.chunkCoordZ)
							.replaceAll("\\{yaw\\}", "" + MathHelper.wrapDegrees(mc.player.rotationYaw))
							.replaceAll("\\{pitch\\}", "" + MathHelper.wrapDegrees(mc.player.rotationPitch))
							.replaceAll("\\{biome\\}", mc.world.getBiome(mc.player.getPosition()).getBiomeName())
							.replaceAll("\\{difficulty\\}", mc.world.getWorldInfo().getDifficulty().name())
							.replaceAll("\\{blocksPerSec\\}", Reference.formatBlocksPerSecond())
							.replaceAll("\\{toggleKey\\}", Reference.getKeyBindingByName("toggleOverlay").getDisplayName())
							.replaceAll("\\{configFile\\}", new File("config/MoreCommands/infoOverlay.txt").getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")) // replacing 1 backslash with 2 so backslashes actually show
							.replaceAll("\\{facing\\}", Reference.getLookDirectionFromLookVec(mc.player.getLookVec()))
							.replaceAll("\\{time\\}", Reference.parseTime(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(mc.player.dimension).getWorldTime() % 24000L, false))
							.replaceAll("\\{time12\\}", Reference.parseTime(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(mc.player.dimension).getWorldTime() % 24000L, true))
							.replaceAll("\\{UUID\\}", mc.player.getUniqueID().toString())
							.replaceAll("\\{holding\\}", Reference.getLocalizedName(mc.player.getHeldItemMainhand().getItem()))
							.replaceAll("\\{rainbow\\}", "" + getRandomColor())
							.replaceAll("\\{easterEgg\\}", "Do /easteregg") // cheater, don't look at this!
							.replaceAll("\\{xp\\}", "" + mc.player.experienceTotal)
							.replaceAll("\\{xpLevel\\}", "" + mc.player.experienceLevel)
							.replaceAll("\\{gamemode\\}", FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(mc.player.getName()).interactionManager.getGameType().getName())
							.replaceAll("\\{fps\\}", "" + mc.getDebugFPS())
							.replaceAll("\\{blockLight\\}", "" + Reference.getBlockLight(mc.world, mc.player.getPosition()))
							.replaceAll("\\{skyLight\\}", "" + Reference.getSkyLight(mc.world, mc.player.getPosition()))
							.replaceAll("\\{lookingAtX\\}", "" + (mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver == null ? "null" : mc.objectMouseOver.getBlockPos().getX()))
							.replaceAll("\\{lookingAtY\\}", "" + (mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver == null ? "null" : mc.objectMouseOver.getBlockPos().getY()))
							.replaceAll("\\{lookingAtZ\\}", "" + (mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver == null ? "null" : mc.objectMouseOver.getBlockPos().getZ()))
							.replaceAll("\\{lookingAt\\}", "" + (mc.objectMouseOver.getBlockPos() == null || mc.objectMouseOver == null ? "null" : Reference.getLocalizedName(mc.world.getBlockState(Reference.centerBlockPos(mc.objectMouseOver.getBlockPos())).getBlock())))
							.replaceAll("\\{isSingleplayer\\}", "" + FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer())
							.replaceAll("\\{language\\}", FMLCommonHandler.instance().getCurrentLanguage())
							.replaceAll("\\{lookingVecX\\}", "" + mc.player.getLookVec().x)
							.replaceAll("\\{lookingVecY\\}", "" + mc.player.getLookVec().y)
							.replaceAll("\\{lookingVecZ\\}", "" + mc.player.getLookVec().z)
							.replaceAll("\\{lookingAtSide\\}", "" + (mc.objectMouseOver.getBlockPos() == null ? "null" : mc.objectMouseOver.sideHit.getName()))
							.replaceAll("\\{updatesPerSecond\\}", "" + Reference.updatesPerSecond)
							.replaceAll("\\{entities\\}", "" + mc.world.getLoadedEntityList().size());
					if (line.equals("") || !line.split("//")[0].equals("")) output.add(line.split("//")[0]); // handling comments in the config, this should be exactly the same as how normal, non-multiline, Java comments work.
				}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return output;
	}

}
