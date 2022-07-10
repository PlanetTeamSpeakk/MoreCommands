package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public class MixinBookEditScreen {
    private static boolean mc_colourPickerOpen = false;
    @Shadow @Final private TextFieldHelper pageEdit;
    @Shadow @Final private TextFieldHelper titleEdit;
    @Shadow private boolean isSigning;

    @Inject(at = @At("TAIL"), method = "init()V")
    private void init(CallbackInfo cbi) {
        Screen thiz = ReflectionHelper.cast(this);
        MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, 5, false, mc_colourPickerOpen, s -> (isSigning ? titleEdit : pageEdit).insertText(s), b -> mc_colourPickerOpen = b);
    }
}
