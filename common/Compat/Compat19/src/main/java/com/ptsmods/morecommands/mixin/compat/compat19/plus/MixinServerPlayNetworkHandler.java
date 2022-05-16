package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.ptsmods.morecommands.api.IMoreGameRules;
import com.ptsmods.morecommands.api.util.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow public ServerPlayerEntity player;
	@Shadow private @Final MinecraftServer server;

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/encryption/SignedChatMessage;Lnet/minecraft/server/filter/TextStream$Message;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V"), method = "handleMessage")
	public TextStream.Message handleMessage_getChatMessage(TextStream.Message message) {
		return new TextStream.Message(handleFormattings(message.getRaw()), handleFormattings(message.getFiltered()));
	}

	private @Unique String handleFormattings(String msg) {
		if (msg == null || msg.isEmpty()) return msg;

		if (!msg.startsWith("/") && (IMoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), IMoreGameRules.get().doChatColoursRule(), player)
				|| player.hasPermissionLevel(server.getOpPermissionLevel()))) msg = Util.translateFormats(msg);
		return msg;
	}
}
