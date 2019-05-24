package com.ptsmods.morecommands.commands;

import java.util.ArrayList;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class spawnmob {

	public spawnmob() {}

	public static class CommandspawnMob extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "spawnmob";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			if (args.length != 0) {
				int amount = args.length >= 2 && Reference.isInteger(args[1]) ? Integer.parseInt(args[1]) : 1;
				RayTraceResult result = Reference.rayTrace((Entity) sender, 160);
				double d0 = result.hitVec.x;
				double d1 = result.hitVec.y;
				double d2 = result.hitVec.z;
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("id", args[0]);
				Entity entity = AnvilChunkLoader.readWorldEntityPos(compound, sender.getEntityWorld(), Math.floor(d0) + 0.5D, d1, Math.floor(d2) + 0.5D, true);
				if (entity != null) {
					Reference.teleportSafely(entity);
					d0 = entity.posX;
					d1 = entity.posY;
					d2 = entity.posZ;
					int success = 1;
					int fail = 0;
					for (int i = 0; i < amount - 1; i++)
						if (AnvilChunkLoader.readWorldEntityPos(compound, sender.getEntityWorld(), d0, d1, d2, true) != null) success++;
						else fail++;
					Reference.sendMessage(sender, TextFormatting.GREEN + "Successfully" + Reference.dtf + " spawned " + success + " entit" + (success == 1 ? "y" : "ies") + " of type " + entity.serializeNBT().getString("id") + (fail == 0 ? "." : "; however, " + fail + " entit" + (fail == 1 ? "y" : "ies") + " did " + TextFormatting.RED + "not" + Reference.dtf + "."));
				} else Reference.sendMessage(sender, "The entities could not be spawned.");
			} else Reference.sendCommandUsage(sender, usage);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "spawnmob", "Spawns a certain amount of a certain mob wherever you're looking.", true);
		}

		private String usage = "/spawnmob <id> [amount] Spawns a certain amount of a certain mob wherever you're looking. Amount defaults to 1.";

	}

}