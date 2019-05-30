package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class cannon {

	public cannon() {}

	public static class Commandcannon extends com.ptsmods.morecommands.miscellaneous.CommandBase {

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
			return "cannon";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return usage;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			Vec3d looking = player.getLookVec();
			AtomicInteger fuse = new AtomicInteger(args.length >= 3 && Reference.isInteger(args[2]) ? Integer.parseInt(args[2]) : 80);
			EntityTNTPrimed tnt = new EntityTNTPrimed(sender.getEntityWorld(), player.posX, player.posY + player.getEyeHeight(), player.posZ, null) {
				@Override
				public void onUpdate() {
					prevPosX = posX;
					prevPosY = posY;
					prevPosZ = posZ;
					if (!hasNoGravity()) motionY -= 0.03999999910593033D;
					move(MoverType.SELF, motionX, motionY, motionZ);
					motionX *= 0.9800000190734863D;
					motionY *= 0.9800000190734863D;
					motionZ *= 0.9800000190734863D;
					if (onGround) {
						motionX *= 0.699999988079071D;
						motionZ *= 0.699999988079071D;
						motionY *= -0.5D;
					}
					fuse.decrementAndGet();
					if (fuse.get() <= 0) {
						setDead();
						if (!world.isRemote) world.newExplosion(this, posX, posY + height / 16.0F, posZ, args.length >= 1 && Reference.isFloat(args[0]) ? Float.parseFloat(args[0]) : 4F, false, true);
					} else {
						handleWaterMovement();
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
					}
				}
			};
			float motionMultiplier = args.length >= 2 && Reference.isFloat(args[1]) ? Float.parseFloat(args[1]) : 1.5f;
			tnt.motionX = looking.x * motionMultiplier;
			tnt.motionY = looking.y * motionMultiplier;
			tnt.motionZ = looking.z * motionMultiplier;
			sender.getEntityWorld().spawnEntity(tnt);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "cannon", "Fire some primed TriNitroToluene!", true);
		}

		private String usage = "/cannon [power] [motion multiplier] [fuse] Fire some primed TriNitroToluene! Power should be a float and defaults to 4, motion multiplier should be a float and defaults to 1.5, fuse should be an int and defaults to 80.";

	}

}