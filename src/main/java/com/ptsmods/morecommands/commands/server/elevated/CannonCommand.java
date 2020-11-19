package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.TntEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CannonCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("cannon").requires(IS_OP).executes(ctx -> fire(ctx.getSource(), 4, 1.5F, 80))
        .then(argument("power", FloatArgumentType.floatArg(0F)).executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), 1.5F, 80))
        .then(argument("motionMultiplier", FloatArgumentType.floatArg(0F)).executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), ctx.getArgument("motionMultiplier", Float.class), 80))
        .then(argument("fuse", IntegerArgumentType.integer(0)).executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), ctx.getArgument("motionMultiplier", Float.class), ctx.getArgument("fuse", Integer.class)))))));
    }

    private int fire(ServerCommandSource source, float power, float motionMultiplier, int fuse) throws CommandSyntaxException {
        return fire(source.getWorld(), source.getEntityOrThrow().getPos().add(0, source.getEntity().getEyeHeight(source.getEntity().getPose()), 0), source.getEntity().getRotationVec(1F), power, motionMultiplier, fuse);
    }

    private int fire(World world, Vec3d pos, Vec3d rotation, float power, float motionMultiplier, int fuse) {
        TntEntity tnt = new TntEntity(world, pos.x, pos.y, pos.z, null) {
            private int fuseTimer = fuse;

            @Override
            public void tick() {
                if (!this.hasNoGravity())
                    this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));

                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.98D));
                if (this.onGround) this.setVelocity(this.getVelocity().multiply(0.7D, -0.5D, 0.7D));

                --this.fuseTimer;
                if (this.fuseTimer <= 0) {
                    this.remove();
                    if (!world.isClient)
                        world.createExplosion(this, this.getX(), this.getBodyY(0.0625D), this.getZ(), power, Explosion.DestructionType.BREAK);
                } else {
                    this.updateWaterState();
                    if (world.isClient)
                        world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        };
        tnt.setVelocity(rotation.multiply(motionMultiplier));
        world.spawnEntity(tnt);
        return (int) power;
    }

}
