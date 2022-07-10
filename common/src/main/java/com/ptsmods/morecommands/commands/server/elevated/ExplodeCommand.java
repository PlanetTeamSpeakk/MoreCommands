package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.arguments.EnumArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplodeCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("explode")
                .executes(ctx -> execute(ctx, null, 4f, false, Explosion.BlockInteraction.BREAK, true))
                .then(argument("pos", Vec3Argument.vec3())
                        .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), 4f, false, Explosion.BlockInteraction.BREAK, true))
                        .then(argument("power", FloatArgumentType.floatArg(0f))
                                .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class), false, Explosion.BlockInteraction.BREAK, true))
                                .then(argument("fire", BoolArgumentType.bool())
                                        .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                ctx.getArgument("fire", Boolean.class), Explosion.BlockInteraction.BREAK, true))
                                        .then(argument("destruct", EnumArgumentType.enumType(Explosion.BlockInteraction.class, Explosion.BlockInteraction.values()))
                                                .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                        ctx.getArgument("fire", Boolean.class), EnumArgumentType.getEnum(ctx, "destruct"), true))
                                                .then(argument("launch", BoolArgumentType.bool())
                                                        .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                                ctx.getArgument("fire", Boolean.class), EnumArgumentType.getEnum(ctx, "destruct"), ctx.getArgument("launch", Boolean.class)))))))));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/explode";
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Vec3 pos, float power, boolean fire, Explosion.BlockInteraction destruct, boolean launch) {
        if (pos == null) pos = ctx.getSource().getPosition();
        Level world = ctx.getSource().getLevel();
        Explosion explosion = world.explode(ctx.getSource().getEntity() != null && !launch ? ctx.getSource().getEntity() : null, pos.x(), pos.y(), pos.z(), power, fire, destruct);
        explosion.finalizeExplosion(true);
        return explosion.getToBlow().size();
    }
}
