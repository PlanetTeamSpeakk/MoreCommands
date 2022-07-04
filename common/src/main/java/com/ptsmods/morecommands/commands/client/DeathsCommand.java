package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeathsCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) throws Exception {
        dispatcher.register(cLiteral("deaths")
                .executes(ctx -> {
                    List<Pair<Long, Pair<Identifier, Vec3d>>> deaths = IDeathTracker.get().getDeaths();
                    if (deaths.isEmpty()) sendMsg("You have not died yet.");
                    else deaths.forEach(death -> sendMsg(DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - death.getLeft(), true, false) +
                            " ago (%s) at %s, %s, %s in dimension %s.", coloured(new SimpleDateFormat("HH:mm:ss").format(new Date(death.getLeft()))),
                            coloured((int) death.getRight().getRight().getX()), coloured((int) death.getRight().getRight().getY()), coloured((int) death.getRight().getRight().getZ()), coloured(death.getRight().getLeft())));

                    return deaths.size();
                }));
    }

    @Override
    public String getDocsPath() {
        return "/deaths";
    }
}
