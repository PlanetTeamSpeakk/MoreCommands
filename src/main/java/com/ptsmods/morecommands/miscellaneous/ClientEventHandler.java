package com.ptsmods.morecommands.miscellaneous;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mojang.text2speech.Narrator;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.ptime.Commandptime;
import com.ptsmods.morecommands.miscellaneous.Reference.Random;
import com.ptsmods.morecommands.net.NetHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler extends EventHandler {

	public static Map<String, Long>	eventsfired				= new HashMap();
	public static int				eventspersec			= 0;
	private static volatile int		eventspassed			= 0;
	private static volatile long	start					= System.currentTimeMillis();
	public static boolean			rainbowHighlight		= false;							// Number 4 already, see fireball.
	public static float				redAmountHighlight		= 0;
	public static float				greenAmountHighlight	= 0;
	public static float				blueAmountHighlight		= 0;
	public static boolean			discoBiomes				= false;							// 5 already, omfg.
	private static boolean			isGamePaused			= false;
	private static boolean			isGamePaused1			= false;
	private ExecutorService			executor				= Executors.newCachedThreadPool();
	private boolean					checker					= false;

	@SubscribeEvent
	public void onPlayerAttackEntity(AttackEntityEvent event) throws CommandException {
		checker = Reference.isSingleplayer() ? !checker : false;
		if (!checker) Reference.powerToolCommand(EnumHand.MAIN_HAND, event);
	}

	@SubscribeEvent
	public void onPlayerLeftClickAir(PlayerInteractEvent.LeftClickEmpty event) throws CommandException {
		Reference.powerToolCommand(event.getHand(), event);
	}

	@SubscribeEvent
	public void onPlayerRightClickAir(PlayerInteractEvent.RightClickEmpty event) throws CommandException {
		Reference.powerToolCommand(EnumHand.MAIN_HAND /*
														 * event#getHand() seems to always return OFF_HAND even though it is the main
														 * hand.
														 */, event);
	}

	private RayTraceResult customHighlight() {
		if (!MoreCommands.modInstalledServerSide) return null;
		RayTraceResult result = Reference.rayTrace(Math.max(Minecraft.getMinecraft().player.getCapability(ReachProvider.reachCap, null).get() - 0.5F, 1F));
		if (result == null) return null;
		return result.typeOfHit == RayTraceResult.Type.BLOCK && Minecraft.getMinecraft().playerController.getCurrentGameType() != GameType.ADVENTURE && Minecraft.getMinecraft().playerController.getCurrentGameType() != GameType.SPECTATOR ? result : null;
	}

	@SubscribeEvent
	public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
		if (MoreCommands.modInstalledServerSide) {
			event.setCanceled(true); // With extended reach Minecraft cannot raytrace entities or some bs like that
										// so I have to make sure the bounding box does not show when hovering over an
										// entity.
			RayTraceResult result;
			if ((result = customHighlight()) != null) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				World theWorld = player.getEntityWorld();
				// Thanks to RenderGlobal#drawSelectionBox for this one
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.glLineWidth(2F);
				GlStateManager.disableTexture2D();
				GlStateManager.depthMask(false);
				BlockPos blockpos = result.getBlockPos();
				IBlockState iblockstate = theWorld.isBlockLoaded(blockpos) ? theWorld.getBlockState(blockpos) : null;
				if (iblockstate != null && theWorld.getWorldBorder().contains(blockpos)) {
					double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
					double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
					double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
					float redAmountHighlight = rainbowHighlight ? ClientEventHandler.redAmountHighlight : 0;
					float greenAmountHighlight = rainbowHighlight ? ClientEventHandler.greenAmountHighlight : 0;
					float blueAmountHighlight = rainbowHighlight ? ClientEventHandler.blueAmountHighlight : 0;
					float alphaAmountHighlight = 0.4F;
					RenderGlobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(theWorld, blockpos).grow(0.0020000000949949026D).offset(-d0, -d1, -d2), redAmountHighlight, greenAmountHighlight, blueAmountHighlight, alphaAmountHighlight);
				}
				GlStateManager.glLineWidth(1F);
				GlStateManager.depthMask(true);
				GlStateManager.enableTexture2D();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (Commandptime.time != -1 && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().world != null) Minecraft.getMinecraft().world.setWorldTime(Commandptime.time);
		boolean flag = false;
		isGamePaused1 = Minecraft.getMinecraft().currentScreen instanceof GuiIngameMenu;
		if (isGamePaused1 && !isGamePaused) {
			isGamePaused = true;
			if (flag = MinecraftForge.EVENT_BUS.post(new GamePaused(Minecraft.getMinecraft().currentScreen))) Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().displayGuiScreen(null);
				Reference.lastScreen = Minecraft.getMinecraft().currentScreen;
			});
		} else if (!isGamePaused1 && isGamePaused) { // net.minecraft.client.Minecraft.isGamePaused() can only return true on
														// singleplayer.
			isGamePaused = false;
			if (flag = MinecraftForge.EVENT_BUS.post(new GameResumed(Minecraft.getMinecraft().currentScreen))) Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().displayGuiScreen(Reference.lastScreen);
				Reference.lastScreen = Minecraft.getMinecraft().currentScreen;
			});
		}
		if (!flag) Reference.lastScreen = Minecraft.getMinecraft().currentScreen;
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (Reference.lastPosition != null) Reference.blocksPerSecond = Reference.calculateBlocksPerSecond();
		try {
			Reference.lastPosition = Minecraft.getMinecraft().player.getPositionVector();
		} catch (NullPointerException e) {} // occurs when the player hasn't logged in a world or server.
		if (Commandptime.time != -1 && !Commandptime.fixed && event.phase == Phase.END) try {
			Commandptime.time += 1;
		} catch (NullPointerException e) {} // They can also occur when logging out from a singleplayer world, this will
											// crash your game as well.
		if (event.phase == Phase.END && Reference.easterEggLoopEnabled) {
			Reference.clientTicksPassed += 1;
			if (Reference.clientTicksPassed / 20.0F == 9.5F) {
				Reference.playEasterEgg(); // it's so relaxing :3
				Reference.clientTicksPassed = 0;
			}
		}
		if (event.phase == Phase.END) {
			Reference.clientTicksPassed2 += 1;
			if (Reference.clientTicksPassed2 / 20 == 1) {
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
				Minecraft.getMinecraft().getSoundHandler().stop("morecommands:easteregg", SoundCategory.PLAYERS);
				event.setCanceled(true);
			}
			// END EASTER EGG
			// BEGIN RAINBOW HIGHLIGHT (EASTEREGG #2)
		} else if (event.getOriginalMessage().toLowerCase().equals("/rainbowhighlight")) {
			rainbowHighlight = !rainbowHighlight;
			event.setCanceled(true);
			// END RAINBOW HIGHLIGHT
			// BEGIN RAINBOW WATER
		} else if (event.getOriginalMessage().toLowerCase().equals("/discobiomes")) {
			discoBiomes = !discoBiomes;
			Minecraft.getMinecraft().renderGlobal.loadRenderers(); // This has the same effect as pressing F3+A which reloads chunks.
			event.setCanceled(true);
			// END RAINBOW WATER
			// BEGIN ALIASES
		} else if (Reference.doesAliasExist(event.getOriginalMessage().split(" ")[0].substring(1)) && event.getOriginalMessage().startsWith("/")) {
			// Minecraft#currentScreen should *always* be an instance of GuiChat when this
			// event is ran as any messages in GuiChat are first sent, which fires this
			// event, before it's closed, but you may never be too careful.
			if (Minecraft.getMinecraft().currentScreen != null) Minecraft.getMinecraft().currentScreen.sendChatMessage("/" + Reference.getCommandFromAlias(event.getOriginalMessage().split(" ")[0].substring(1)));
			else if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, "/" + Reference.getCommandFromAlias(event.getOriginalMessage().split(" ")[0].substring(1))) == 0) Reference.sendChatMessage("/" + Reference.getCommandFromAlias(event.getOriginalMessage().split(" ")[0].substring(1)));
			event.setCanceled(true);
		}
		// END ALIASES
		if (event.getOriginalMessage().startsWith("/") && ClientCommandHandler.instance.getCommands().keySet().contains(event.getOriginalMessage().substring(1).split(" ")[0]) && !event.isCanceled()) event.setCanceled(MinecraftForge.EVENT_BUS.post(new ClientCommandEvent(ClientCommandHandler.instance.getCommands().get(event.getOriginalMessage().substring(1).split(" ")[0]), Minecraft.getMinecraft().player, Reference.removeArg(event.getOriginalMessage().substring(1).split(" "), 0))));
		if (event.isCanceled()) Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(event.getOriginalMessage());
	}

	@SubscribeEvent
	public void onChatMessageReceived(ClientChatReceivedEvent event) {
		if (event.getMessage().getUnformattedText().trim().equals("")) event.setCanceled(true);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		Map<String, KeyBinding> keyBindings = Reference.getKeyBindings();
		for (String keyBinding : keyBindings.keySet())
			if (keyBindings.get(keyBinding).isPressed()) keyBindings.get(keyBinding).run();
	}

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.EXPERIENCE && Reference.isInfoOverlayEnabled()) {
			InfoOverlay.INSTANCE.draw();
			Reference.updated += 1;
		}
	}

	@SubscribeEvent
	public void onGamePaused(GamePaused event) {
		if (!Reference.getMinecraftVersion().startsWith("1.11")) Minecraft.getMinecraft().displayGuiScreen(new com.ptsmods.morecommands.miscellaneous.GuiIngameMenu());
	}

	// @SubscribeEvent
	// public void onGamePaused(GamePaused event) {
	// event.setCanceled(true);
	// }
	// You can probably guess what these 2 events together will do
	// @SubscribeEvent
	// public void onGameResumed(GameResumed event) {
	// event.setCanceled(true);
	// }
	// If you can't: https://youtu.be/L1d530LibpE

	@SubscribeEvent
	public void onClientCommand(ClientCommandEvent event) {
		if (runOnTicker(event.getCommand())) {
			event.setCanceled(true);
			Ticker.INSTANCE.addRunnable(Type.CLIENT, extraArgs -> {
				try {
					event.getCommand().execute(FMLCommonHandler.instance().getMinecraftServerInstance(), event.getSender(), event.getParameters());
				} catch (WrongUsageException e1) {
					Reference.sendMessage(event.getSender(), TextFormatting.RED + new TextComponentTranslation("commands.generic.usage", new Object[] {new TextComponentTranslation(e1.getMessage(), e1.getErrorObjects())}).getFormattedText());
					return;
				} catch (CommandException e2) {
					Reference.sendMessage(event.getSender(), TextFormatting.RED + new TextComponentTranslation(e2.getMessage(), e2.getErrorObjects()).getFormattedText());
				} catch (Throwable e3) {
					e3.printStackTrace();
					Reference.sendMessage(event.getSender(), TextFormatting.RED + "An unknown error occured while attempting to perform this command.");
				}
			});
		}
	}

	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		event.getManager().channel().pipeline().addBefore("packet_handler", null, new NetHandler(Side.CLIENT)); // So packets can be consumed.
	}

	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (MoreCommands.modInstalledServerSide) {
			if (!event.getCapabilities().containsKey(new ResourceLocation(Reference.MOD_ID, "reach")) && event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(Reference.MOD_ID, "reach"), new ReachProvider());
			if (!event.getCapabilities().containsKey(new ResourceLocation(Reference.MOD_ID, "fakeplayer")) && event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(Reference.MOD_ID, "fakeplayer"), new FPProvider());
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		event.getEntityPlayer().getCapability(ReachProvider.reachCap, null).set(FMLCommonHandler.instance().getSide() == Side.SERVER || Reference.isSingleplayer() ? (EntityPlayerMP) event.getEntityPlayer() : null, event.getOriginal().getCapability(ReachProvider.reachCap, null).get());
	}

	@SubscribeEvent
	public void onBiomeColor(BiomeEvent.BiomeColor event) {
		// Sets grass, water and foliage colors to rainbows!
		if (discoBiomes) event.setNewColor(getRandomColor());
	}

	private int getRandomColor() {
		return Reference.shiftRGB(Random.randInt(255), Random.randInt(255), Random.randInt(255), 255);
	}

	@SubscribeEvent
	public void onEvent(Event event) {
		eventsfired.put(event.getClass().getName(), eventsfired.getOrDefault(event.getClass().getName(), 0L) + 1);
		eventspassed += 1;
		if (System.currentTimeMillis() - start >= 1000) {
			eventspersec = eventspassed;
			eventspassed = 0;
			start = System.currentTimeMillis();
		}
	}

	private boolean runOnTicker(ICommand command) {
		if (command instanceof CommandBase && ((CommandBase) command).runOnTicker()) return true;
		else if (!(command instanceof CommandBase)) return true;
		else return false;
	}

}
