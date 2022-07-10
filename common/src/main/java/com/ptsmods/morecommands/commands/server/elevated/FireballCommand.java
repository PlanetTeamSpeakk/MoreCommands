package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireballCommand extends Command {
    public static final Map<LargeFireball, Triple<Vec3, AtomicInteger, Integer>> fireballs = new HashMap<>();
    private static final SimpleCommandExceptionType ONLY_LIVING = new SimpleCommandExceptionType(literalText("Only living entities may run this command.").build());

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("fireball")
                .executes(ctx -> execute(ctx, 4f, 1d, 1))
                .then(argument("power", FloatArgumentType.floatArg(0f))
                        .executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), 1d, 1))
                        .then(argument("speed", DoubleArgumentType.doubleArg(0))
                                .executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), 1))
                                .then(argument("impacts", IntegerArgumentType.integer(0))
                                        .executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), ctx.getArgument("impacts", Integer.class)))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/fireball";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, float power, double speed, int impacts) throws CommandSyntaxException {
        Vec3 velocity0 = MoreCommands.getRotationVector(ctx.getSource().getRotation()).scale(speed*2);
        LivingEntity entity = ctx.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) ctx.getSource().getEntity() : null;
        if (entity == null) throw ONLY_LIVING.create();
        AtomicInteger impactsDone = new AtomicInteger();
        AbstractHurtingProjectile fireball = MoreCommands.isAprilFirst() ? new WitherSkull(ctx.getSource().getLevel(), entity, velocity0.x, velocity0.y, velocity0.z) {
            public void setDeltaMovement(Vec3 velocity) {
                super.setDeltaMovement(velocity0);
            }

            protected void onHit(HitResult result) {
                HitResult.Type type = result.getType();
                if (type == HitResult.Type.ENTITY) this.onHitEntity((EntityHitResult) result);
                else if (type == HitResult.Type.BLOCK) this.onHitBlock((BlockHitResult) result);
                if (!this.level.isClientSide) {
                    this.level.explode(this, this.getX(), this.getY(), this.getZ(), power, true, Explosion.BlockInteraction.DESTROY);
                    if (impactsDone.addAndGet(1) >= impacts) Compat.get().setRemoved(this, 1);
                }
            }
        } : Compat.get().newFireballEntity(ctx.getSource().getLevel(), entity, velocity0.x, velocity0.y, velocity0.z, (int) power);
        fireball.setDeltaMovement(velocity0);

        MixinEntityAccessor accessor = (MixinEntityAccessor) fireball;
        accessor.setXRot_(ctx.getSource().getRotation().x);
        accessor.setYRot_(ctx.getSource().getRotation().y);

        fireball.setPosRaw(ctx.getSource().getPosition().x, ctx.getSource().getPosition().y + (ctx.getSource().getEntity() == null ? 0 : ctx.getSource().getEntity().getEyeHeight(ctx.getSource().getEntity().getPose())), ctx.getSource().getPosition().z);
        fireball.tick();
        ctx.getSource().getLevel().addFreshEntity(fireball);
        return 1;
    }

}
