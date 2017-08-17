package com.ptsmods.morecommands.miscellaneous;

import java.util.Date;
import java.util.HashMap;

import com.ptsmods.morecommands.commands.fixTime.CommandfixTime;

import net.minecraft.block.BlockStairs;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerEventHandler extends EventHandler {

	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		if (CommandfixTime.time != -1 && CommandfixTime.server != null) {
			try {new CommandfixTime().setAllWorldTimes(CommandfixTime.server, CommandfixTime.time);} catch (NullPointerException e) {} // Probably not necessary because it's on server tick and not client tick 
		}																															   // but just for that extra bit of security
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
		try {
			CommandBase command = ((CommandBase) event.getCommand()); // checking if the command extends com.ptsmods.morecommands.miscellaneous.CommandBase
		} catch (ClassCastException e) {return;}
		if (((CommandBase) event.getCommand()).hasCooldown()) {
			if (Reference.cooldowns.containsKey(event.getCommand().getName()) && 
					Reference.cooldowns.get(event.getCommand().getName()).containsKey(event.getSender()) && 
					new Date().getTime()/1000-Reference.cooldowns.get(event.getCommand().getName()).get(event.getSender()) <= ((CommandBase) event.getCommand()).getCooldownSeconds()) {
				event.setCanceled(true);
				Long cooldown = ((CommandBase) event.getCommand()).getCooldownSeconds()-(new Date().getTime()/1000-Reference.cooldowns.get(event.getCommand().getName()).get(event.getSender()));
				Reference.sendMessage(event.getSender(), "You're still on cooldown, try again in " + cooldown + " second" + (cooldown == 1 ? "" : "s") + ".");
			} else {
				HashMap<ICommandSender, Long> data = new HashMap<ICommandSender, Long>();
				data.put(event.getSender(), new Date().getTime()/1000);
				Reference.cooldowns.put(event.getCommand().getName(), data);
			}
		}
	}
	
}
