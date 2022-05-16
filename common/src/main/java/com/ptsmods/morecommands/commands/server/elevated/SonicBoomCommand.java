package com.ptsmods.morecommands.commands.server.elevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinTaskAccessor;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SonicBoomCommand extends Command {
	private final Map<ServerWorld, StaticSonicBoomTask> tasks = new HashMap<>();

	@Override
	public void preinit() throws Exception {
		TickEvent.SERVER_POST.register(server -> {
			Set<Map.Entry<ServerWorld, StaticSonicBoomTask>> tasks = new HashSet<>(this.tasks.entrySet());
			this.tasks.clear();

			for (Map.Entry<ServerWorld, StaticSonicBoomTask> task : tasks) {
				if (task.getValue().getStatus() != Task.Status.RUNNING) continue;

				task.getValue().tick(task.getKey(), null, task.getKey().getTime());
				this.tasks.put(task.getKey(), task.getValue());
			}
		});
	}

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
		if (Version.getCurrent().isOlderThan(Version.V1_19)) return;

		dispatcher.register(literalReqOp("sonicboom")
				.executes(ctx -> {
					HitResult hit = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrThrow(), ctx.getSource().getWorld(), ctx.getSource().getEntity() instanceof PlayerEntity ?
							ReachCommand.getReach(ctx.getSource().getPlayer(), false) : 5f, false, true);

					if (hit.getType() != HitResult.Type.ENTITY) {
						sendError(ctx, "You're not looking at an entity.");
						return 0;
					}

					StaticSonicBoomTask task = new StaticSonicBoomTask(ctx.getSource().getEntityOrThrow(), ctx.getSource().getPosition(), ((EntityHitResult) hit).getEntity());
					task.start(ctx.getSource().getWorld(), ctx.getSource().getWorld().getTime());
					tasks.put(ctx.getSource().getWorld(), task);

					return 1;
				}));
	}

	public static class StaticSonicBoomTask extends Task<WardenEntity> {
		private final Vec3d pos;
		private final Entity attacker, target;
		private static final int RUN_TIME = MathHelper.ceil(60.0F);
		private int delay = 34;

		public StaticSonicBoomTask(Entity attacker, Vec3d pos, Entity target) {
			super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN,
					MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleState.REGISTERED,
					MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryModuleState.REGISTERED), RUN_TIME);
			this.pos = pos;
			this.attacker = attacker;
			this.target = target;
		}

		public void start(ServerWorld world, long time) {
			MixinTaskAccessor accessor = (MixinTaskAccessor) this;

			accessor.setStatus(Status.RUNNING);
			accessor.setEndTime(time + (long) RUN_TIME);
			run(world, null, time);
		}

		protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
			return true;
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
			return true;
		}

		protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
			serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
					SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.HOSTILE, 3.0F, 1.0F);
		}

		protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
			if (delay-- != 0) return;

			Vec3d vec3d = pos.add(0.0, 1.6, 0.0);
			Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
			Vec3d vec3d3 = vec3d2.normalize();

			for(int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
				Vec3d vec3d4 = vec3d.add(vec3d3.multiply(i));
				serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
			}

			serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.HOSTILE, 3.0F, 1.0F);
			target.damage(DamageSource.method_43964(attacker), 10.0F);

			double kbRes = target instanceof LivingEntity ? ((LivingEntity) target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) : 0f;
			double vertical = 0.5 * (1.0 - kbRes);
			double horizontal = 2.5 * (1.0 - kbRes);
			target.addVelocity(vec3d3.getX() * horizontal, vec3d3.getY() * vertical, vec3d3.getZ() * horizontal);
		}
	}
}
