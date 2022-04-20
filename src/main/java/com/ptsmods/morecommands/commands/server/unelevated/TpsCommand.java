package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.RollingAverage;
import com.ptsmods.morecommands.util.TickStatistics;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class TpsCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        if (!FabricLoader.getInstance().isModLoaded("spark")) { // Spark Profiler is a great mod and we will not be overriding their command.
            TickStatistics tickStatistics = new TickStatistics();
            dispatcher.register(literalReq("tps").executes(ctx -> {
                sendMsg(ctx, "TPS from last 5s, 10s, 1m, 5m, 15m:");
                sendMsg(ctx, String.join(DF + ", ", formatTps(tickStatistics.tps5Sec()), formatTps(tickStatistics.tps10Sec()), formatTps(tickStatistics.tps1Min()), formatTps(tickStatistics.tps5Min()), formatTps(tickStatistics.tps15Min())));
                sendMsg(ctx, "Tick durations (min/med/95%ile/max ms) from last 10s, 1m:");
                sendMsg(ctx, formatTickDuration(tickStatistics.duration10Sec()) + DF + "; " + formatTickDuration(tickStatistics.duration1Min()));
                sendMsg(ctx, new LiteralText("")
                        .append(new LiteralText("Also have a look at ").setStyle(DS))
                        .append(new LiteralText("Spark ").formatted(Formatting.DARK_GRAY, Formatting.UNDERLINE))
                        .append(new LiteralText("\u26A1").formatted(Formatting.YELLOW, Formatting.UNDERLINE, Formatting.BOLD)).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://spark.lucko.me"))));
                return 1;
            }));
        }
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
