package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen extends Screen {

    @Shadow protected EditBox input;

    protected MixinChatScreen(Component component) {
        super(component);
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void focusInputAfterClick(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        // Ensure the input box always remains focused.
        setFocused(input);
    }
}
