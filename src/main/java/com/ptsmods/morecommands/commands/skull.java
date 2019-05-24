package com.ptsmods.morecommands.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mojang.util.UUIDTypeAdapter;
import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;
import com.ptsmods.morecommands.miscellaneous.Ticker;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class skull {

	public skull() {}

	public static class Commandskull extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getPlayerList().getOnlinePlayerNames()) : new ArrayList();
		}

		@Override
		public String getName() {
			return "skull";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length == 0) Reference.sendCommandUsage(sender, usage);
			else if (!(sender instanceof Entity)) Reference.sendMessage(sender, TextFormatting.RED + "Only entities may use this command.");
			else Reference.execute(() -> {
				ItemStack stack = new ItemStack(Items.SKULL, args.length >= 2 && Reference.isInteger(args[1]) ? Integer.parseInt(args[1]) : 64, 3);
				NBTTagCompound tag = new NBTTagCompound();
				if (args.length >= 3 && Reference.isBoolean(args[2]) && Boolean.parseBoolean(args[2]) || args.length < 3) {
					String playername = args[0];
					try {
						UUID id = UUIDTypeAdapter.fromString(playername);
						try {
							playername = new Gson().fromJson(Reference.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(id)), Map.class).get("name").toString();
						} catch (JsonSyntaxException | IOException e) {
							e.printStackTrace();
							Reference.sendMessage(sender, "An error occurred while getting the playername attached to the given UUID.");
							return;
						}
					} catch (IllegalArgumentException e) {} // Given playername is not a UUID.
					tag.setString("SkullOwner", playername);
				} else {
					UUID id;
					try {
						id = UUIDTypeAdapter.fromString(args[0]);
					} catch (IllegalArgumentException e) {
						try {
							id = UUIDTypeAdapter.fromString(MoreObjects.firstNonNull(new Gson().fromJson(Reference.getHTML("https://api.mojang.com/users/profiles/minecraft/" + args[0]), Map.class).get("id").toString(), ((EntityLivingBase) sender).getUniqueID().toString()));
						} catch (JsonSyntaxException | IOException e1) {
							e1.printStackTrace();
							Reference.sendMessage(sender, TextFormatting.RED + "The UUID of the player could not be gotten, is the playername valid?");
							return;
						}
					}
					try {
						Map<String, Object> data0 = new Gson().fromJson(Reference.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(id)), Map.class);
						List<Map> properties = (List<Map>) data0.get("properties");
						if (properties == null || properties.isEmpty()) Reference.sendMessage(sender, TextFormatting.RED + "A skin for the given player could not be found.");
						else {
							NBTTagCompound owner = new NBTTagCompound();
							owner.setString("Id", id.toString());
							NBTTagCompound propTag = new NBTTagCompound();
							NBTTagList textures = new NBTTagList();
							NBTTagCompound value = new NBTTagCompound();
							value.setString("Value", properties.get(0).get("value").toString());
							textures.appendTag(value);
							propTag.setTag("textures", textures);
							owner.setTag("Properties", propTag);
							tag.setTag("SkullOwner", owner);
							NBTTagCompound name = new NBTTagCompound();
							name.setString("Name", TextFormatting.RESET + data0.get("name").toString() + "'s Head");
							tag.setTag("display", name);
						}
					} catch (Exception e) {
						if (e instanceof IOException && e.getMessage() != null && e.getMessage().contains("429")) {
							Reference.sendMessage(sender, "Rate limit! Please wait a minute before trying again. (The api can only be accessed once every minute for every UUID)");
							return;
						}
						e.printStackTrace();
						Reference.sendMessage(sender, TextFormatting.RED + "An error occured while getting the encoded data of the given player, please report this to MoreCommand's creator and don't forget to include the playername/UUID used.");
						return;
					}
				}
				stack.setTagCompound(tag);
				Ticker.INSTANCE.addRunnable(TickEvent.Type.SERVER, extraArgs -> {
					if (sender instanceof EntityPlayer) ((EntityPlayer) sender).inventory.addItemStackToInventory(stack);
					else((Entity) sender).entityDropItem(stack, ((Entity) sender).getEyeHeight());
					Reference.playSound((Entity) sender, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS);
					Reference.sendMessage(sender, "Your skull" + (stack.getCount() == 1 ? "" : "s") + " ha" + (stack.getCount() == 1 ? "s" : "ve") + " arrived.");
				});
			});
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "skull", "Get absolutely anyone's head.", true);
		}

		private String usage = "/skull <player> [amount] [update] Get another player's head, player should be either a username or a UUID, update affects whether the skull will automatically update when the owner of the skull changes their skin and defaults to true.";

	}

}