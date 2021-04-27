package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import com.ptsmods.morecommands.mixin.common.accessor.MixinSignBlockEntityAccessor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.SelectionManager;
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

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {

	private boolean mc_translateFormattings = false;
	private ButtonWidget mc_btn = null;
	private static boolean mc_colourPickerOpen = false;
	@Shadow private SelectionManager selectionManager;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer; getWidth(Ljava/lang/String;)I"), method = "method_27611(Ljava/lang/String;)Z")
	private int init_getWidth(TextRenderer textRenderer, String s) {
		return ClientOptions.Tweaks.noSignLimit.getValue() ? s.length() <= 384 ? 90 : 91 : textRenderer.getWidth(s); // A limit of 384 characters is hard coded in UpdateSignC2SPacket.
	}

	@Inject(at = @At("RETURN"), method = "init()V")
	private void init(CallbackInfo cbi) {
		SignEditScreen thiz = ReflectionHelper.cast(this);
		MoreCommandsClient.addButton(thiz, mc_btn = new ButtonWidget(thiz.width/2 - 150/2, thiz.height/4 + 145, 150, 20, new LiteralText("Translate formattings: " + Formatting.RED + "OFF"), btn -> {
			mc_translateFormattings = !mc_translateFormattings;
			mc_updateBtn();
		}) {
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				return false; // So you don't trigger the translate formattings button every time you press space after you've pressed it yourself once.
			}
		});
		MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, thiz.height/2 - 87, true, mc_colourPickerOpen, selectionManager::insert, b -> mc_colourPickerOpen = b);
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
		if (mc_translateFormattings) {
			mc_translateFormattings = false;
			mc_updateBtn();
		}
		return false;
	}

}
