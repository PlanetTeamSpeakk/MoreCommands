package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ChatScreenEditBoxMarker;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerEventHandler.class)
public class MixinAbstractContainerEventHandler {

    @Inject(method = "setFocused", at = @At("HEAD"), cancellable = true)
    private void cancelFocusIfNotEditBoxOnChatScreen(GuiEventListener guiEventListener, CallbackInfo ci) {
        // Focus must remain on EditBox on the ChatScreen.
        AbstractContainerEventHandler thiz = ReflectionHelper.cast(this);

        if (thiz instanceof ChatScreen && !(guiEventListener instanceof ChatScreenEditBoxMarker))
            ci.cancel();
    }
}
