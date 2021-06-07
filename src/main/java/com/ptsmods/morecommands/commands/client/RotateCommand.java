package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class RotateCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
        dispatcher.register(cLiteral("rotate").then(cArgument("yaw", FloatArgumentType.floatArg(-180, 180)).then(cArgument("pitch", FloatArgumentType.floatArg(-90, 90)).executes(ctx -> {
            getPlayer().setYaw(ctx.getArgument("yaw", Float.class) + 180);
            getPlayer().setPitch(ctx.getArgument("pitch", Float.class) + 90);
            return 1; // TODO fix me lol
        }))));
    }
}
