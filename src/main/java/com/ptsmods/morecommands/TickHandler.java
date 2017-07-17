package com.ptsmods.morecommands;

import com.ptsmods.morecommands.commands.ptime.Commandptime;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TickHandler {
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (Commandptime.time != -1) {
			try {
				Minecraft.getMinecraft().world.setWorldTime(Commandptime.time);
			} catch (NullPointerException e) { // NullPointerExceptions can occur when logging out from a server, these will crash your game.
				e.printStackTrace();
			}
		}

	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (Commandptime.time != -1 && !Commandptime.fixed) {
			try {
				Commandptime.time += 1;
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
}
