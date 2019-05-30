package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.miscellaneous.Reference.LogType;
import com.ptsmods.morecommands.miscellaneous.Ticker.TickRunnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GameRuleNoDownFall implements IGameRule<Boolean> {

	@Override
	public String getName() {
		return "noDownFall";
	}

	@Override
	public ValueType getType() {
		return ValueType.BOOLEAN_VALUE;
	}

	@Override
	public Boolean getDefaultValue() {
		return false;
	}

	@Override
	public void onUpdate(WorldServer world, Boolean oldValue, Boolean newValue) {}

	@Override
	public void onCreateWorld(MinecraftServer server, World world) {}

	@Override
	public void initWorld(MinecraftServer server, World world, Boolean value) {
		TickRunnable runnable = extraArgs -> {
			World world0 = FMLCommonHandler.instance().getMinecraftServerInstance() == null ? (World) extraArgs[1] : FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(world.provider.getDimension());
			if (!world0.isRemote && world0.getWorldInfo().isRaining() && getValue(world0)) {
				world0.getWorldInfo().setRaining(false);
				Reference.print(LogType.INFO, "It was starting to rain, but because the gamerule 'noDownFall' has been set to true, this has been prevented.");
			}
		};
		Ticker.INSTANCE.addRunnable(TickEvent.Type.WORLD, runnable.setRemoveWhenRan(false));
	}

	@Override
	public void initServer(MinecraftServer server) {}

}
