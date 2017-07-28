package com.ptsmods.morecommands.commands;

import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class enchant {

	public static class Commandenchant extends net.minecraft.command.CommandEnchant {
		
		/*
		 * Copied code from net.minecraft.command.CommandEnchant.execute
		 * 
		 * CHANGES:
		 * The command no longer has any restrictions.
		 */
		@Override
	    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	        if (args.length < 2) {
	            throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
	        } else {
	            EntityLivingBase entitylivingbase = (EntityLivingBase)getEntity(server, sender, args[0], EntityLivingBase.class);
	            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
	            Enchantment enchantment;

	            try {
	                enchantment = Enchantment.getEnchantmentByID(parseInt(args[1], 0));
	            } catch (NumberInvalidException var12) {
	                enchantment = Enchantment.getEnchantmentByLocation(args[1]);
	            }

	            if (enchantment == null) {
	                throw new NumberInvalidException("commands.enchant.notFound", new Object[] {args[1]});
	            } else {
	            	int i = 1;
	            	ItemStack itemstack = entitylivingbase.getHeldItemMainhand();
	            	if (args.length >= 3) {
	            		i = parseInt(args[2], enchantment.getMinLevel(), 127);
	            	}

	            	itemstack.addEnchantment(enchantment, i);
	            	Reference.sendMessage(sender, "Successfully enchanted your item.");
	            	sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
	                
	            }
	        }
	    }
		
	    public String getCommandUsage(ICommandSender sender) {
	        return "commands.enchant.usage" + " Enchant any item with any enchant of any level, thanks to MoreCommands.";
	    }
		
	}
	
}
