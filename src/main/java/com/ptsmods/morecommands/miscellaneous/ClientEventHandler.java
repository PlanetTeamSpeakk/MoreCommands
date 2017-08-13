package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;

import org.lwjgl.opengl.Display;

import com.mojang.text2speech.Narrator;
import com.ptsmods.morecommands.commands.ptime.Commandptime;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
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
		//event.getTarget().onKillCommand(); // :3
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
	
	@SubscribeEvent
	public void onChatMessageSent(ClientChatEvent event) {
		if (Reference.narratorActive) {
			event.setCanceled(true);
			if (event.getOriginalMessage().toLowerCase().equals("cancel")) {
				Reference.sendMessage(Minecraft.getMinecraft().player, "The narrate command has been canceled.");
				Reference.resetNarratorMessage();
				Reference.narratorActive = false;
			} else if (event.getOriginalMessage().toLowerCase().equals("done")) {
				Narrator.getNarrator().say(Reference.getNarratorMessage());
				Reference.resetNarratorMessage();
				Reference.narratorActive = false;
			} else {
				if (event.getOriginalMessage().toLowerCase().equals("bee movie")) {
					try {
						Reference.addTextToNarratorMessage(Reference.getHTML("https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/bee_movie_script.txt")); // I know it's an old meme, but idgaf.
					} catch (IOException e) {}
					Reference.sendMessage(Minecraft.getMinecraft().player, ":O you added the bee movie script! Do note that once you say 'done' there's not way back unless you force close Minecraft with task manager and restart it.");
				} else Reference.addTextToNarratorMessage(event.getOriginalMessage());
			}
		}
	}
	
	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		if (!Reference.shouldRegisterCommands && !Reference.warnedUnregisteredCommands) {
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				public void run() {
					Reference.sendMessage(Minecraft.getMinecraft().player, TextFormatting.RED + "MoreComands commands were not registered, this is due to the necessary files being downloaded this session. "
							+ "Try restarting your game and if it still does not work after that, check your internet connection.");
					Reference.warnedUnregisteredCommands = true;
				}
			});
		}
	}
	
}
