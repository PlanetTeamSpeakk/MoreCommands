package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class GameRuleWildLimit implements IGameRule<Integer> {

	@Inject("wildLimit")
	public static GameRuleWildLimit instance = null;

	@Override
	public String getName() {
		return "wildLimit";
	}

	@Override
	public ValueType getType() {
		return ValueType.NUMERICAL_VALUE;
	}

	@Override
	public Integer getDefaultValue() {
		return 10000;
	}

	@Override
	public void onUpdate(WorldServer world, Integer oldValue, Integer newValue) {}

	@Override
	public void onCreateWorld(MinecraftServer server, World world) {}

	@Override
	public void initWorld(MinecraftServer server, World world, Integer value) {}

	@Override
	public void initServer(MinecraftServer server) {}

}
