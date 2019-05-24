package com.ptsmods.morecommands.miscellaneous;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;
import com.ptsmods.morecommands.commands.god.Commandgod;
import com.ptsmods.morecommands.net.NetHandler;
import com.ptsmods.morecommands.net.ServerCapabilitiesUpdatePacket;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ServerEventHandler extends EventHandler {

	public static Map<String, Long>	eventsfired		= new HashMap();
	public static int				eventspersec	= 0;
	private static volatile int		eventspassed	= 0;
	private static volatile long	start			= System.currentTimeMillis();

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (CommandfixTime.time != -1) for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
			world.setWorldTime(CommandfixTime.time);
	}

	@SubscribeEvent
	public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) throws CommandException {
		if (event.getSide() == Side.SERVER && event.getHand() == EnumHand.MAIN_HAND) Reference.powerToolServerCommand(event.getEntityPlayer(), event.getHand(), event);
		// Too buggy as of right now, this will mess up your entire game.
		// Reference.print(LogType.INFO, Reference.arrows);
		// if (Reference.arrows.get(event.getEntityPlayer().getUniqueID().toString()) !=
		// null && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof
		// BlockStairs) {
		// // event.getEntityPlayer().dismountRidingEntity();
		// Reference.arrows.get(event.getEntityPlayer().getUniqueID().toString()).onKillCommand();
		// Reference.arrows.remove(event.getEntityPlayer().getUniqueID().toString());
		// Reference.print(LogType.INFO, "Dismounted in eventhandler");
		// } else Reference.sitOnStairs(event, event.getEntityPlayer(), event.getPos());
	}

	@SubscribeEvent
	public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) throws CommandException {
		if (event.getSide() == Side.SERVER && event.getHand() == EnumHand.MAIN_HAND) Reference.powerToolServerCommand(event.getEntityPlayer(), event.getHand(), event);
	}

	@SubscribeEvent
	public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) throws CommandException {
		if (event.getSide() == Side.SERVER) {
			Reference.powerToolServerCommand(event.getEntityPlayer(), event.getHand(), event);
			if (event.getHand() == EnumHand.MAIN_HAND) Reference.superPickaxeBreak(event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		if (event instanceof ClientCommandEvent) return;
		CommandBase command = event.getCommand() instanceof CommandBase ? (CommandBase) event.getCommand() : null;
		if (command != null && command.singleplayerOnly() && !FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
			Reference.sendMessage(event.getSender(), "This command is currently only for singleplayer.");
			event.setCanceled(true);
			// START COOLDOWN SYSTEM
		} else if (command != null && command.hasCooldown()) if (Reference.cooldowns.containsKey(command.getName()) && Reference.cooldowns.get(command.getName()).containsKey(event.getSender()) && new Date().getTime() / 1000 - Reference.cooldowns.get(command.getName()).get(event.getSender()) <= command.getCooldownSeconds()) {
			event.setCanceled(true);
			Long cooldown = command.getCooldownSeconds() - (new Date().getTime() / 1000 - Reference.cooldowns.get(command.getName()).get(event.getSender()));
			Reference.sendMessage(event.getSender(), "You're still on cooldown, try again in " + cooldown + " second" + (cooldown == 1 ? "" : "s") + ".");
		} else {
			Map<ICommandSender, Long> data = new HashMap<>();
			data.put(event.getSender(), new Date().getTime() / 1000);
			Reference.cooldowns.put(event.getCommand().getName(), data);
		}
		// END COOLDOWN SYSTEM
		// START PERMISSIONS SYSTEM
		if (!Reference.checkPermission(event.getSender(), event.getCommand())) {
			Reference.sendMessage(event.getSender(), TextFormatting.RED + "You do not have permission to use this command," + (Reference.isOp((EntityPlayer) event.getSender()) ? " you can give yourself permission to use this command by doing /mcperms group create Operator, /mcperms group addperm Operator " + Permission.getPermissionFromCommand(event.getCommand()) + ", OR /mcperms group addperm Operator *, and /mcperms player addgroup Operator " + event.getSender().getName() + "." : Reference.isSingleplayer() && event.getSender().getEntityWorld().getMinecraftServer().getServerOwner().equals(event.getSender().getName()) ? " to enable cheats, run /togglecheats." : " if you believe you should, ask an admin to set the permissions for you."));
			event.setCanceled(true);
		}
		// END PERMISSIONS SYSTEM
		if (!event.isCanceled() && runOnTicker(event.getCommand())) {
			event.setCanceled(true);
			Ticker.INSTANCE.addRunnable(TickEvent.Type.SERVER, extraArgs -> {
				try {
					event.getCommand().execute(FMLCommonHandler.instance().getMinecraftServerInstance(), event.getSender(), event.getParameters());
				} catch (WrongUsageException e) {
					Reference.sendMessage(event.getSender(), TextFormatting.RED + TextFormatting.getTextWithoutFormattingCodes(new TextComponentTranslation("commands.generic.usage", new Object[] {new TextComponentTranslation(e.getMessage(), e.getErrorObjects())}).getFormattedText()));
					return;
				} catch (CommandException e) {
					Reference.sendMessage(event.getSender(), TextFormatting.RED + TextFormatting.getTextWithoutFormattingCodes(new TextComponentTranslation(e.getMessage(), e.getErrorObjects()).getFormattedText()));
				} catch (Throwable e) {
					e.printStackTrace();
					Reference.sendMessage(event.getSender(), TextFormatting.RED + "An unknown error occured while attempting to perform this command.");
				}
			});
		}
	}

	@SubscribeEvent
	public void onHarvestBlock(PlayerEvent.HarvestCheck event) {}

	@SubscribeEvent
	public void onBlockBreaking(PlayerEvent.BreakSpeed event) {}

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event) {
		// God mode on or fake player
		if (Commandgod.invinciblePlayers.contains(event.getEntity().getUniqueID().toString()) || event.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP) event.getEntity()).connection.netManager.channel().pipeline().get("packet_handler") == null) event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		Reference.netWrapper.sendTo(new ServerCapabilitiesUpdatePacket(event.player.getCapability(ReachProvider.reachCap, null).get()), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		// Below line not necessary for fake players as fake players cannot parse
		// packets anyway and it defaults to false.
		Reference.netWrapper.sendTo(new ServerCapabilitiesUpdatePacket(event.player.getCapability(ReachProvider.reachCap, null).get()), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.player.getCapability(FPProvider.fpCap, null).isFake && event.player instanceof EntityPlayerMP && ((EntityPlayerMP) event.player).connection.netManager.channel().pipeline().get("packet_handler") != null) event.player.getCapability(FPProvider.fpCap, null).isFake = false;
		if (event.player.getCapability(ReachProvider.reachCap, null).get() != event.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()) {
			Reference.sendMessage(event.player, TextFormatting.RED + "Your reach distance is unsynchronized, the main value is", event.player.getCapability(ReachProvider.reachCap, null).get(), "but your secondary value is", event.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getBaseValue() + ", thus it has been reset to your main value, feel free to change this back if you want to.");
			float reach = event.player.getCapability(ReachProvider.reachCap, null).get();
			event.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(reach);
			event.player.getCapability(ReachProvider.reachCap, null).set((EntityPlayerMP) event.player, reach); // This sends a packet so I don't have to.
		} else Reference.netWrapper.sendTo(new ServerCapabilitiesUpdatePacket(event.player.getCapability(ReachProvider.reachCap, null).get()), (EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onServerConnectionFromClient(ServerConnectionFromClientEvent event) {
		if (event.getManager().channel().pipeline().get("packet_handler") != null) event.getManager().channel().pipeline().addBefore("packet_handler", null, new NetHandler(Side.SERVER));
	}

	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (!event.getCapabilities().containsKey(new ResourceLocation(Reference.MOD_ID, "reach")) && event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(Reference.MOD_ID, "reach"), new ReachProvider());
		if (!event.getCapabilities().containsKey(new ResourceLocation(Reference.MOD_ID, "fakeplayer")) && event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(Reference.MOD_ID, "fakeplayer"), new FPProvider());
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		event.getEntityPlayer().getCapability(ReachProvider.reachCap, null).set(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? (EntityPlayerMP) event.getEntityPlayer() : null, event.getOriginal().getCapability(ReachProvider.reachCap, null).get());
		event.getEntityPlayer().getCapability(FPProvider.fpCap, null).isFake = event.getOriginal().getCapability(FPProvider.fpCap, null).isFake;
		event.getEntityPlayer().getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(event.getOriginal().getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
		event.getEntityPlayer().getEntityAttribute(EntityLivingBase.SWIM_SPEED).setBaseValue(event.getOriginal().getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue());
	}

	@SubscribeEvent
	public void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
		cancelIfDisabled(event, event.getWorld());
	}

	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
		cancelIfDisabled(event, event.getWorld());
	}

	@SubscribeEvent
	public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
		cancelIfDisabled(event, event.getWorld());
	}

	private void cancelIfDisabled(Event event, World worldIn) {
		if (worldIn.getGameRules().getBoolean("disableEntitySpawning")) if (event.isCancelable()) event.setCanceled(true);
		else if (event.hasResult()) event.setResult(Result.DENY);
	}

	@SubscribeEvent
	public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (event.getWorld().getGameRules().getBoolean("disableFarmlandTrample")) {
			event.setCanceled(true);
			event.getWorld().setBlockState(event.getPos(), event.getWorld().getBlockState(event.getPos()));
		}
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
