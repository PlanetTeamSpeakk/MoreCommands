package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/network/protocol/game/ClientboundCommandsPacket$ArgumentNodeStub")
public class MixinClientboundCommandsPacketArgumentNodeStub {
    private static final @Unique SuggestionProvider<?> emptyProvider = (context, builder) -> builder.buildFuture();

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/ArgumentTypeInfos;" +
            "unpack(Lcom/mojang/brigadier/arguments/ArgumentType;)Lnet/minecraft/commands/synchronization/ArgumentTypeInfo$Template;"),
            method = "<init>(Lcom/mojang/brigadier/tree/ArgumentCommandNode;)V")
    private static ArgumentType<?> init_unpack(ArgumentType<?> argumentType) {
        return IMoreCommands.get().isServerOnly() && argumentType instanceof CompatArgumentType<?, ?, ?> ?
                ((CompatArgumentType<?, ?, ?>) argumentType).toVanillaArgumentType() : argumentType;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/tree/ArgumentCommandNode;getCustomSuggestions()Lcom/mojang/brigadier/suggestion/SuggestionProvider;", remap = false),
            method = "<init>(Lcom/mojang/brigadier/tree/ArgumentCommandNode;)V")
    private static SuggestionProvider<?> init_getSuggestionsId(ArgumentCommandNode<?, ?> node) {
        return IMoreCommands.get().isServerOnly() && node.getType() instanceof CompatArgumentType<?, ?, ?> ?
                emptyProvider : node.getCustomSuggestions();
    }
}
