package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class potion {

	public potion() {}

	public static class Commandpotion extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			aliases.add("pot");
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 1 ? getListOfStringsMatchingLastWord(args, Lists.newArrayList("setcolour", "settype", "add", "remove")) : args.length == 2 ? args[0].equals("add") ? getListOfStringsMatchingLastWord(args, Potion.REGISTRY.getKeys()) : args[0].equals("settype") ? getListOfStringsMatchingLastWord(args, PotionType.REGISTRY.getKeys()) : new ArrayList() : new ArrayList();
		}

		@Override
		public String getName() {
			return "potion";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length < 2 || !Lists.newArrayList("setcolour", "setcolor", "settype", "add", "remove").contains(args[0])) Reference.sendCommandUsage(sender, usage);
			else if (!(sender instanceof EntityLivingBase)) Reference.sendMessage(sender, TextFormatting.RED + "Only living entities may use this command.");
			else {
				ItemStack held = ((EntityLivingBase) sender).getHeldItemMainhand();
				if (held.getItem() != Items.POTIONITEM) Reference.sendMessage(sender, "You must be holding a potion item for this command to work.");
				else if (args[0].equals("setcolour") || args[0].equals("setcolor")) {
					if (!Reference.isInteger(args[1], 16) && !Reference.isInteger(args[1].substring(1), 16)) Reference.sendMessage(sender, TextFormatting.RED + "The given colour was invalid.");
					else {
						NBTTagCompound nbt = MoreObjects.firstNonNull(held.getTagCompound(), new NBTTagCompound());
						nbt.setInteger("CustomPotionColor", Integer.parseInt(args[1].startsWith("#") ? args[1].substring(1) : args[1], 16));
						held.setTagCompound(nbt);
						Reference.sendMessage(sender, "The colour of your potion has been set.");
					}
				} else if (args[0].equals("settype")) {
					PotionType type = PotionType.REGISTRY.getObject(new ResourceLocation(args[1]));
					if (type.getRegistryName().equals(new ResourceLocation("minecraft:empty"))) Reference.sendMessage(sender, TextFormatting.RED + "The given type could not be found.");
					else {
						PotionUtils.addPotionToItemStack(held, type);
						Reference.sendMessage(sender, "Your potion's type has been set.");
					}
				} else if (args[0].equals("add")) {
					ResourceLocation loc = new ResourceLocation(args[1]);
					Potion effect = null;
					for (Potion pot : Iterators.toArray(Potion.REGISTRY.iterator(), Potion.class))
						if (pot.getRegistryName().equals(loc)) {
							effect = pot;
							break;
						}
					if (effect == null) Reference.sendMessage(sender, TextFormatting.RED + "The given effect could not be found.");
					else {
						int duration = args.length >= 3 && Reference.isInteger(args[2]) ? Integer.parseInt(args[2]) * 20 : 60 * 20;
						int amplifier = args.length >= 4 && Reference.isInteger(args[3]) ? Math.min(Integer.parseInt(args[3]), Byte.MAX_VALUE) : 0;
						boolean showParticles = args.length >= 5 && Reference.isBoolean(args[4]) ? Boolean.parseBoolean(args[4]) : true;
						boolean ambient = args.length >= 6 && Reference.isBoolean(args[5]) ? Boolean.parseBoolean(args[5]) : false;
						NBTTagList oldTagList = MoreObjects.firstNonNull(held.getTagCompound(), new NBTTagCompound()).getTagList("CustomPotionEffects", 10).copy();
						PotionUtils.appendEffects(held, Lists.newArrayList(new PotionEffect(effect, duration, amplifier, ambient, showParticles)));
						NBTTagList newTagList = MoreObjects.firstNonNull(held.getTagCompound(), new NBTTagCompound()).getTagList("CustomPotionEffects", 10);
						oldTagList.forEach(nbt -> newTagList.appendTag(nbt)); // There seems to be an internal bug where it gets the tag list with a
																				// type of 9 even though its type is 10, so we fix that here.
						Reference.sendMessage(sender, "Successfully added the effect to your potion.");
					}
				} else if (args[0].equals("remove")) {
					NBTTagList list = MoreObjects.firstNonNull(held.getTagCompound(), new NBTTagCompound()).getTagList("CustomPotionEffects", 10);
					if (!Reference.isInteger(args[1])) Reference.sendMessage(sender, TextFormatting.RED + "The given index was not an integer.");
					else if (Integer.parseInt(args[1]) <= 0) Reference.sendMessage(sender, TextFormatting.RED + "Index cannot be 0 or less.");
					else if (Integer.parseInt(args[1]) > list.tagCount()) Reference.sendMessage(sender, TextFormatting.RED + "The given index (" + Integer.parseInt(args[1]) + ") was greater than the amount of custom effects on your potion (" + list.tagCount() + ").");
					else {
						PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT((NBTTagCompound) list.get(Integer.parseInt(args[1]) - 1));
						list.removeTag(Integer.parseInt(args[1]) - 1);
						Reference.sendMessage(sender, "Effect " + Integer.parseInt(args[1]) + " of type " + effect.getPotion().getRegistryName() + " with a duration of " + effect.getDuration() / 20 + " seconds and an amplifier of " + effect.getAmplifier() + " which did " + (effect.doesShowParticles() ? "" : "not ") + "show particles and was" + (effect.getIsAmbient() ? "" : "n't") + " ambient has been removed.");
					}
				}
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "potion", "Add or remove an effect to/from your potion or set its colour.", true);
		}

		private String usage = "/potion <setcolour|settype|add|remove> <hex colour>/<type>/<effect>/<index> [duration] [amplifier] [show particles] [ambient] Add or remove an effect to your potion or set its colour. Duration should be an integer and defaults to 60, amplifier should be a byte (int with a max value of 127) and defaults to 0, index-based, show-particles should be a boolean and defaults to true and ambient (semi-show particles) should be a boolean and defaults to false. E.g. /potion setcolour #000000 OR /potion settype minecraft:long_night_vision OR /potion add minecraft:fire_resistance 120 1 false true OR /potion remove 1.";

	}

}