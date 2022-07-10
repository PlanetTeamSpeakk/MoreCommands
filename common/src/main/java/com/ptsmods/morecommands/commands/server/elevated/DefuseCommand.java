package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;

public class DefuseCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("defuse")
                .then(argument("range", IntegerArgumentType.integer(1))
                        .executes(ctx -> defuse(ctx, getTntEntities(ctx.getSource(), ctx.getArgument("range", Integer.class)))))
                .then(literalReqOp("all")
                        .executes(ctx -> defuse(ctx, getTntEntities(ctx.getSource(), -1)))));
    }

    @SuppressWarnings("unchecked")
    private Collection<PrimedTnt> getTntEntities(CommandSourceStack source, int range) throws CommandSyntaxException {
        return (Collection<PrimedTnt>) EntityArgument.entities().parse(new StringReader("@e[type=tnt" + (range == -1 ? "" : ",distance=.." + range) + "]")).findEntities(source);
    }

    private int defuse(CommandContext<CommandSourceStack> ctx, Collection<PrimedTnt> entities) {
        for (PrimedTnt tnt : entities) {
            tnt.kill();
            tnt.getCommandSenderWorld().addFreshEntity(new ItemEntity(tnt.getCommandSenderWorld(), tnt.getX(), tnt.getY(), tnt.getZ(), new ItemStack(Registry.ITEM.get(new ResourceLocation("minecraft:tnt")), 1)));
        }
        sendMsg(ctx, SF + "" + entities.size() + " TNT entit" + (entities.size() == 1 ? "y" : "ies") + " " + DF + "ha" + (entities.size() == 1 ? "s" : "ve") + " been killed and " + (entities.size() == 1 ? "a " : "") + "TNT item" + (entities.size() == 1 ? "" : "s") + " ha" + (entities.size() == 1 ? "s" : "ve") + " been spawned in " + (entities.size() == 1 ? "its" : "their") + " place.");
        return entities.size();
    }

    @Override
    public String getDocsPath() {
        return "/elevated/defuse";
    }
}
