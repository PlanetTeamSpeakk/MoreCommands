package com.ptsmods.morecommands.mixin.compat.compat19.plus;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.arguments.CompatArgumentType;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundCommandsPacket.class)
public class MixinClientboundCommandsPacket {
    private static final @Unique SuggestionProvider<?> emptyProvider = (context, builder) -> builder.buildFuture();

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/tree/ArgumentCommandNode;getCustomSuggestions()Lcom/mojang/brigadier/suggestion/SuggestionProvider;"), method = "createEntry")
    private static SuggestionProvider<?> createEntry_getCustomSuggestions(ArgumentCommandNode<?, ?> node) {
        return IMoreCommands.get().isServerOnly() && node.getType() instanceof CompatArgumentType<?, ?, ?> ?
                emptyProvider : node.getCustomSuggestions();
    }
}
