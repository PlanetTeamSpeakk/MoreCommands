package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public class MixinBookEditScreen {
	private static boolean mc_colourPickerOpen = false;
	@Shadow @Final private SelectionManager currentPageSelectionManager;
	@Shadow @Final private SelectionManager bookTitleSelectionManager;
	@Shadow private boolean signing;

	@Inject(at = @At("TAIL"), method = "init()V")
	private void init(CallbackInfo cbi) {
		Screen thiz = ReflectionHelper.cast(this);
		MoreCommandsClient.addColourPicker(thiz, thiz.width - 117, 5, false, mc_colourPickerOpen, s -> (signing ? bookTitleSelectionManager : currentPageSelectionManager).insert(s), b -> mc_colourPickerOpen = b);
	}
}
