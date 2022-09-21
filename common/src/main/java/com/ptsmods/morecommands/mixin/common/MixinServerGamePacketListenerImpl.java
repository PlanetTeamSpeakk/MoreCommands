package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream; map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"), method = "handleSignUpdate")
    public Stream<String> onSignUpdate_map(Stream<String> stream, Function<String, String> func) {
        return stream.map(s -> {
            ServerGamePacketListenerImpl thiz = ReflectionHelper.cast(this);
            if (MoreGameRules.get().checkBooleanWithPerm(thiz.player.getCommandSenderWorld().getGameRules(), MoreGameRules.get().doSignColoursRule(), thiz.player)
                    || player.hasPermissions(server.getOperatorUserPermissionLevel())) s = Util.translateFormats(s);
            else s = ChatFormatting.stripFormatting(s);
            return s;
        });
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; length()I", remap = false), method = "handleRenameItem")
    public int onRenameItem_length(String string) {
        return (MoreGameRules.get().checkBooleanWithPerm(player.getCommandSenderWorld().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? ChatFormatting.stripFormatting(MoreCommands.translateFormattings(string)) : string).length();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;setItemName(Ljava/lang/String;)V"), method = "handleRenameItem")
    public void onRenameItem_setNewName(AnvilMenu anvil, String name) {
        anvil.setItemName(MoreGameRules.get().checkBooleanWithPerm(player.getCommandSenderWorld().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? MoreCommands.translateFormattings(name) : name);
    }
}
