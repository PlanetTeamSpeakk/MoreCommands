package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class CannonCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("cannon")
                .executes(ctx -> fire(ctx.getSource(), 4, 1.5F, 80))
                .then(argument("power", FloatArgumentType.floatArg(0F))
                        .executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), 1.5F, 80))
                        .then(argument("motionMultiplier", FloatArgumentType.floatArg(0F))
                                .executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), ctx.getArgument("motionMultiplier", Float.class), 80))
                                .then(argument("fuse", IntegerArgumentType.integer(0))
                                        .executes(ctx -> fire(ctx.getSource(), ctx.getArgument("power", Float.class), ctx.getArgument("motionMultiplier", Float.class), ctx.getArgument("fuse", Integer.class)))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/cannon";
    }

    private int fire(CommandSourceStack source, float power, float motionMultiplier, int fuse) throws CommandSyntaxException {
        return fire(source.getLevel(), source.getEntityOrException().position().add(0, Objects.requireNonNull(source.getEntity()).getEyeHeight(source.getEntity().getPose()), 0),
                source.getEntity().getViewVector(1F), power, motionMultiplier, fuse);
    }

    private int fire(Level world, Vec3 pos, Vec3 rotation, float power, float motionMultiplier, int fuse) {
        PrimedTnt tnt = new PrimedTnt(world, pos.x, pos.y, pos.z, null) {
            private int fuseTimer = fuse;

            @Override
            public void tick() {
                if (!this.isNoGravity())
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));

                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
                if (this.onGround) this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));

                --this.fuseTimer;
                if (this.fuseTimer <= 0) {
                    setRemoved(RemovalReason.KILLED);
                    if (!level.isClientSide)
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), power, Explosion.BlockInteraction.BREAK);
                } else {
                    this.updateInWaterStateAndDoFluidPushing();
                    if (level.isClientSide)
                        level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
            }
        };
        tnt.setDeltaMovement(rotation.scale(motionMultiplier));
        world.addFreshEntity(tnt);
        return (int) power;
    }

}
