package com.ptsmods.morecommands.mixin.compat.compat190.min;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import net.minecraft.client.GuiMessage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMessage.class)
public abstract class MixinGuiMessage<T> implements GuiMessageAddon {

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(int addedTime, T message, int id, CallbackInfo cbi) {
        if (message instanceof Component)
            mc$setStringContent(IMoreCommands.get().textToString((Component) message, null, true));
    }

    @Override
    public Component mc$getRichContent() {
        return getMessage() instanceof Component ? (Component) getMessage() : null;
    }

    @Shadow public abstract T getMessage();
}
