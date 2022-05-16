package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.ArgumentSignatures;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.ChatMessageSigner;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface MixinClientPlayerEntityAccessor {

	@Invoker
	ChatMessageSignature callSignChatMessage(ChatMessageSigner signer, Text message);
	@Invoker
	ArgumentSignatures callSignArguments(ChatMessageSigner signer, ParseResults<CommandSource> parseResults);
}