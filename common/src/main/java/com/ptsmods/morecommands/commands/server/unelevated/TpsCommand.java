package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommandsArch;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.RollingAverage;
import com.ptsmods.morecommands.util.TickStatistics;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;

public class TpsCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        if (MoreCommandsArch.isFabricModLoaded("spark")) return; // Spark Profiler is a great mod and we will not be overriding their command.

        TickStatistics tickStatistics = new TickStatistics();
        dispatcher.register(literalReq("tps").executes(ctx -> {
            sendMsg(ctx, "TPS from last 5s, 10s, 1m, 5m, 15m:");
            sendMsg(ctx, String.join(DF + ", ", formatTps(tickStatistics.tps5Sec()), formatTps(tickStatistics.tps10Sec()), formatTps(tickStatistics.tps1Min()),
                    formatTps(tickStatistics.tps5Min()), formatTps(tickStatistics.tps15Min())));
            sendMsg(ctx, "Tick durations (min/med/95%ile/max ms) from last 10s, 1m:");
            sendMsg(ctx, formatTickDuration(tickStatistics.duration10Sec()) + DF + "; " + formatTickDuration(tickStatistics.duration1Min()));
            sendMsg(ctx, literalText("")
                    .append(literalText("Also have a look at ", DS))
                    .append(literalText("Spark ")
                            .formatted(ChatFormatting.DARK_GRAY, ChatFormatting.UNDERLINE))
                    .append(literalText("\u26A1")
                            .formatted(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE, ChatFormatting.BOLD))
                    .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://spark.lucko.me"))));
            return 1;
        }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/tps";
    }

    private String formatTps(double tps) {
        return formatFromFloat((float) tps, 20, 0.8f, 0.9f, true) + (tps > 20 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private String formatTickDuration(RollingAverage average) {
        return String.join(SF + "/", formatTickDuration(average.min()), formatTickDuration(average.median()), formatTickDuration(average.percentile(0.95d)), formatTickDuration(average.max()));
    }

    private String formatTickDuration(double duration) {
        return formatFromFloat((float) (50 - duration), 50, 0.2f, 0, true) + String.format("%.1f", duration);
    }
}
