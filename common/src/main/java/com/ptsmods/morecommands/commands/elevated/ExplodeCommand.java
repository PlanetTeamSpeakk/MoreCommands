package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.arguments.EnumArgumentType;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class ExplodeCommand extends Command {
    private static final Explosion.BlockInteraction interaction = Explosion.BlockInteraction.values()[2]; // They changed the name.

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("explode")
                .executes(ctx -> execute(ctx, null, 4f, false, interaction, true))
                .then(argument("pos", Vec3Argument.vec3())
                        .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), 4f, false, interaction, true))
                        .then(argument("power", FloatArgumentType.floatArg(0f))
                                .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class), false, interaction, true))
                                .then(argument("fire", BoolArgumentType.bool())
                                        .executes(ctx -> execute(ctx, Vec3Argument.getVec3(ctx, "pos"), ctx.getArgument("power", Float.class),
                                                ctx.getArgument("fire", Boolean.class), interaction, true))
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

    private int execute(CommandContext<CommandSourceStack> ctx, Vec3 pos, float power, boolean fire, Explosion.BlockInteraction interaction, boolean launch) {
        if (pos == null) pos = ctx.getSource().getPosition();

        return Compat.get().explode(ctx.getSource().getLevel(), ctx.getSource().getEntity() != null && !launch ? ctx.getSource().getEntity() : null,
                pos.x, pos.y, pos.z, power, fire, interaction).getToBlow().size();
    }
}
