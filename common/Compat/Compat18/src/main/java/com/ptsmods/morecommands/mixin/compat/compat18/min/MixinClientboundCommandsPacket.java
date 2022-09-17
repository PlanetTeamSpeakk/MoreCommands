package com.ptsmods.morecommands.mixin.compat.compat18.min;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundCommandsPacket.class)
public class MixinClientboundCommandsPacket {
    private static final @Unique SuggestionProvider<?> emptyProvider = (context, builder) -> builder.buildFuture();

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/tree/ArgumentCommandNode;getCustomSuggestions()Lcom/mojang/brigadier/suggestion/SuggestionProvider;"), method = "writeNode")
    private static SuggestionProvider<?> writeNode_getCustomSuggestions(ArgumentCommandNode<?, ?> node) {
        // If the node has a CompatArgumentType argument and we're in server-only mode, we tell the client this node has custom suggestions.
        return node.getCustomSuggestions() == null && node.getType() instanceof CompatArgumentType<?, ?, ?> && IMoreCommands.get().isServerOnly() ?
                emptyProvider : node.getCustomSuggestions();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypes;serialize(Lnet/minecraft/network/FriendlyByteBuf;Lcom/mojang/brigadier/arguments/ArgumentType;)V"), method = "writeNode")
    private static <T extends ArgumentType<?>> void writeNode_serialize(FriendlyByteBuf friendlyByteBuf, T argumentType) {
        ArgumentTypes.serialize(friendlyByteBuf, IMoreCommands.get().isServerOnly() && argumentType instanceof CompatArgumentType<?, ?, ?> ?
                ((CompatArgumentType<?, ?, ?>) argumentType).toVanillaArgumentType() : argumentType);
    }
}
