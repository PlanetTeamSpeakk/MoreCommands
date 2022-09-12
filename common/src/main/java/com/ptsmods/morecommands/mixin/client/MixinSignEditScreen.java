package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {
    private @Unique boolean translateFormattings = false;
    private Button mc_btn = null;
    private @Unique static boolean colourPickerOpen = false;
    @Shadow private TextFieldHelper signField;
    private @Unique String lastContent;
    private @Unique String lastContentTranslated;

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I"),
            method = {"method_27611", "m_169823_"}, require = 1, remap = false)
    private String init_getWidth_s(String s) {
        return ClientOptions.Tweaks.noSignLimit.getValue() ? ChatFormatting.stripFormatting(Util.translateFormats(s)) : s; // A limit of 384 characters is hard coded in UpdateSignC2SPacket.
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    private void init(CallbackInfo cbi) {
        SignEditScreen thiz = ReflectionHelper.cast(this);
        ((ScreenAddon) thiz).mc$addButton(mc_btn = new Button(thiz.width/2 - 150/2, thiz.height/4 + 145, 150, 20, LiteralTextBuilder.literal("Translate formattings: " + ChatFormatting.RED + "OFF"), btn -> {
            translateFormattings = !translateFormattings;
            mc_updateBtn();
        }) {
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false; // So you don't trigger the 'translate formattings' button every time you press space after you've pressed it yourself once.
            }
        });
        MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, thiz.height/2 - 87, true, colourPickerOpen, signField::insertText, b -> colourPickerOpen = b);
    }

    private void mc_updateBtn() {
        mc_btn.setMessage(LiteralTextBuilder.literal("Translate formattings: " + Util.formatFromBool(translateFormattings, ChatFormatting.GREEN + "ON", ChatFormatting.RED + "OFF")));
    }

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "render")
    private String render_string2(String string2) {
        if (translateFormattings && !Objects.equals(lastContent, string2)) lastContentTranslated = Util.translateFormats(string2);
        lastContent = string2;
        return translateFormattings ? lastContentTranslated : string2;
    }

    @Inject(at = @At("HEAD"), method = "charTyped(CI)Z")
    public void charTyped(char chr, int keyCode, CallbackInfoReturnable<Boolean> cbi) {
        if (translateFormattings) {
            translateFormattings = false;
            mc_updateBtn();
        }
    }
}
