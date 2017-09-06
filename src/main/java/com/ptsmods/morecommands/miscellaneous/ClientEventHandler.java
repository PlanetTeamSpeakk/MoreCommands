package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;
import java.util.HashMap;

import com.mojang.text2speech.Narrator;
import com.ptsmods.morecommands.commands.ptime.Commandptime;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler extends EventHandler {

	private static boolean isGamePaused = false;
	private static boolean isGamePaused1 = false;

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
		if (Commandptime.time != -1)
			try {
				Minecraft.getMinecraft().world.setWorldTime(Commandptime.time);
			} catch (NullPointerException e) {} // NullPointerExceptions can occur when logging out from a server, these will crash your game.
		isGamePaused1  = Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu;
		if (isGamePaused1 && !isGamePaused) {
			isGamePaused = true;
			if (MinecraftForge.EVENT_BUS.post(new GamePaused()))
				Minecraft.getMinecraft().displayGuiScreen(null);
		} else if (!isGamePaused1 && isGamePaused) { // net.minecraft.client.Minecraft.isGamePaused() can only return true on singleplayer.
			isGamePaused = false;
			if (MinecraftForge.EVENT_BUS.post(new GameResumed()))
				Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (Reference.lastPosition != null) Reference.blocksPerSecond = Reference.calculateBlocksPerSecond();
		try {
			Reference.lastPosition = Minecraft.getMinecraft().player.getPositionVector();
		} catch (NullPointerException e) {} // occurs when the player hasn't logged in a world or server.
		if (Commandptime.time != -1 && !Commandptime.fixed && event.phase == Phase.END)
			try {
				Commandptime.time += 1;
			} catch (NullPointerException e) {} // They can also occur when logging out from a singleplayer world, this will crash your game as well.
		if (Reference.isSittingOnChair && !(Reference.player == null) && !Reference.player.isRiding()) Reference.dismountStairs(); // killing the arrow as soon as the player isn't riding it anymore.
		if (event.phase == Phase.END && Reference.easterEggLoopEnabled) {
			Reference.clientTicksPassed += 1;
			if (Reference.clientTicksPassed/20.0F == 9.5F) {
				Reference.playEasterEgg(); // it's so relaxing :3
				Reference.clientTicksPassed = 0;
			}
		}
		if (event.phase == Phase.END) {
			Reference.clientTicksPassed2 += 1;
			if (Reference.clientTicksPassed2/20 == 1) {
				Reference.updatesPerSecond = Reference.updated;
				Reference.updated = 0;
				Reference.clientTicksPassed2 = 0;
			}
		}
	}

	@SubscribeEvent
	public void onChatMessageSent(ClientChatEvent event) {
		// BEGIN NARRATE COMMAND
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
			} else if (event.getOriginalMessage().toLowerCase().equals("bee movie")) {
				try {
					Reference.addTextToNarratorMessage(Reference.getHTML("https://raw.githubusercontent.com/PlanetTeamSpeakk/MoreCommands/master/bee_movie_script.txt")); // I know it's an old meme, but idgaf.
				} catch (IOException e) {}
				Reference.sendMessage(Minecraft.getMinecraft().player, ":O you added the bee movie script! Do note that once you say 'done' there's not way back unless you force close Minecraft with task manager and restart it.");
			} else Reference.addTextToNarratorMessage(event.getOriginalMessage());
			// END NARRATE COMMAND
			// BEGIN EASTER EGG
		} else if (event.getOriginalMessage().toLowerCase().equals("/easteregg")) {
			if (!Reference.easterEggLoopEnabled) {
				Reference.playEasterEgg();
				Reference.easterEggLoopEnabled = true;
				event.setCanceled(true);
			} else {
				Reference.easterEggLoopEnabled = false;
				Reference.clientTicksPassed = 0;
				PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
				EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(Minecraft.getMinecraft().player.getName());
				packetBuffer.writeString("players");
				packetBuffer.writeString("morecommands:easteregg");
				player.connection.sendPacket(new SPacketCustomPayload("MC|StopSound", packetBuffer)); // letting the client send a packet to itself :3
				event.setCanceled(true);
			}
			// END EASTER EGG
			// BEGIN ALIASES
		} else if (Reference.doesAliasExist(event.getOriginalMessage().split(" ")[0].substring(1))) {
			String command = Reference.getCommandFromAlias(event.getOriginalMessage().split(" ")[0].substring(1));
			if (ClientCommandHandler.instance.getCommands().keySet().contains(command.split(" ")[0])) ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, command);
			else Reference.sendChatMessage(Minecraft.getMinecraft().player, "/" + command);
			event.setCanceled(true);
		}
		// END ALIASES
		if (event.isCanceled()) Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getOriginalMessage());
	}

	@SubscribeEvent
	public void onChatMessageReceived(ClientChatReceivedEvent event) {
		if (event.getMessage().getUnformattedText().trim().equals("")) event.setCanceled(true);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		HashMap<String, KeyBinding> keyBindings = Reference.getKeyBindings();
		for (String keyBinding : keyBindings.keySet())
			if (keyBindings.get(keyBinding).isPressed()) Reference.keyBindPressed(keyBinding);
	}

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.EXPERIENCE && Reference.isInfoOverlayEnabled()) {
			new InfoOverlay();
			Reference.updated += 1;
		}
	}

	@SubscribeEvent
	public void onGamePaused(GamePaused event) {
		Minecraft.getMinecraft().displayGuiScreen(new com.ptsmods.morecommands.miscellaneous.GuiIngameMenu());
	}

	//	@SubscribeEvent
	//	public void onGamePaused(GamePaused event) {
	//		event.setCanceled(true);
	//	}
	//  You can probably guess what these 2 events together will do
	//	@SubscribeEvent
	//	public void onGameResumed(GameResumed event) {
	//		event.setCanceled(true);
	//	}
	//  If you can't: https://youtu.be/L1d530LibpE
}
