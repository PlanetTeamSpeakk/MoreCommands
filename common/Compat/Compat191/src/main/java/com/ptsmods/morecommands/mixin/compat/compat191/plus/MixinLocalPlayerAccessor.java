package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface MixinLocalPlayerAccessor {

    @Invoker MessageSignature callSignMessage(MessageSigner signer, ChatMessageContent chatMessageContent, LastSeenMessages lastSeenMessages);
    @Invoker ArgumentSignatures callSignCommandArguments(MessageSigner messageSigner, ParseResults<SharedSuggestionProvider> parseResults, @Nullable Component component, LastSeenMessages lastSeenMessages);
    @Invoker ChatMessageContent callBuildSignedContent(String string, @Nullable Component component);
}
