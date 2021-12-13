package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.VanishCommand;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	@Inject(at = @At("TAIL"), method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo cbi) {
		MoreCommands.updateFormatting(player.getServer(), 0, null); // Updating from gamerules
		MoreCommands.updateFormatting(player.getServer(), 1, null);
		if (player.getDataTracker().get(DataTrackerHelper.VANISH)) VanishCommand.vanish(player, false);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V")
	public void onPlayerConnect_broadcastChatMessage(PlayerManager thiz, Text msg, MessageType type, UUID id, ClientConnection connection, ServerPlayerEntity player) {
		if (MoreGameRules.checkBooleanWithPerm(thiz.getServer().getWorld(World.OVERWORLD).getGameRules(), MoreGameRules.doJoinMessageRule, player) && !player.getDataTracker().get(DataTrackerHelper.VANISH))
			thiz.broadcast(msg, type, id);
	}

}
