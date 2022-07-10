package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerPlayNetworkHandler {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayer player;

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream; map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"), method = "handleSignUpdate")
    public Stream<String> onSignUpdate_map(Stream<String> stream, Function<String, String> func) {
        return stream.map(s -> {
            ServerGamePacketListenerImpl thiz = ReflectionHelper.cast(this);
            if (MoreGameRules.get().checkBooleanWithPerm(thiz.player.getLevel().getGameRules(), MoreGameRules.get().doSignColoursRule(), thiz.player)
                    || player.hasPermissions(server.getOperatorUserPermissionLevel())) s = Util.translateFormats(s);
            else s = ChatFormatting.stripFormatting(s);
            return s;
        });
    }

    // TODO
    @Group(name = "bookUpdate1171Compat", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;getString(I)Ljava/lang/String;"), method = "handleEditBook")
    public String onBookUpdate_getString(ListTag list, int index) {
        ServerGamePacketListenerImpl thiz = ReflectionHelper.cast(this);
        String s = list.getString(index);
        if (MoreGameRules.get().checkBooleanWithPerm(thiz.player.getLevel().getGameRules(), MoreGameRules.get().doBookColoursRule(), thiz.player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel())) s = Util.translateFormats(s);
        return s;
    }

    @Group(name = "bookUpdate1171Compat", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;", remap = false), method = "handleEditBook")
    public Stream<String> onBookUpdate_limit(Stream<String> stream, long maxSize) {
        return MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doBookColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? stream.limit(maxSize).map(Util::translateFormats) : stream.limit(maxSize);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; length()I", remap = false), method = "handleRenameItem")
    public int onRenameItem_length(String string) {
        return (MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? ChatFormatting.stripFormatting(MoreCommands.translateFormattings(string)) : string).length();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;setItemName(Ljava/lang/String;)V"), method = "handleRenameItem")
    public void onRenameItem_setNewName(AnvilMenu anvil, String name) {
        anvil.setItemName(MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()) ? MoreCommands.translateFormattings(name) : name);
    }
}
