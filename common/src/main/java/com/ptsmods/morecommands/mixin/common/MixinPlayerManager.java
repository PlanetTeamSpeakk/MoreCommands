package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IDataTrackerHelper;
import com.ptsmods.morecommands.commands.elevated.VanishCommand;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    public void onPlayerConnect(Connection connection, ServerPlayer player, CallbackInfo cbi) {
        MoreCommands.updateFormatting(player.getServer(), 0, null); // Updating from gamerules
        MoreCommands.updateFormatting(player.getServer(), 1, null);
        if (player.getEntityData().get(IDataTrackerHelper.get().vanish())) VanishCommand.vanish(player, false);
    }
}
