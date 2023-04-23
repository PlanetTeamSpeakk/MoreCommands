package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class PweatherCommand extends ClientCommand {
    public static boolean isRaining = false;
    public static float rainGradient = 0f;
    public static float thunderGradient = 0f;
    public static WeatherType pweather = WeatherType.OFF;

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        LiteralArgumentBuilder<ClientSuggestionProvider> cmd = cLiteral("pweather");
        for (WeatherType type : WeatherType.values()) {
            cmd.then(cLiteral(type.name().toLowerCase())
                    .executes(ctx -> {
                        if (type != WeatherType.OFF) {
                            rainGradient = getWorld().getRainLevel(1f);
                            thunderGradient = getWorld().getThunderLevel(1f);
                            isRaining = getWorld().getLevelData().isRaining();
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
            world.setRainLevel(rainGradient);
            world.setThunderLevel(thunderGradient);
            world.getLevelData().setRaining(isRaining);
        }),
        CLEAR("Can't you tell I got news for you? The sun is shining and so are you.", world -> {
            world.setRainLevel(0f);
            world.getLevelData().setRaining(false);
        }),
        RAIN("Purple rain, purple raaiiinn.", world -> {
            world.setRainLevel(1f);
            world.getLevelData().setRaining(true);
        }),
        THUNDER("I was lightning before the thunder, thun- thunder.", world -> {
            world.setRainLevel(1f);
            world.setThunderLevel(1f);
            world.getLevelData().setRaining(true);
        });

        public final String msg;
        public final Consumer<ClientLevel> consumer;

        WeatherType(String msg, Consumer<ClientLevel> consumer) {
            this.msg = msg;
            this.consumer = consumer;
        }
    }
}
