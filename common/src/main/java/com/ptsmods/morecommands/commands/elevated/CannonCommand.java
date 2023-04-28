package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.CustomPrimedTnt;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.item.PrimedTnt;
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
        PrimedTnt tnt = new CustomPrimedTnt(world, pos.x, pos.y, pos.z, null, power, fuse);
        tnt.setDeltaMovement(rotation.scale(motionMultiplier));
        world.addFreshEntity(tnt);
        return (int) power;
    }
}
