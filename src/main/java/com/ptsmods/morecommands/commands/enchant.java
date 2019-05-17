package com.ptsmods.morecommands.commands;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class enchant {

	public static class Commandenchant extends net.minecraft.command.CommandEnchant {
		
		private volatile boolean silent = false;
		
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
	        	if (args[1].equals("*")) {
	        		silent = true;
	        		for (Enchantment enchantment : Enchantment.REGISTRY) {
	        			args[1] = enchantment.getRegistryName().toString();
	        			execute(server, sender, args);
	        		}
	        		silent = false;
	        		Reference.sendMessage(sender, "Successfully enchanted your item.");
	        	} else if (args[1].contains(";")) {
	        		silent = true;
	        		for (String ench : args[1].split(";")) {
	        			args[1] = ench;
	        			execute(server, sender, args);
	        		}
	        		silent = false;
	        		Reference.sendMessage(sender, "Successfully enchanted your item.");
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
		            		i = parseInt(args[2], enchantment.getMinLevel(), Short.MAX_VALUE);
		            	}
		            	if (itemstack.getTagCompound() == null) {
		            		itemstack.setTagCompound(new NBTTagCompound());
		                }
		                if (!itemstack.getTagCompound().hasKey("ench", 9)) {
		                	itemstack.getTagCompound().setTag("ench", new NBTTagList());
		                }
		                NBTTagList nbttaglist = itemstack.getTagCompound().getTagList("ench", 10);
		                NBTTagCompound nbttagcompound = new NBTTagCompound();
		                nbttagcompound.setShort("id", (short) Enchantment.getEnchantmentID(enchantment));
		                nbttagcompound.setShort("lvl", (short) i);
		                nbttaglist.appendTag(nbttagcompound);
		                if (!silent) Reference.sendMessage(sender, "Successfully enchanted your item.");
		            	sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
		                
		            }
	        	}
	        }
	    }
		
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
	    public String getCommandUsage(ICommandSender sender) {
	        return "commands.enchant.usage";
	    }
		
	}
	
}
