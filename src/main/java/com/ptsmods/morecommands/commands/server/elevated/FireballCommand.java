package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
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

import java.util.concurrent.atomic.AtomicInteger;

public class FireballCommand extends Command {

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("fireball").requires(IS_OP).executes(ctx -> execute(ctx, 4f, 1d, 1))
		.then(argument("power", FloatArgumentType.floatArg(0f)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), 1d, 1))
		.then(argument("speed", DoubleArgumentType.doubleArg(0)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), 1))
		.then(argument("impacts", IntegerArgumentType.integer(0)).executes(ctx -> execute(ctx, ctx.getArgument("power", Float.class), ctx.getArgument("speed", Double.class), ctx.getArgument("impacts", Integer.class)))))));
	}

	private int execute(CommandContext<ServerCommandSource> ctx, float power, double speed, int impacts) throws CommandSyntaxException {
		Vec3d velocity0 = MoreCommands.getRotationVector(ctx.getSource().getRotation()).multiply(speed*2);
		LivingEntity entity = ctx.getSource().getEntity() instanceof LivingEntity ? (LivingEntity) ctx.getSource().getEntity() : null;
		if (entity == null) throw new SimpleCommandExceptionType(new LiteralText("Only living entities may run this command.")).create();
		AtomicInteger impactsDone = new AtomicInteger();
		ExplosiveProjectileEntity fireball = MoreCommands.isAprilFirst() ? new WitherSkullEntity(ctx.getSource().getWorld(), entity, 0.1, 0.1, 0.1) {
			public void setVelocity(Vec3d velocity) {
				super.setVelocity(velocity0);
			}

			protected void onCollision(HitResult result) {
				HitResult.Type type = result.getType();
				if (type == HitResult.Type.ENTITY) this.onEntityHit((EntityHitResult) result);
				else if (type == HitResult.Type.BLOCK) this.onBlockHit((BlockHitResult) result);
				if (!this.world.isClient) {
					this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), power, true, Explosion.DestructionType.DESTROY);
					if (impactsDone.addAndGet(1) >= impacts) Compat.getCompat().setRemoved(this, 1);
				}

			}
		} : new FireballEntity(ctx.getSource().getWorld(), entity, 0.1, 0.1, 0.1) {
			public void setVelocity(Vec3d velocity) {
				super.setVelocity(velocity0);
			}

			protected void onCollision(HitResult result) {
				HitResult.Type type = result.getType();
				if (type == HitResult.Type.ENTITY) this.onEntityHit((EntityHitResult) result);
				else if (type == HitResult.Type.BLOCK) this.onBlockHit((BlockHitResult) result);
				if (!this.world.isClient) {
					this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), power, true, Explosion.DestructionType.DESTROY );
					if (impactsDone.addAndGet(1) >= impacts) Compat.getCompat().setRemoved(this, 1);
				}
			}
		};
		fireball.setVelocity(velocity0);
		Compat.getCompat().setEntityPitch(fireball, ctx.getSource().getRotation().x);
		Compat.getCompat().setEntityYaw(fireball, ctx.getSource().getRotation().y);
		fireball.setPos(ctx.getSource().getPosition().x, ctx.getSource().getPosition().y + (ctx.getSource().getEntity() == null ? 0 : ctx.getSource().getEntity().getEyeY()), ctx.getSource().getPosition().z);
		fireball.tick();
		ctx.getSource().getWorld().spawnEntity(fireball);
		return 1;
	}

}
