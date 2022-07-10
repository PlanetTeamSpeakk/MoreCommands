package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IDeathTracker;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;

public class DeathsCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) throws Exception {
        dispatcher.register(cLiteral("deaths")
                .executes(ctx -> {
                    List<Tuple<Long, Tuple<ResourceLocation, Vec3>>> deaths = IDeathTracker.get().getDeaths();
                    if (deaths.isEmpty()) sendMsg("You have not died yet.");
                    else deaths.forEach(death -> sendMsg(DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - death.getA(), true, false) +
                            " ago (%s) at %s, %s, %s in dimension %s.", coloured(new SimpleDateFormat("HH:mm:ss").format(new Date(death.getA()))),
                            coloured((int) death.getB().getB().x()), coloured((int) death.getB().getB().y()), coloured((int) death.getB().getB().z()), coloured(death.getB().getA())));

                    return deaths.size();
                }));
    }

    @Override
    public String getDocsPath() {
        return "/deaths";
    }
}
