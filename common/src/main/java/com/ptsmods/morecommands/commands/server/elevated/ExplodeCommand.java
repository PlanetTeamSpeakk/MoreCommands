package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.arguments.EnumArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ExplodeCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReqOp("explode").executes(ctx -> execute(ctx, null, 4f, false, Explosion.DestructionType.BREAK, true))
        .then(argument("pos", Vec3ArgumentType.vec3())
                .executes(ctx -> execute(ctx, Vec3ArgumentType.getVec3(ctx, "pos"), 4f, false, Explosion.DestructionType.BREAK, true))
                .then(argument("power", FloatArgumentType.floatArg(0f))
                        .executes(ctx -> execute(ctx, Vec3ArgumentType.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class), false, Explosion.DestructionType.BREAK, true))
                        .then(argument("fire", BoolArgumentType.bool())
                                .executes(ctx -> execute(ctx, Vec3ArgumentType.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                        ctx.getArgument("fire", Boolean.class), Explosion.DestructionType.BREAK, true))
                                .then(argument("destruct", EnumArgumentType.enumType(Explosion.DestructionType.class, Explosion.DestructionType.values()))
                                        .executes(ctx -> execute(ctx, Vec3ArgumentType.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                ctx.getArgument("fire", Boolean.class), EnumArgumentType.getEnum(ctx, "destruct"), true))
                                        .then(argument("launch", BoolArgumentType.bool())
                                                .executes(ctx -> execute(ctx, Vec3ArgumentType.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                        ctx.getArgument("fire", Boolean.class), EnumArgumentType.getEnum(ctx, "destruct"), ctx.getArgument("launch", Boolean.class)))))))));
    }

    private int execute(CommandContext<ServerCommandSource> ctx, Vec3d pos, float power, boolean fire, Explosion.DestructionType destruct, boolean launch) {
        if (pos == null) pos = ctx.getSource().getPosition();
        World world = ctx.getSource().getWorld();
        Explosion explosion = world.createExplosion(ctx.getSource().getEntity() != null && !launch ? ctx.getSource().getEntity() : null, pos.getX(), pos.getY(), pos.getZ(), power, fire, destruct);
        explosion.affectWorld(true);
        return explosion.getAffectedBlocks().size();
    }
}
