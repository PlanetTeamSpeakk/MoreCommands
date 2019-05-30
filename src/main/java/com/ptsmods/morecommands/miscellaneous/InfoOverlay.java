package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
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
		int xOffset = 2;
		int yOffset = 2;
		if (Reference.setVariables.containsKey("xOffset") && Reference.isInteger(Reference.setVariables.get("xOffset"))) xOffset = Integer.parseInt(Reference.setVariables.get("xOffset"));
		if (Reference.setVariables.containsKey("yOffset") && Reference.isInteger(Reference.setVariables.get("yOffset"))) yOffset = Integer.parseInt(Reference.setVariables.get("yOffset"));
		drawString(mc.fontRenderer, string, xOffset, row * 10 + yOffset, Integer.parseInt("FFFFFF", 16));
	}

	private void drawStrings(String... strings) {
		for (int x = 0; x < strings.length; x++)
			drawString(strings[x], x);
	}

	private static TextFormatting getRandomColor() {
		if (Reference.Random.randInt(101) == 0) Reference.lastColor = Reference.getRandomColor("WHITE", "BLACK", "LIGHT_GRAY");
		return Reference.lastColor;
	}

	private static List<String> parseInfoOverlayConfig(List<String> config) {
		Minecraft.getMinecraft();
		List<String> output = new ArrayList<>();
		Reference.setVariables = new HashMap<>();
		try {
			for (String line : config)
				if (line.startsWith("var ")) {
					if (line.split(" ").length == 4) Reference.setVariables.put(line.split(" ")[1], line.split(" ")[3]);
				} else {
					//@formatter:off
					line = line.replaceAll("\\{playerName\\}", translate("playerName"))
							.replaceAll("\\{x\\}", translate("x"))
							.replaceAll("\\{y\\}", translate("y"))
							.replaceAll("\\{z\\}", translate("z"))
							.replaceAll("\\{chunkX\\}", translate("chunkX"))
							.replaceAll("\\{chunkY\\}", translate("chunkY"))
							.replaceAll("\\{chunkZ\\}", translate("chunkZ"))
							.replaceAll("\\{yaw\\}", translate("yaw"))
							.replaceAll("\\{pitch\\}", translate("pitch"))
							.replaceAll("\\{biome\\}", translate("biome"))
							.replaceAll("\\{difficulty\\}", translate("difficulty"))
							.replaceAll("\\{blocksPerSec\\}", translate("blocksPerSec"))
							.replaceAll("\\{toggleKey\\}", translate("toggleKey"))
							.replaceAll("\\{configFile\\}", translate("configFile")) // replacing 1 backslash with 2 so backslashes actually show
							.replaceAll("\\{facing\\}", translate("facing"))
							.replaceAll("\\{time\\}", translate("time"))
							.replaceAll("\\{time12\\}", translate("time12"))
							.replaceAll("\\{UUID\\}", translate("UUID"))
							.replaceAll("\\{holding\\}", translate("holding"))
							.replaceAll("\\{rainbow\\}", translate("rainbow"))
							.replaceAll("\\{xp\\}", translate("xp"))
							.replaceAll("\\{xpLevel\\}", translate("xpLevel"))
							.replaceAll("\\{gamemode\\}", translate("gamemode"))
							.replaceAll("\\{fps\\}", translate("fps"))
							.replaceAll("\\{blockLight\\}", translate("blockLight"))
							.replaceAll("\\{skyLight\\}", translate("skyLight"))
							.replaceAll("\\{lookingAtX\\}", translate("lookingAtX"))
							.replaceAll("\\{lookingAtY\\}", translate("lookingAtY"))
							.replaceAll("\\{lookingAtZ\\}", translate("lookingAtZ"))
							.replaceAll("\\{lookingAt\\}", translate("lookingAt"))
							.replaceAll("\\{isSingleplayer\\}", translate("isSingleplayer"))
							.replaceAll("\\{language\\}", translate("language"))
							.replaceAll("\\{lookingVecX\\}", translate("lookingVecX"))
							.replaceAll("\\{lookingVecY\\}", translate("lookingVecY"))
							.replaceAll("\\{lookingVecZ\\}", translate("lookingVecZ"))
							.replaceAll("\\{lookingAtSide\\}", translate("lookingAtSide"))
							.replaceAll("\\{updatesPerSecond\\}", translate("updatesPerSecond"))
							.replaceAll("\\{entities\\}", translate("entities"))
							.replaceAll("\\{eventsPerSecond\\}", translate("eventsPerSecond"));//@formatter:on
					if (line.equals("") || !line.split("//")[0].equals("")) output.add(line.split("//")[0]); // handling comments in the config, this should be exactly the same as how
																											 // normal, non-multiline Java comments work.
				}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String translate(String key) {
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult result = Reference.rayTrace(160); // There seems to be some kind of maximum and I think it is 160.
		String output = "{" + key + "}";
		try {//@formatter:off
			switch (key) {
			case "playerName": {output = mc.player.getName(); break;}
			case "x": {output = String.format("%f", mc.player.getPositionVector().x); break;}
			case "y": {output = String.format("%f", mc.player.getPositionVector().y); break;}
			case "z": {output = String.format("%f", mc.player.getPositionVector().z); break;}
			case "chunkX": {output = "" + mc.player.chunkCoordX; break;}
			case "chunkY": {output = "" + mc.player.chunkCoordY; break;}
			case "chunkZ": {output = "" + mc.player.chunkCoordZ; break;}
			case "yaw": {output = "" + MathHelper.wrapDegrees(mc.player.rotationYaw); break;}
			case "pitch": {output = "" + MathHelper.wrapDegrees(mc.player.rotationPitch); break;}
			case "biome": {output = mc.world.getBiome(mc.player.getPosition()).getBiomeName(); break;}
			case "difficulty": {output = mc.world.getWorldInfo().getDifficulty().name(); break;}
			case "blocksPerSec": {output = Reference.formatBlocksPerSecond(); break;}
			case "toggleKey": {output = Reference.getKeyBindingByName(I18n.format("key.morecommands.toggleOverlay")).getDisplayName(); break;}
			case "configFile": {output = new File("config/MoreCommands/infoOverlay.txt").getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"); break;}
			case "facing": {output = Reference.getLookDirectionFromLookVec(mc.player.getLookVec()); break;}
			case "time": {output = Reference.parseTime(Minecraft.getMinecraft().world.getWorldTime() % 24000L, false); break;}
			case "time12": {output = Reference.parseTime(Minecraft.getMinecraft().world.getWorldTime() % 24000L, true); break;}
			case "UUID": {output = mc.player.getUniqueID().toString(); break;}
			case "holding": {output = Reference.getLocalizedName(mc.player.getHeldItemMainhand().getItem()); break;}
			case "rainbow": {output = "" + getRandomColor(); break;}
			case "xp": {output = "" + mc.player.experienceTotal; break;}
			case "xpLevel": {output =  "" + mc.player.experienceLevel; break;}
			case "gamemode": {output = mc.playerController.getCurrentGameType().name(); break;}
			case "fps": {output = "" + Minecraft.getDebugFPS(); break;}
			case "blockLight": {output = "" + Reference.getBlockLight(mc.world, mc.player.getPosition()); break;}
			case "skyLight": {output = "" + Reference.getSkyLight(mc.world, mc.player.getPosition()); break;}
			case "lookingAtX": {output = "" + (result.typeOfHit == RayTraceResult.Type.BLOCK || result.typeOfHit == RayTraceResult.Type.MISS ? result.getBlockPos().getX() : result.entityHit.getPosition().getX()); break;}
			case "lookingAtY": {output = "" + (result.typeOfHit == RayTraceResult.Type.BLOCK || result.typeOfHit == RayTraceResult.Type.MISS ? result.getBlockPos().getY() : result.entityHit.getPosition().getY()); break;}
			case "lookingAtZ": {output = "" + (result.typeOfHit == RayTraceResult.Type.BLOCK || result.typeOfHit == RayTraceResult.Type.MISS ? result.getBlockPos().getZ() : result.entityHit.getPosition().getZ()); break;}
			case "lookingAt": {output = result.typeOfHit == RayTraceResult.Type.BLOCK || result.typeOfHit == RayTraceResult.Type.MISS ? Reference.firstNonNull(Minecraft.getMinecraft().world.getBlockState(result.getBlockPos()).getBlock().getPickBlock(Minecraft.getMinecraft().world.getBlockState(result.getBlockPos()), result, Minecraft.getMinecraft().world, result.getBlockPos(), Minecraft.getMinecraft().player), ItemStack.EMPTY).getDisplayName() : result.entityHit.getName(); break;}
			case "isSingleplayer": {output = "" + Reference.isSingleplayer(); break;}
			case "language": {output = FMLCommonHandler.instance().getCurrentLanguage(); break;}
			case "lookingVecX": {output = "" + result.hitVec.x; break;}
			case "lookingVecY": {output = "" + result.hitVec.y; break;}
			case "lookingVecZ": {output = "" + result.hitVec.z; break;}
			case "lookingAtSide": {output = result.typeOfHit == RayTraceResult.Type.BLOCK ? result.sideHit.getName() : "none"; break;}
			case "updatesPerSecond": {output = "" + Reference.updatesPerSecond; break;}
			case "entities": {output = "" + mc.world.getLoadedEntityList().size(); break;}
			case "eventsPerSecond": {output = "" + ClientEventHandler.eventspersec; break;}
			default: break;
			}//@formatter:on
		} catch (Exception e) {
			e.printStackTrace();
			output = "ERROR";
		}
		return output;
	}

}
