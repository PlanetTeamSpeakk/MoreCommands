package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

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

		@Override
		public int getRequiredPermissionLevel() {
			return 2;
		}

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
			return "fireball";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			EntityPlayer player = (EntityPlayer) sender;
			//Minecraft.getMinecraft().player.sendChatMessage("/fireball"); // :D
			String power = "1";
			if (args.length != 0 && Reference.isInteger(args[0])) power = args[0];
			World world = player.getEntityWorld();
			Vec3d lookvec = player.getLookVec();
			NBTTagCompound nbt;
			try {
				nbt = JsonToNBT.getTagFromJson("{id:\"minecraft:fireball\",direction:[" + Double.toString(lookvec.x) + "," + Double.toString(lookvec.y) + "," + Double.toString(lookvec.z) + "],"
						+ "power:[" + Double.toString(lookvec.x) + "," + Double.toString(lookvec.y) + "," + Double.toString(lookvec.z) + "],ExplosionPower:" + power + "}");
			} catch (NBTException e) {
				return;
			}
			String direction = Reference.getLookDirectionFromLookVec(lookvec, true);
			double d0 = player.getPositionEyes(0).x;
			double d1 = player.getPositionEyes(0).y; // this will shoot it from the player's eyes.
			double d2 = player.getPositionEyes(0).z;
			if (direction.equals("up"))
				d1 += 2;
			else if (direction.equals("down"))
				d1 -= 2;
			else if (direction.equals("north"))
				d2 -= 2;
			else if (direction.equals("north-east")) {
				d2 -= 2;
				d0 += 2;
			} else if (direction.equals("east"))
				d0 += 2;
			else if (direction.equals("south-east")) {
				d2 += 2;
				d0 += 2;
			} else if (direction.equals("south"))
				d2 += 2;
			else if (direction.equals("south-west")) {
				d2 += 2;
				d0 -= 2;
			} else if (direction.equals("west"))
				d0 -= 2;
			else if (direction.equals("north-west")) {
				d0 -= 2;
				d2 -= 2;
			}
			Entity entity = AnvilChunkLoader.readWorldEntityPos(nbt, world, d0, d1, d2, true);
			if (entity != null) entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);

		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "fireball", "Permission to use the fireball command.", true);
		}

		protected String usage = "/fireball [power] Summons a fireball in the direction you're looking, power defaults to 1.";

	}

}