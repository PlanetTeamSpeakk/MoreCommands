package com.ptsmods.morecommands.commands.elevated;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinTaskAccessor;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SonicBoomCommand extends Command {
    private final Map<ServerLevel, StaticSonicBoomTask> tasks = new HashMap<>();

    @Override
    public void preinit() throws Exception {
        TickEvent.SERVER_POST.register(server -> {
            Set<Map.Entry<ServerLevel, StaticSonicBoomTask>> tasks = new HashSet<>(this.tasks.entrySet());
            this.tasks.clear();

            for (Map.Entry<ServerLevel, StaticSonicBoomTask> task : tasks) {
                if (task.getValue().getStatus() != Behavior.Status.RUNNING) continue;

                task.getValue().tickOrStop(task.getKey(), null, task.getKey().getGameTime());
                this.tasks.put(task.getKey(), task.getValue());
            }
        }); // TODO
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        if (Version.getCurrent().isOlderThan(Version.V1_19)) return;

        dispatcher.register(literalReqOp("sonicboom")
                .executes(ctx -> {
                    HitResult hit = MoreCommands.getRayTraceTarget(ctx.getSource().getEntityOrException(), ctx.getSource().getEntity() instanceof Player ?
                            ReachCommand.getReach(ctx.getSource().getPlayerOrException(), false) : 5f, false, true);

                    if (hit.getType() != HitResult.Type.ENTITY) {
                        sendError(ctx, "You're not looking at an entity.");
                        return 0;
                    }

                    StaticSonicBoomTask task = new StaticSonicBoomTask(ctx.getSource().getEntityOrException(), ctx.getSource().getPosition(), ((EntityHitResult) hit).getEntity());
                    task.start(ctx.getSource().getLevel(), ctx.getSource().getLevel().getGameTime());
                    tasks.put(ctx.getSource().getLevel(), task);

                    return 1;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/sonic-boom";
    }

    public static class StaticSonicBoomTask extends Behavior<Warden> {
        private final Vec3 pos;
        private final Entity attacker, target;
        private static final int RUN_TIME = Mth.ceil(60.0F);
        private int delay = 34;

        public StaticSonicBoomTask(Entity attacker, Vec3 pos, Entity target) {
            super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SONIC_BOOM_COOLDOWN,
                    MemoryStatus.VALUE_ABSENT, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryStatus.REGISTERED,
                    MemoryModuleType.SONIC_BOOM_SOUND_DELAY, MemoryStatus.REGISTERED), RUN_TIME);
            this.pos = pos;
            this.attacker = attacker;
            this.target = target;
        }

        public void start(ServerLevel world, long time) {
            MixinTaskAccessor accessor = (MixinTaskAccessor) this;

            accessor.setStatus(Status.RUNNING);
            accessor.setEndTimestamp(time + (long) RUN_TIME);
            run(world, null, time);
        }

        @Override
        protected boolean canStillUse(ServerLevel serverWorld, Warden wardenEntity, long l) {
            return true;
        }

        protected void run(ServerLevel serverWorld, Warden wardenEntity, long l) {
            serverWorld.playSound(null, pos.x(), pos.y(), pos.z(),
                    SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 3.0F, 1.0F);
        }

        @Override
        protected void tick(ServerLevel serverWorld, Warden wardenEntity, long l) {
            if (delay-- != 0) return;

            Vec3 vec3d = pos.add(0.0, 1.6, 0.0);
            Vec3 vec3d2 = target.getEyePosition().subtract(vec3d);
            Vec3 vec3d3 = vec3d2.normalize();

            for(int i = 1; i < Mth.floor(vec3d2.length()) + 7; ++i) {
                Vec3 vec3d4 = vec3d.add(vec3d3.scale(i));
                serverWorld.sendParticles(ParticleTypes.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
            }

            serverWorld.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, 1.0F);
//            target.hurt(DamageSource.sonicBoom(attacker), 10.0F);

            double kbRes = target instanceof LivingEntity ? ((LivingEntity) target).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0f;
            double vertical = 0.5 * (1.0 - kbRes);
            double horizontal = 2.5 * (1.0 - kbRes);
            target.push(vec3d3.x() * horizontal, vec3d3.y() * vertical, vec3d3.z() * horizontal);
        }
    }
}
