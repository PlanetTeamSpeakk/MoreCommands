package com.ptsmods.morecommands.mixin.compat.compat192.min;

import com.ptsmods.morecommands.api.callbacks.PlayerListEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfo")
    public Object onPlayerList_remove(Map<?, ?> map, Object key) {
        Object entry = map.remove(key);
        PlayerListEvent.REMOVE.invoker().call((PlayerInfo) entry);
        return entry;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfo")
    public Object onPlayerList_put(Map<Object, Object> map, Object key, Object value) {
        map.put(key, value);
        PlayerListEvent.ADD.invoker().call((PlayerInfo) value);
        return value;
    }
}
