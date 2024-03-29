package com.ptsmods.morecommands.mixin.compat.compat190;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface MixinLocalPlayerAccessor {

    @Invoker MessageSignature callSignMessage(MessageSigner signer, Component message);
    @Invoker ArgumentSignatures callSignCommandArguments(MessageSigner signer, ParseResults<SharedSuggestionProvider> parseResults, Component preview);
}
