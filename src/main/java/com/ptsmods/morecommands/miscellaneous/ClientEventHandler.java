package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.commands.ptime.Commandptime;

import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
	
	@SubscribeEvent
	public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) throws CommandException {
		Reference.powerToolCommand(event.getEntityPlayer(), event.getHand(), event, false);
	}
	
	@SubscribeEvent
	public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) throws CommandException {
		Reference.powerToolCommand(event.getEntityPlayer(), event.getHand(), event, false);
	}
	
	@SubscribeEvent
	public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) throws CommandException {
		Reference.powerToolCommand(event.getEntityPlayer(), event.getHand(), event, false);
		Reference.superPickaxeBreak(event.getEntityPlayer(), event.getHand());
	}
	
	@SubscribeEvent
	public void onPlayerLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) throws CommandException {
		Reference.powerToolCommand(event.getEntityPlayer(), event.getHand(), event, true);
	}
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (Commandptime.time != -1) {
			try {
				Minecraft.getMinecraft().world.setWorldTime(Commandptime.time);
			} catch (NullPointerException e) {} // NullPointerExceptions can occur when logging out from a server, these will crash your game.
		}

	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (Commandptime.time != -1 && !Commandptime.fixed && event.phase == Phase.END) {
			try {
				Commandptime.time += 1;
			} catch (NullPointerException e) {} // They can also occur when logging out from a singleplayer world, this will crash your game as well.
		}
		if (Reference.isSittingOnChair && !(Reference.player == null) && !Reference.player.isRiding()) Reference.dismountStairs(); // killing the arrow as soon as the player isn't riding it anymore.
	}
	
}
