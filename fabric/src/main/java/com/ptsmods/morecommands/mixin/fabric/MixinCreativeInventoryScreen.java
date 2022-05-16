package com.ptsmods.morecommands.mixin.fabric;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.mixin.client.accessor.MixinClickableWidgetAccessor;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements CreativeInventoryScreenAddon {
	private @Unique ButtonWidget pagerPrev, pagerNext;

	public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Override
	public ButtonWidget mc$getPagerPrev() {
		return pagerPrev;
	}

	@Override
	public ButtonWidget mc$getPagerNext() {
		return pagerNext;
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void init(CallbackInfo cbi) {
		CreativeInventoryScreen thiz = ReflectionHelper.cast(this);
		for (ClickableWidget button : ((ScreenAddon) this).mc$getButtons()) {
			if (!(button instanceof FabricCreativeGuiComponents.ItemGroupButtonWidget)) continue;

			FabricCreativeGuiComponents.Type type = ((MixinItemGroupButtonWidgetAccessor) button).getType();
			if (type == FabricCreativeGuiComponents.Type.PREVIOUS) pagerPrev = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;
			else pagerNext = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;

			if (!ClientOptions.Rendering.bigTabPager.getValue()) continue;
			((MixinClickableWidgetAccessor) button).setHeight(22);
			button.setWidth(22);
			button.x = thiz.width / 2 - (type == FabricCreativeGuiComponents.Type.PREVIOUS ? button.getWidth() : 0);
			button.y = y - 50;
		}
	}
}
