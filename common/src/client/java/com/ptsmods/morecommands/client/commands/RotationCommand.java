package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.util.Mth;

public class RotationCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        dispatcher.register(cLiteral("rotation")
                .then(cLiteral("set")
                        .then(cLiteral("yaw")
                                .then(cArgument("yaw", FloatArgumentType.floatArg(-180, 180))
                                        .executes(ctx -> runSuccess(() -> getPlayer().setYRot(ctx.getArgument("yaw", Float.class))))))
                        .then(cLiteral("pitch")
                                .then(cArgument("pitch", FloatArgumentType.floatArg(-180, 180))
                                        .executes(ctx -> runSuccess(() -> getPlayer().setXRot(ctx.getArgument("pitch", Float.class)))))))
                .then(cLiteral("add")
                        .then(cArgument("yaw", FloatArgumentType.floatArg(-180, 180))
                                .then(cArgument("pitch", FloatArgumentType.floatArg(-90, 90))
                                        .executes(ctx -> {
                                            getPlayer().turn((ctx.getArgument("yaw", Float.class) % 360.0F) / 0.15F,
                                                    (Mth.clamp(ctx.getArgument("pitch", Float.class), -90.0F, 90.0F) % 360.0F) / 0.15F);
                                            return 1;
                                        })))));
    }

    @Override
    public String getDocsPath() {
        return "/rotation";
    }
}
