package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;

public class DefuseCommand extends Command {
	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literalReqOp("defuse").then(argument("range", IntegerArgumentType.integer(1)).executes(ctx -> defuse(ctx, getTntEntities(ctx.getSource(), ctx.getArgument("range", Integer.class)))).
				then(literalReqOp("all").executes(ctx -> defuse(ctx, getTntEntities(ctx.getSource(), -1))))));
	}

	@SuppressWarnings("unchecked")
	private Collection<TntEntity> getTntEntities(ServerCommandSource source, int range) throws CommandSyntaxException {
		return (Collection<TntEntity>) EntityArgumentType.entities().parse(new StringReader("@e[type=tnt" + (range == -1 ? "" : ",distance=.." + range) + "]")).getEntities(source);
	}

	private int defuse(CommandContext<ServerCommandSource> ctx, Collection<TntEntity> entities) {
		for (TntEntity tnt : entities) {
			tnt.kill();
			tnt.getEntityWorld().spawnEntity(new ItemEntity(tnt.getEntityWorld(), tnt.getX(), tnt.getY(), tnt.getZ(), new ItemStack(Registry.ITEM.get(new Identifier("minecraft:tnt")), 1)));
		}
		sendMsg(ctx, SF + "" + entities.size() + " TNT entit" + (entities.size() == 1 ? "y" : "ies") + " " + DF + "ha" + (entities.size() == 1 ? "s" : "ve") + " been killed and " + (entities.size() == 1 ? "a " : "") + "TNT item" + (entities.size() == 1 ? "" : "s") + " ha" + (entities.size() == 1 ? "s" : "ve") + " been spawned in " + (entities.size() == 1 ? "its" : "their") + " place.");
		return entities.size();
	}

}
