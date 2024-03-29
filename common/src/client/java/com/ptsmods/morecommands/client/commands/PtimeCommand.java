package com.ptsmods.morecommands.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.arguments.TimeArgumentType;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class PtimeCommand extends ClientCommand {
    private static int time = -1;
    private static boolean fixed = false;
    private static int serverTime = -1;

    public static boolean isEnabled() {
        return time != -1;
    }

    public static void setServerTime(long time) {
        serverTime = (int) time;
    }

    public void preinit() {
        ClientTickEvent.CLIENT_LEVEL_PRE.register(world -> {
            if (isEnabled()) {
                world.setDayTime(fixed ? time : (time = (time+1) % 24000));
                serverTime++;
            }
        });
    }

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        dispatcher.register(cLiteral("ptime")
                .then(cLiteral("off")
                        .executes(ctx -> {
                            time = -1;
                            fixed = false;
                            getWorld().setDayTime(serverTime);
                            serverTime = -1;
                            sendMsg("Time is now synchronised with the server time.");
                            return 1;
                        }))
                .then(cArgument("time", TimeArgumentType.time())
                        .executes(ctx -> {
                            TimeArgumentType.WorldTime worldTime = TimeArgumentType.getTime(ctx, "time");
                            if (time != -1) setServerTime(getWorld().getDayTime());
                            time = worldTime.getTime();
                            fixed = worldTime.isFixed();
                            sendMsg("Your personal time has been " + (fixed ? "fixed" : "set") + ".");
                            return 1;
                        })));
    }

    @Override
    public String getDocsPath() {
        return "/ptime";
    }
}
