package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ReachCommand extends Command {
    private static final UUID morecommandsModifier = UUID.nameUUIDFromBytes("morecommands".getBytes(StandardCharsets.UTF_8));

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("reach")
                .executes(ctx -> {
                    double reach = ctx.getSource().getPlayerOrException().getAttributeValue(getReachAttribute());
                    double base = ctx.getSource().getPlayerOrException().getAttributeBaseValue(getReachAttribute());
                    sendMsg(ctx, "Your reach is currently " + SF + reach + DF + (base != reach ? " (" + SF + base + " base" + DF + ")" : "") + ".");
                    return (int) reach;
                })
                .then(argument("reach", DoubleArgumentType.doubleArg(1d, 160d))
                        .executes(ctx -> {
                            double oldReach = ctx.getSource().getPlayerOrException().getAttributeBaseValue(getReachAttribute());
                            double reach = ctx.getArgument("reach", Double.class);
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            Objects.requireNonNull(player.getAttribute(getReachAttribute())).setBaseValue(reach);
                            addModifier("pehkui:reach", player, reach);
                            addModifier("reach-entity-attributes:reach", player, reach);
                            addModifier("forge:reach_distance", player, reach);
                            addModifier("forge:entity_reach", player, reach);
                            addModifier("forge:attack_range", player, reach);
                            sendMsg(ctx, "Your reach has been set from " + SF + oldReach + DF + " to " + SF + reach + DF + ".");
                            return (int) reach;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/reach";
    }

    @Override
    public boolean registerInServerOnly() {
        return false;
    }

    private void addModifier(String id, ServerPlayer player, double reach) {
        Optional.ofNullable(Compat.get().<Attribute>getBuiltInRegistry("attribute").get(new ResourceLocation(id)))
                .map(player::getAttribute)
                .ifPresent(atr -> {
                    atr.removePermanentModifier(morecommandsModifier);
                    atr.addPermanentModifier(new AttributeModifier(morecommandsModifier, "MoreCommands Modifier", reach / 4.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                });
    }

    public static double getReach(Player player, boolean squared) {
        return player.getAttributes().hasAttribute(getReachAttribute()) ? Math.pow(player.getAttributeValue(getReachAttribute()) +
                (player instanceof ServerPlayer && squared ? 1.5d : 0), squared ? 2 : 1) : 4.5d * (squared ? 4.5d : 1);
    }
    
    public static Attribute getReachAttribute() {
        return IMoreCommands.get().getReachAttribute().get();
    }
}
