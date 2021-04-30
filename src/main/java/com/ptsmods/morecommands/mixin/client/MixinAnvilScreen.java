package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen {
	private static boolean mc_colourPickerOpen = false;
	@Shadow private TextFieldWidget nameField;

	@Inject(at = @At("TAIL"), method = "setup()V")
	protected void setup(CallbackInfo cbi) {
		Screen thiz = ReflectionHelper.cast(this);
		MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, thiz.height/2 - 87, true, mc_colourPickerOpen, nameField::write, b -> mc_colourPickerOpen = b);
	}
}
