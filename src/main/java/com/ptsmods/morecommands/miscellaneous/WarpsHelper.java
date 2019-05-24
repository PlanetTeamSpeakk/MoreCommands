package com.ptsmods.morecommands.miscellaneous;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.parser.ParserException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class WarpsHelper {

	private static final Map<String, Warps> allWarps;

	private WarpsHelper() {}

	static {
		File f = new File("config/MoreCommands/warps.json"); // F
		Map<String, Map<String, Map<String, Double>>> warps = null;
		try {
			warps = new Gson().fromJson(Reference.joinCustomChar("\n", Files.readAllLines(Paths.get(f.getAbsolutePath())).toArray(new String[0])), Map.class);
		} catch (ParserException | StringIndexOutOfBoundsException | IOException e) {
			f.deleteOnExit();
			Reference.print(LogType.ERROR, "An error occured while reading warps.json, it is most likely corrupt or old and has thus been deleted.");
			e.printStackTrace();
		}
		warps = warps == null ? new HashMap<>() : warps;
		Map<String, Warps> warps0 = new HashMap<>();
		for (Entry<String, Map<String, Map<String, Double>>> entry : warps.entrySet()) {
			List<Warp> warps1 = new ArrayList();
			for (Entry<String, Map<String, Double>> warp : entry.getValue().entrySet())
				warps1.add(new Warp(warp.getKey(), warp.getValue().get("dimension").intValue(), warp.getValue().get("x"), warp.getValue().get("y"), warp.getValue().get("z"), warp.getValue().get("yaw"), warp.getValue().get("pitch")));
			warps0.put(entry.getKey(), new Warps(warps1));
		}
		allWarps = warps0;
	}

	public static void addWarp(World world, String name, int dimension, Vec3d location, double yaw, double pitch) {
		if (world == null || location == null) return;
		if (!allWarps.containsKey("" + world.getSeed())) allWarps.put("" + world.getSeed(), new Warps(new ArrayList()));
		allWarps.get("" + world.getSeed()).add(new Warp(name, dimension, location.x, location.y, location.z, yaw, pitch));
		try {
			saveWarps();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeWarp(World world, String name) throws IOException {
		if (world != null && allWarps.containsKey("" + world.getSeed())) for (Warp warp : new Warps(allWarps.get("" + world.getSeed())))
			if (warp.name.equals(name)) {
				allWarps.get("" + world.getSeed()).remove(warp);
				saveWarps();
			}
	}

	public static boolean doesWarpExist(World world, String name) {
		if (world != null && allWarps.containsKey("" + world.getSeed())) for (Warp warp : allWarps.get("" + world.getSeed()))
			if (warp.name.equals(name)) return true;
		return false;
	}

	public static void saveWarps() throws IOException {
		Map<String, Map<String, Map<String, Double>>> warps = new HashMap();
		for (Entry<String, Warps> warps0 : allWarps.entrySet()) {
			Map<String, Map<String, Double>> warps1 = new HashMap();
			for (Warp warp : warps0.getValue()) {
				Map<String, Double> warpMap = new HashMap();
				warpMap.put("dimension", (double) warp.dimension);
				warpMap.put("x", warp.x);
				warpMap.put("y", warp.y);
				warpMap.put("z", warp.z);
				warpMap.put("yaw", (double) warp.yaw);
				warpMap.put("pitch", (double) warp.pitch);
				warps1.put(warp.name, warpMap);
				Reference.print(LogType.INFO, "WarpMap" + warpMap);
			}
			warps.put(warps0.getKey(), warps1);
			Reference.print(LogType.INFO, "Warps1" + warps1);
		}
		Reference.print(LogType.INFO, "Warps" + warps);
		PrintWriter writer = new PrintWriter(new FileWriter(new File("config/MoreCommands/warps.json")), true);
		new GsonBuilder().setPrettyPrinting().create().toJson(warps, writer);
		writer.flush();
		writer.close();
	}

	public static String getWarpsString(World world) {
		return TextFormatting.GOLD + Reference.joinCustomChar(TextFormatting.YELLOW + ", " + TextFormatting.GOLD, getWarpNames(world).toArray(new String[0]));
	}

	public static List<String> getWarpNames(World world) {
		List<String> names = new ArrayList();
		for (Warp warp : getWarps(world))
			names.add(warp.name);
		return names;
	}

	public static Warps getWarps(World world) {
		return allWarps.containsKey("" + world.getSeed()) ? new Warps(allWarps.get("" + world.getSeed())) : new Warps(new ArrayList());
	}

	public static Warp getWarpByName(World world, String name) {
		for (Warp warp : getWarps(world))
			if (warp.name.equals(name)) return warp;
		return null;
	}

	public static class Warps extends ArrayList<Warp> {
		private static final long serialVersionUID = 5348771342876848096L;

		private Warps(List<Warp> warps) {
			super(warps);
		}

	}

	public static class Warp {

		public final String	name;
		public final int	dimension;
		public final double	x, y, z;
		public final float	yaw, pitch;

		private Warp(String name, int dimension, double x, double y, double z, double yaw, double pitch) {
			this.name = name;
			this.dimension = dimension;
			this.x = x;
			this.y = y;
			this.z = z;
			this.yaw = (float) yaw;
			this.pitch = (float) pitch;
		}

	}

}
