package com.ptsmods.morecommands.forge.compat;

import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.util.compat.ForgeCompatAdapter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

public class ForgeCompat18 extends ForgeCompatAdapter {

    @Override
    public boolean shouldRegisterListeners() {
        return Version.getCurrent().isOlderThanOrEqual(Version.V1_18);
    }

    @Override
    public void registerListeners() {
        MinecraftForge.EVENT_BUS.addListener(new Listener()::onCreateFluidSource);
    }

    private static class Listener {
        private void onCreateFluidSource(BlockEvent.CreateFluidSourceEvent event) {
            if (event.getWorld() instanceof Level && ((Level) event.getWorld()).getGameRules().getBoolean(IMoreGameRules.get().fluidsInfiniteRule()))
                event.setResult(Event.Result.ALLOW);
        }
    }
}
