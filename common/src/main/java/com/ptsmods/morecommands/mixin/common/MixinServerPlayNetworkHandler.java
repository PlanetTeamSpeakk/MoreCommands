package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
    @Shadow @Final private MinecraftServer server;
    @Shadow public ServerPlayerEntity player;

//    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onDisconnected(Lnet/minecraft/text/Text;)V")
//    public void onDisconnected_broadcastChatMessage(PlayerManager playerManager, Text msg, MessageType type, UUID id) {
//        if (MoreGameRules.get().checkBooleanWithPerm(Objects.requireNonNull(playerManager.getServer().getWorld(World.OVERWORLD)).getGameRules(), MoreGameRules.get().doJoinMessageRule(), player)
//                && !player.getDataTracker().get(DataTrackerHelper.VANISH)) Compat.get().broadcast(playerManager, type, msg); // TODO
//    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream; map(Ljava/util/function/Function;)Ljava/util/stream/Stream;"), method = "onUpdateSign(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;)V")
    public Stream<String> onSignUpdate_map(Stream<String> stream, Function<String, String> func) {
        return stream.map(s -> {
            ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
            if (MoreGameRules.get().checkBooleanWithPerm(thiz.player.getWorld().getGameRules(), MoreGameRules.get().doSignColoursRule(), thiz.player)
                    || player.hasPermissionLevel(server.getOpPermissionLevel())) s = Util.translateFormats(s);
            else s = Formatting.strip(s);
            return s;
        });
    }

    @Group(name = "bookUpdate1171Compat", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtList;getString(I)Ljava/lang/String;"), method = "onBookUpdate(Lnet/minecraft/network/packet/c2s/play/BookUpdateC2SPacket;)V")
    public String onBookUpdate_getString(NbtList list, int index) {
        ServerPlayNetworkHandler thiz = ReflectionHelper.cast(this);
        String s = list.getString(index);
        if (MoreGameRules.get().checkBooleanWithPerm(thiz.player.getWorld().getGameRules(), MoreGameRules.get().doBookColoursRule(), thiz.player)
                || player.hasPermissionLevel(server.getOpPermissionLevel())) s = Util.translateFormats(s);
        return s;
    }

    @Group(name = "bookUpdate1171Compat", min = 1, max = 1)
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;", remap = false), method = "onBookUpdate")
    public Stream<String> onBookUpdate_limit(Stream<String> stream, long maxSize) {
        return MoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.get().doBookColoursRule(), player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()) ? stream.limit(maxSize).map(Util::translateFormats) : stream.limit(maxSize);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/String; length()I", remap = false), method = "onRenameItem(Lnet/minecraft/network/packet/c2s/play/RenameItemC2SPacket;)V")
    public int onRenameItem_length(String string) {
        return (MoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()) ? Formatting.strip(MoreCommands.translateFormattings(string)) : string).length();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler; setNewItemName(Ljava/lang/String;)V"), method = "onRenameItem(Lnet/minecraft/network/packet/c2s/play/RenameItemC2SPacket;)V")
    public void onRenameItem_setNewName(AnvilScreenHandler anvil, String name) {
        anvil.setNewItemName(MoreGameRules.get().checkBooleanWithPerm(player.getWorld().getGameRules(), MoreGameRules.get().doItemColoursRule(), player)
                || player.hasPermissionLevel(server.getOpPermissionLevel()) ? MoreCommands.translateFormattings(name) : name);
    }
}
