package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(ClientPlayerEntity.class)
public interface MixinClientPlayerEntityAccessor {

    @Invoker
    MessageSignature callSignChatMessage(ChatMessageSigner signer, Text message);
    @Invoker
    ArgumentSignatureDataMap callSignArguments(ChatMessageSigner signer, ParseResults<CommandSource> parseResults, @Nullable Text preview);
}
