package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class WhoIsCommand extends Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("whois")
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                            sendMsg(ctx, "Info for player " + SF + IMoreCommands.get().textToString(player.getDisplayName(), null, true));
                            sendMsg(ctx, "UUID: " + SF + player.getStringUUID());
                            sendMsg(ctx, "World: " + SF + player.getCommandSenderWorld().dimension().location().toString());
                            sendMsg(ctx, "Coords: " + SF + Compat.get().blockPosition(player).getX() + DF + ", " +
                                    SF + Compat.get().blockPosition(player).getY() + DF + ", " + SF + Compat.get().blockPosition(player).getZ());
                            sendMsg(ctx, "Rotation: " + SF + Mth.wrapDegrees(((MixinEntityAccessor) player).getYRot_()) + DF + ", " + SF + Mth.wrapDegrees(((MixinEntityAccessor) player).getXRot_()));
                            sendMsg(ctx, "Health: " + formatFromFloat(player.getHealth(), player.getMaxHealth(), .5f, .8f, false));
                            sendMsg(ctx, "Food: " + formatFromFloat(player.getFoodData().getFoodLevel(), 20f, .5f, .8f, false));
                            sendMsg(ctx, "Saturation: " + SF + player.getFoodData().getSaturationLevel());
                            sendMsg(ctx, "IP: " + SF + player.getIpAddress());
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/who-is";
    }
}
