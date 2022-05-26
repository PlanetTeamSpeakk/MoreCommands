package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.math.MathHelper;

public class RotateCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
        dispatcher.register(cLiteral("rotate")
                .then(cArgument("yaw", FloatArgumentType.floatArg(-180, 180))
                        .then(cArgument("pitch", FloatArgumentType.floatArg(-90, 90))
                                .executes(ctx -> {
                                    getPlayer().changeLookDirection((ctx.getArgument("yaw", Float.class) % 360.0F) / 0.15F,
                                            (MathHelper.clamp(ctx.getArgument("pitch", Float.class), -90.0F, 90.0F) % 360.0F) / 0.15F);
                                    return 1;
                                }))));
    }

    @Override
    public String getDocsPath() {
        return "/client/rotate";
    }
}
