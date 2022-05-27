package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.world.ClientWorld;

import java.util.function.Consumer;

public class PweatherCommand extends ClientCommand {
    public static boolean isRaining = false;
    public static float rainGradient = 0f;
    public static float thunderGradient = 0f;
    public static WeatherType pweather = WeatherType.OFF;

    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<ClientCommandSource> cmd = cLiteral("pweather");
        for (WeatherType type : WeatherType.values()) {
            cmd.then(cLiteral(type.name().toLowerCase())
                    .executes(ctx -> {
                        if (type != WeatherType.OFF) {
                            rainGradient = getWorld().getRainGradient(1f);
                            thunderGradient = getWorld().getThunderGradient(1f);
                            isRaining = getWorld().getLevelProperties().isRaining();
                        }
                        pweather = type;
                        pweather.consumer.accept(getWorld());
                        sendMsg(type.msg);
                        if (pweather != WeatherType.OFF) sendMsg("To sync your personal weather with the server again, use " + SF + "/pweather off" + DF + ".");
                        return 1;
                    }));
        }
        dispatcher.register(cmd);
    }

    @Override
    public String getDocsPath() {
        return "/pweather";
    }

    public enum WeatherType {
        OFF("Your personal weather is now synced with the server again.", world -> {
            world.setRainGradient(rainGradient);
            world.setThunderGradient(thunderGradient);
            world.getLevelProperties().setRaining(isRaining);
        }),
        CLEAR("Can't you tell I got news for you? The sun is shining and so are you.", world -> {
            world.setRainGradient(0f);
            world.getLevelProperties().setRaining(false);
        }),
        RAIN("Purple rain, purple raaiiinn.", world -> {
            world.setRainGradient(1f);
            world.getLevelProperties().setRaining(true);
        }),
        THUNDER("I was lightning before the thunder, thun- thunder.", world -> {
            world.setRainGradient(1f);
            world.setThunderGradient(1f);
            world.getLevelProperties().setRaining(true);
        });

        public final String msg;
        public final Consumer<ClientWorld> consumer;

        WeatherType(String msg, Consumer<ClientWorld> consumer) {
            this.msg = msg;
            this.consumer = consumer;
        }
    }
}
