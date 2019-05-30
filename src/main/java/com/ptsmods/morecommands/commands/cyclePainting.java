package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.UUID;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.ReachProvider;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityPainting.EnumArt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class cyclePainting {

	public cyclePainting() {}

	public static class CommandcyclePainting extends com.ptsmods.morecommands.miscellaneous.CommandBase {

		@Override
		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		@Override
		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override
		public String getName() {
			return "cyclepainting";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			RayTraceResult result = Reference.rayTrace(getCommandSenderAsPlayer(sender), getCommandSenderAsPlayer(sender).isCreative() ? getCommandSenderAsPlayer(sender).getCapability(ReachProvider.reachCap, null).get() : 5F);
			if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityPainting) {
				EntityPainting painting = (EntityPainting) result.entityHit;
				EnumArt oldArt = painting.art;
				painting.art = EnumArt.values()[args.length >= 1 && Reference.isInteger(args[0]) ? Integer.parseInt(args[0]) % 26 : painting.art.ordinal() == EnumArt.values().length - 1 ? 0 : painting.art.ordinal() + 1]; // the actual cycling
				painting.setDead();
				BlockPos pos = getSummonLocation(painting.getHangingPosition(), oldArt, painting.art, painting.facingDirection);
				Entity painting0 = AnvilChunkLoader.readWorldEntityPos(painting.serializeNBT(), sender.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), false);
				painting0.setUniqueId(UUID.randomUUID());
				sender.getEntityWorld().spawnEntity(painting0);
			} else Reference.sendMessage(sender, TextFormatting.RED + "It appears as if you're not looking at a painting.");
		}

		private BlockPos getSummonLocation(BlockPos oldLocation, EnumArt oldArt, EnumArt newArt, EnumFacing facing) {
			// Making this took longer than I'd like to admit.
			int oX = oldLocation.getX();
			int oY = oldLocation.getY();
			int oZ = oldLocation.getZ();
			int[] oOffsets = getOffsets(oldArt);
			int oOffsetXZ = oOffsets[0];
			int oOffsetY = oOffsets[1];
			int nX = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? oX + oOffsetXZ : oX;
			int nY = oY + oOffsetY;
			int nZ = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? oZ + oOffsetXZ : oZ;
			int[] nOffsets = getOffsets(newArt);
			int nOffsetXZ = nOffsets[0];
			int nOffsetY = nOffsets[1];
			nX = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? nX - nOffsetXZ : nX;
			nY = nY - nOffsetY;
			nZ = facing == EnumFacing.WEST || facing == EnumFacing.EAST ? nZ - nOffsetXZ : nZ;
			return new BlockPos(nX, nY, nZ);
		}

		private int[] getOffsets(EnumArt art) {
			int offsetXZ = 0;
			int offsetY = 0;
			switch (art) {
			case WANDERER:
			case GRAHAM:
			case MATCH:
			case BUST:
			case STAGE:
			case VOID:
			case SKULL_AND_ROSES:
			case WITHER:
				offsetY = 1;
				break;
			case FIGHTERS:
				offsetXZ = 1;
				offsetY = 1;
				break;
			case POINTER:
			case PIGSCENE:
			case BURNING_SKULL:
				offsetY = 2;
				offsetXZ = 1;
				break;
			case SKELETON:
			case DONKEY_KONG:
				offsetY = 1;
				offsetXZ = 1;
			default:
				break; // All the 1x1 paintings
			}
			return new int[] { offsetXZ, offsetY };
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "cyclepainting", "Change the style of the painting you're looking at.", true);
		}

		private String usage = "/cyclepainting [ordinal] Change the style of the painting you're looking at to the next one in the list or to the set ordinal. Ordinal should be an integer and can range from 0 to 25. (26 paintings in total)";

	}

}