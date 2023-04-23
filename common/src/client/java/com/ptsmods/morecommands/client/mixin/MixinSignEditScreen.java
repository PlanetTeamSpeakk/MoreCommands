package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.client.MoreCommandsClient;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MixinAccessWidener;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Predicate;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {
    private @Unique boolean translateFormattings = false;
    private Button mc_btn = null;
    private @Unique static boolean colourPickerOpen = false;
    @Shadow private TextFieldHelper signField;
    private @Unique String lastContent;
    private @Unique String lastContentTranslated;
    @Shadow @Final private String[] messages;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(SignBlockEntity sbe, boolean filtered, CallbackInfo cbi) {
        Component[] text = MixinAccessWidener.get().signBlockEntity$getTexts(sbe);
        for (int i = 0; i < text.length; i++)
            this.messages[i] = IMoreCommands.get().textToString(text[i], null, true).replace("\u00A7", "&");
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/TextFieldHelper;<init>" +
            "(Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Predicate;)V"),
            method = "init")
    private Predicate<String> init_new_TextFieldHelper(Predicate<String> predicate) {
        return text -> Minecraft.getInstance().font.width(ClientOptions.Tweaks.noSignLimit.getValue() ?
                Objects.requireNonNull(ChatFormatting.stripFormatting(text)) : text) <= 90;
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
