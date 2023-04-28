package com.ptsmods.morecommands.mixin.compat.compat193.plus;

import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.callbacks.PlayerListEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfoRemove")
    public Object onPlayerList_remove(Map<?, ?> map, Object key) {
        Object entry = map.remove(key);
        PlayerListEvent.REMOVE.invoker().call((PlayerInfo) entry);
        return entry;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/Map;putIfAbsent(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", remap = false), method = "handlePlayerInfoUpdate")
    public Object onPlayerList_put(Map<Object, Object> map, Object key, Object value) {
        Object prevValue = map.putIfAbsent(key, value);

        if (prevValue == null) PlayerListEvent.ADD.invoker().call((PlayerInfo) value);
        return prevValue;
    }

    @Inject(at = @At("HEAD"), method = "sendUnsignedCommand", cancellable = true)
    public void sendUnsignedCommand(String command, CallbackInfoReturnable<Boolean> cbi) {
        IMoreCommandsClient.handleCommand(command, cbi);
    }

    @Inject(at = @At("HEAD"), method = "sendCommand", cancellable = true)
    public void sendCommand(String command, CallbackInfo cbi) {
        IMoreCommandsClient.handleCommand(command, cbi);
    }
}
