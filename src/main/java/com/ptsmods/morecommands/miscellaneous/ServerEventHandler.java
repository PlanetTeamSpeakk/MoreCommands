package com.ptsmods.morecommands.miscellaneous;

import java.util.Date;
import java.util.HashMap;

import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;

import net.minecraft.block.BlockStairs;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerEventHandler extends EventHandler {

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (CommandfixTime.time != -1 && CommandfixTime.server != null)
			try {new CommandfixTime().setAllWorldTimes(CommandfixTime.server, CommandfixTime.time);} catch (NullPointerException e) {} // Probably not necessary because it's on server tick and not client tick
	}

	@SubscribeEvent
	public void onPlayerUseItem(PlayerInteractEvent.RightClickBlock event) throws CommandException {
		if (Reference.isSittingOnChair && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockStairs) {
			event.getEntityPlayer().dismountEntity(Reference.arrow);
			Reference.arrow.onKillCommand();
		}
		Reference.sitOnStairs(event, event.getEntityPlayer(), event.getPos(), event.getEntityPlayer().getServer());
	}

	@SubscribeEvent
	public void onCommand(CommandEvent event) {
		CommandBase command = null;
		try {
			command = (CommandBase) event.getCommand(); // checking if the command extends com.ptsmods.morecommands.miscellaneous.CommandBase
		} catch (ClassCastException e) {return;}
		if (command.singleplayerOnly() && !FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
			Reference.sendMessage(event.getSender(), "This command is currently only for singleplayer.");
			event.setCanceled(true);
			// START COOLDOWN SYSTEM
		} else if (command.hasCooldown())
			if (Reference.cooldowns.containsKey(event.getCommand().getName()) &&
					Reference.cooldowns.get(event.getCommand().getName()).containsKey(event.getSender()) &&
					new Date().getTime()/1000-Reference.cooldowns.get(event.getCommand().getName()).get(event.getSender()) <= ((CommandBase) event.getCommand()).getCooldownSeconds()) {
				event.setCanceled(true);
				Long cooldown = ((CommandBase) event.getCommand()).getCooldownSeconds()-(new Date().getTime()/1000-Reference.cooldowns.get(event.getCommand().getName()).get(event.getSender()));
				Reference.sendMessage(event.getSender(), "You're still on cooldown, try again in " + cooldown + " second" + (cooldown == 1 ? "" : "s") + ".");
			} else {
				HashMap<ICommandSender, Long> data = new HashMap<>();
				data.put(event.getSender(), new Date().getTime()/1000);
				Reference.cooldowns.put(event.getCommand().getName(), data);
			}
		// END COOLDOWN SYSTEM
		// START PERMISSIONS SYSTEM
		if (!Reference.checkPermission(event.getSender(), command.getPermission())) {
			Reference.sendMessage(event.getSender(), TextFormatting.RED + "You do not have permission to use this command, " + (Reference.isOp((EntityPlayer) event.getSender()) ?
					" you can give yourself permissions to use this command by doing /mcperms group create Operator, /mcperms group addperm Operator " + command.getPermission().toString() +
					", or just a *, and /mcperms player addgroup Operator " + event.getSender().getName() :
					" if you believe you should ask an admin to set the permissions for you."));
			event.setCanceled(true);
		}
		// END PERMISSIONS SYSTEM
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getEntity().getIsInvulnerable()) {
			Reference.inventories.put((EntityPlayer) event.getEntity(), ((EntityPlayer) event.getEntity()).inventory.writeToNBT(new NBTTagList()));
			((EntityPlayer) event.getEntity()).inventory.clear();
			HashMap<String, Float> data = new HashMap<>();
			data.put("yaw", event.getEntity().rotationYaw);
			data.put("pitch", event.getEntity().rotationPitch);
			Reference.pitchNYaws.put((EntityPlayer) event.getEntity(), data);
			Vec3d location = event.getEntity().getPositionVector();
			if (location.y < 0D) location = new Vec3d(location.x, 0D, location.z);
			Reference.locations.put((EntityPlayer) event.getEntity(), event.getEntity().getPositionVector());
			Reference.experiencePoints.put((EntityPlayer) event.getEntity(), ((EntityPlayer) event.getEntity()).experienceTotal - 100);
			Reference.removeExperience((EntityPlayer) event.getEntity(), ((EntityPlayer) event.getEntity()).experienceTotal + 100);
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (Reference.inventories.containsKey(event.player)) {
			event.player.setEntityInvulnerable(true);
			event.player.inventory.readFromNBT(Reference.inventories.get(event.player));
			event.player.setPositionAndRotation(0, 0, 0, Reference.pitchNYaws.get(event.player).get("yaw"), Reference.pitchNYaws.get(event.player).get("pitch")); // I like to pitch, I never catch.
			event.player.setPositionAndUpdate(Reference.locations.get(event.player).x, Reference.locations.get(event.player).y, Reference.locations.get(event.player).z);
			event.player.addExperience(Reference.experiencePoints.get(event.player));
			Reference.sendMessage(event.player, "You died, but since you had god on your location, inventory and experience has been recovered and it's now like nothing happened.");
		}
	}

}
