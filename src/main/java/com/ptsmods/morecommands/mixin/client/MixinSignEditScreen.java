package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {

    private static final Method mc_addButtonMethod = ReflectionHelper.getYarnMethod(Screen.class, "addButton", "method_25411", AbstractButtonWidget.class);
    private boolean mc_translateFormattings = false;
    private ButtonWidget mc_btn = null;
    @Shadow @Final private String[] field_24285;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;)V")
    private void init(SignBlockEntity sbe, CallbackInfo cbi) {
        Text[] text = ReflectionHelper.getYarnFieldValue(SignBlockEntity.class, "text", "field_12050", sbe);
        for (int i = 0; i < text.length; i++)
            field_24285[i] = MoreCommands.textToString(text[i], null).replace("\u00A7", "&");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer; getWidth(Ljava/lang/String;)I"), method = "method_27611(Ljava/lang/String;)Z")
    private int init_getWidth(TextRenderer textRenderer, String s) {
        return ClientOptions.Tweaks.noSignLimit ? s.length() <= 384 ? 90 : 91 : textRenderer.getWidth(s); // A limit of 384 characters is hard coded in UpdateSignC2SPacket.
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    private void init(CallbackInfo cbi) {
        SignEditScreen thiz = MoreCommands.cast(this);
        try {
            mc_addButtonMethod.invoke(thiz, mc_btn = new ButtonWidget(thiz.width/2 - 150/2, thiz.height/4 + 145, 150, 20, new LiteralText("Translate formattings: " + Formatting.RED + "OFF"), btn -> {
                mc_translateFormattings = !mc_translateFormattings;
                mc_updateBtn();
            }) {
                public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                    return false; // So you don't trigger the translate formattings button every time you press space after you've pressed it yourself once.
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            MoreCommands.log.catching(e);
        }
    }

    private void mc_updateBtn() {
        mc_btn.setMessage(new LiteralText("Translate formattings: " + Command.formatFromBool(mc_translateFormattings, Formatting.GREEN + "ON", Formatting.RED + "OFF")));
    }

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
    private String render_string2(String string2) {
        return mc_translateFormattings ? Command.translateFormats(string2) : string2;
    }

    @Inject(at = @At("HEAD"), method = "charTyped(CI)Z")
    public boolean charTyped(char chr, int keyCode, CallbackInfoReturnable<Boolean> cbi) {
        mc_translateFormattings = false;
        mc_updateBtn();
        return false;
    }

}
