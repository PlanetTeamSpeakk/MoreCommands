package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class clone {

	public clone() {}

	public static class Commandclone extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "clone";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length == 0) {
				RayTraceResult result = Reference.rayTrace(getCommandSenderAsPlayer(sender), 160F);
				if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
					if (!(result.entityHit instanceof EntityPlayer)) {
						Entity e = AnvilChunkLoader.readWorldEntityPos(result.entityHit.serializeNBT(), sender.getEntityWorld(), result.entityHit.posX, result.entityHit.posY, result.entityHit.posZ, false);
						if (e != null) {
							e.setUniqueId(UUID.randomUUID());
							sender.getEntityWorld().spawnEntity(e);
							Reference.sendMessage(sender, TextFormatting.GREEN + "Successfully" + Reference.dtf + " cloned entity of type", e.serializeNBT().getString("id") + ".");
						} else Reference.sendMessage(sender, TextFormatting.DARK_RED + "The entity could not be cloned, huh.");
					} else Reference.sendMessage(sender, TextFormatting.RED + "Players cannot be cloned.");
				} else Reference.sendMessage(sender, TextFormatting.RED + "It appears as if you're not looking at an entity.");
			} else {
				List<Entity> entities = EntitySelector.matchEntities(sender, Reference.join(args), Entity.class);
				int success = 0;
				int fail = 0;
				for (Entity e : entities) {
					Entity e0;
					try {
						e0 = AnvilChunkLoader.readWorldEntityPos(e.serializeNBT(), sender.getEntityWorld(), e.posX, e.posY, e.posZ, false);
					} catch (Exception ex) {
						fail++;
						continue;
					}
					if (e0 != null) {
						e0.setUniqueId(UUID.randomUUID());
						sender.getEntityWorld().spawnEntity(e0);
						if (e.world != null) success++;
						else fail++;
					} else fail++;
				}
				Reference.sendMessage(sender, TextFormatting.GRAY + "" + success + Reference.dtf + " entit" + (success == 1 ? "y was " : "ies were ") + TextFormatting.GREEN + "successfully" + Reference.dtf + " cloned" + (fail == 0 ? "." : " while " + TextFormatting.GRAY + "" + fail + Reference.dtf + " entit" + (fail == 1 ? "y was " : "ies were ") + TextFormatting.RED + "not" + Reference.dtf + "."));
			}
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "clone", "Clone the entity you're looking at.", true);
		}

		private String usage = "/clone [selector] Clones whatever entity you're looking at or clones every entity matching the given selector, e.g. @e[type=zombie].";

	}

}