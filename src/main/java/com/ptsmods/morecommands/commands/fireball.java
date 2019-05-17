package com.ptsmods.morecommands.commands;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.ptsmods.morecommands.miscellaneous.CommandType;
import com.ptsmods.morecommands.miscellaneous.Permission;
import com.ptsmods.morecommands.miscellaneous.Reference;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;

public class fireball {

	public fireball() {}

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
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			EntityPlayer player = getCommandSenderAsPlayer(sender);
			Vec3d looking = player.getLookVec();
			int impacts = args.length >= 3 && Reference.isInteger(args[2]) ? Integer.parseInt(args[2]) : 1;
			AtomicInteger impactsDone = new AtomicInteger();
			EntityFireball fireball;
			// Yet another little easteregg, we're at 5 now.
			// 1. /easteregg, see ClientEventHandler.
			// 2. Structure, see EEGenerator.
			// 3. This
			// 4. Rainbow highlight, see ClientEventHandler.
			// 5. Dico biomes, see ClientEventHandler.
			if (Reference.isAprilFirst()) fireball = new EntityWitherSkull(player.getEntityWorld(), player, 0.1, 0.1, 0.1) {
				@Override
				public void onImpact(RayTraceResult result) {
					// Copied from super onImpact so I can change the explosion power
					if (!world.isRemote) {
						if (result.entityHit != null) {
							if (shootingEntity != null) {
								if (result.entityHit.attackEntityFrom(DamageSource.causeMobDamage(shootingEntity), 8.0F)) if (result.entityHit.isEntityAlive()) applyEnchantments(shootingEntity, result.entityHit);
								else shootingEntity.heal(5.0F);
							} else result.entityHit.attackEntityFrom(DamageSource.MAGIC, 5.0F);
							if (result.entityHit instanceof EntityLivingBase) {
								int i = 0;
								if (world.getDifficulty() == EnumDifficulty.NORMAL) i = 10;
								else if (world.getDifficulty() == EnumDifficulty.HARD) i = 40;
								if (i > 0) ((EntityLivingBase) result.entityHit).addPotionEffect(new PotionEffect(MobEffects.WITHER, 20 * i, 1));
							}
						}
						world.newExplosion(this, posX, posY, posZ, args.length != 0 && Reference.isInteger(args[0]) ? Integer.parseInt(args[0]) : 1F, false, net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity));
						setDead();
					}
					if (impacts <= 0 || impactsDone.incrementAndGet() < impacts) isDead = false;
				}
			};
			else fireball = new EntityLargeFireball(player.getEntityWorld(), player, 0.1, 0.1, 0.1) {
				@Override
				public void onImpact(RayTraceResult result) {
					super.onImpact(result);
					if (impacts <= 0 || impactsDone.incrementAndGet() < impacts) isDead = false;
					// I love this feature :D
				}
			};
			fireball.setPositionAndUpdate(fireball.posX, fireball.posY + player.getEyeHeight(), fireball.posZ);
			if (args.length != 0 && Reference.isInteger(args[0]) && fireball instanceof EntityLargeFireball) ((EntityLargeFireball) fireball).explosionPower = Integer.parseInt(args[0]); // If the fireball is a wither skull, the explosion power is handled in the
																																															// onImpact method.
			double coefficient = 0.2;
			if (args.length >= 2 && Reference.isDouble(args[1])) coefficient = Double.parseDouble(args[1]) / 10D;
			fireball.motionX = looking.x;
			fireball.motionY = looking.y;
			fireball.motionZ = looking.z;
			fireball.accelerationX = fireball.motionX * coefficient;
			fireball.accelerationY = fireball.motionY * coefficient;
			fireball.accelerationZ = fireball.motionZ * coefficient;
			player.getEntityWorld().spawnEntity(fireball);
		}

		@Override
		public CommandType getCommandType() {
			return CommandType.SERVER;
		}

		@Override
		public Permission getPermission() {
			return new Permission(Reference.MOD_ID, "fireball", "Permission to use the fireball command.", true);
		}

		protected String usage = "/fireball [power] [speed] [impacts] Summons a fireball in the direction you're looking. Power and impacts default to 1 and speed defaults to 2. Impacts means how many times the fireball should explode before it dies, setting it to 0 or below is fun, especially with high power.";

	}

}