package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InfoOverlay extends Gui {

	private Minecraft mc;
	
	public InfoOverlay() {
		this.mc = Minecraft.getMinecraft();
		int color = Integer.parseInt("FFAA00", 16);
		this.zLevel = Float.MAX_VALUE;
		try {
			if (Reference.Random.randInt(101) == 0) Reference.loadInfoOverlayConfig(); // This adds a refresh rate of about 3-5 seconds.
		} catch (IOException e) {
			e.printStackTrace();
		}
		drawStrings(parseInfoOverlayConfig(Reference.getInfoOverlayConfig()).toArray(new String[0]));
	}
	
	private void drawString(String string, int row) {
		drawString(this.mc.fontRenderer, string, 2, row*10 + 2, Integer.parseInt("FFFFFF", 16));
	}
	
	private void drawStrings(String... strings) {
		for (int x = 0; x < strings.length; x++) {
			drawString(strings[x], x);
		}
	}
	
    private static List<String> parseInfoOverlayConfig(List<String> config) {
    	Minecraft mc = Minecraft.getMinecraft();
    	List<String> output = new ArrayList<String>();
    	try {
	    	for (String line : config) {
	    		line = line.replaceAll("\\{playerName\\}", mc.player.getName());
	    		line = line.replaceAll("\\{x\\}", String.format("%f", mc.player.getPositionVector().x));
	    		line = line.replaceAll("\\{y\\}", String.format("%f", mc.player.getPositionVector().y));
	    		line = line.replaceAll("\\{z\\}", String.format("%f", mc.player.getPositionVector().z));
	    		line = line.replaceAll("\\{chunkX\\}", "" + mc.player.chunkCoordX);
	    		line = line.replaceAll("\\{chunkY\\}", "" + mc.player.chunkCoordY);
	    		line = line.replaceAll("\\{chunkZ\\}", "" + mc.player.chunkCoordZ);
	    		line = line.replaceAll("\\{yaw\\}", "" + MathHelper.wrapDegrees(mc.player.rotationYaw));
	    		line = line.replaceAll("\\{pitch\\}", "" + MathHelper.wrapDegrees(mc.player.rotationPitch));
	    		line = line.replaceAll("\\{biome\\}", mc.world.getBiome(mc.player.getPosition()).getBiomeName());
	    		line = line.replaceAll("\\{difficulty\\}", mc.world.getWorldInfo().getDifficulty().name());
	    		line = line.replaceAll("\\{blocksPerSec\\}", String.format("%f", Reference.blocksPerSecond));
	    		line = line.replaceAll("\\{toggleKey\\}", Reference.getKeyBindingByName("toggleOverlay").getDisplayName());
	    		line = line.replaceAll("\\{configFile\\}", new File("config/MoreCommands/infoOverlay.txt").getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
	    		line = line.replaceAll("\\{facing\\}", Reference.getLookDirectionFromLookVec(mc.player.getLookVec()));
	    		line = line.replaceAll("\\{time\\}", Reference.parseTime(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getWorldTime(), false));
	    		line = line.replaceAll("\\{easterEgg\\}", ":O, you found the easter egg!"); // cheater, don't look at this!
	    		if (!line.split("//")[0].equals("")) output.add(line.split("//")[0]);
	    	}
    	} catch (NullPointerException e) {}
    	return output;
    }
	
}
