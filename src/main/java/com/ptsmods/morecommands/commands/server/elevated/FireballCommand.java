package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.util.CompatHolder;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.compat.MixinEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FireballCommand extends Command {
	public static final Map<FireballEntity, Triple<Vec3d, AtomicInteger, Integer>> fireballs = new HashMap<>();

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("fireball").executes(ctx -> execute(ctx, 4f, 1d, 1))
		.then(argument("power", FloatArgumentType.floatArg(0f)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), 1d, 1))
		.then(argument("speed", DoubleArgumentType.doubleArg(0)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), 1))
		.then(argument("impacts", IntegerArgumentType.integer(0)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), ctx.getArgument("impacts", Integer.class)))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, float power, double speed, int impacts) throws CommandSyntaxException {
		Vec3d velocity0 = MoreCommands.getRotationVector(ctx.getSource().getRotation()).multiply(speed*2);
		LivingEntity entity = ctx.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) ctx.getSource().getEntity() : null;
		if (entity == null) throw new SimpleCommandExceptionType(new LiteralText("Only living entities may run this command.")).create();
		AtomicInteger impactsDone = new AtomicInteger();
		ExplosiveProjectileEntity fireball = MoreCommands.isAprilFirst() ? new WitherSkullEntity(ctx.getSource().getWorld(), entity, velocity0.x, velocity0.y, velocity0.z) {
			public void setVelocity(Vec3d velocity) {
				super.setVelocity(velocity0);
			}

			protected void onCollision(HitResult result) {
				HitResult.Type type = result.getType();
				if (type == HitResult.Type.ENTITY) this.onEntityHit((EntityHitResult) result);
				else if (type == HitResult.Type.BLOCK) this.onBlockHit((BlockHitResult) result);
				if (!this.world.isClient) {
					this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), power, true, Explosion.DestructionType.DESTROY);
					if (impactsDone.addAndGet(1) >= impacts) CompatHolder.getCompat().setRemoved(this, 1);
				}
			}
		} : CompatHolder.getCompat().newFireballEntity(ctx.getSource().getWorld(), entity, velocity0.x, velocity0.y, velocity0.z, (int) power);
		fireball.setVelocity(velocity0);

        MixinEntityAccessor accessor = (MixinEntityAccessor) fireball;
        accessor.setPitch_(ctx.getSource().getRotation().x);
        accessor.setYaw_(ctx.getSource().getRotation().y);

		fireball.setPos(ctx.getSource().getPosition().x, ctx.getSource().getPosition().y + (ctx.getSource().getEntity() == null ? 0 : ctx.getSource().getEntity().getEyeHeight(ctx.getSource().getEntity().getPose())), ctx.getSource().getPosition().z);
		fireball.tick();
		ctx.getSource().getWorld().spawnEntity(fireball);
		return 1;
	}

}
