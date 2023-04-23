package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.MixinServerPlayerEntityAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinSignBlockEntityAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class MixinAccessWidenerImpl implements MixinAccessWidener {
    private static boolean ignoreInteract;

    @Override
    public void serverPlayerEntity$setSyncedExperience(ServerPlayer player, int experience) {
        ((MixinServerPlayerEntityAccessor) player).setLastSentExp(experience);
    }

    @Override
    public char serverPlayNetworkHandler$gameMsgCharAt(ServerGamePacketListenerImpl thiz, String string, int index, ServerPlayer player, MinecraftServer server) {
        char ch = string.charAt(index);
        if (!string.startsWith("/") && ch == '\u00A7' && (MoreGameRules.get().checkBooleanWithPerm(thiz.player.level.getGameRules(), MoreGameRules.get().doChatColoursRule(), thiz.player)
                || player.hasPermissions(server.getOperatorUserPermissionLevel()))) ch = '&';
        return ch;
    }

    @Override
    public Component[] signBlockEntity$getTexts(SignBlockEntity sbe) {
        return ((MixinSignBlockEntityAccessor) sbe).getMessages();
    }
}
