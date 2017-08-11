package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class fireball {

	public fireball() {
	}

	public static class Commandfireball extends com.ptsmods.morecommands.miscellaneous.CommandBase {

	    public int getRequiredPermissionLevel() {
	        return 2;
	    }

		public java.util.List getAliases() {
			ArrayList aliases = new ArrayList();
			return aliases;
		}

		public java.util.List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		public String getName() {
			return "fireball";
		}

		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			World world = player.getEntityWorld();
			Vec3d lookvec = player.getLookVec();
			NBTTagCompound nbt;
			try {
				nbt = JsonToNBT.getTagFromJson("{id:\"minecraft:fireball\",direction:[" + Double.toString(lookvec.x) + "," + Double.toString(lookvec.y) + "," + Double.toString(lookvec.z) + "],"
						+ "power:[" + Double.toString(lookvec.x) + "," + Double.toString(lookvec.y) + "," + Double.toString(lookvec.z) + "],ExplosionPower:1}");
			} catch (NBTException e) {
				return;
			}
			String direction = Reference.getLookDirectionFromLookVec(lookvec);
			double d0 = player.getPosition().getX() + 0.5;
			double d1 = player.getPosition().getY() + player.getEyeHeight(); // this will shoot the fireball from your eyes.
			double d2 = player.getPosition().getZ() + 0.5;
			if (direction.equals("up")) {
				d1 += 2;
			} else if (direction.equals("down")) {
				d1 -= (player.getEyeHeight() + 2);
			} else if (direction.equals("north")) {
				d2 -= 2;
			} else if (direction.equals("north-east")) {
				d2 -= 2;
				d0 += 2;
			} else if (direction.equals("east")) {
				d0 += 2;
			} else if (direction.equals("south-east")) {
				d2 += 2;
				d0 += 2;
			} else if (direction.equals("south")) {
				d2 += 2;
			} else if (direction.equals("south-west")) {
				d2 += 2;
				d0 -= 2;
			} else if (direction.equals("west")) {
				d0 -= 2;
			} else if (direction.equals("north-west")) {
				d0 -= 2;
				d2 -= 2;
			}
			Entity entity = AnvilChunkLoader.readWorldEntityPos(nbt, world, d0, d1, d2, true);
			entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);

		}
		
		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}
		
		protected String usage = "/fireball Summons a fireball in the direction you're looking.";

	}

}