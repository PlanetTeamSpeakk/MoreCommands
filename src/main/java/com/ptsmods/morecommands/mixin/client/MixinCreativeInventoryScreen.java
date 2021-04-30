package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import com.ptsmods.morecommands.mixin.client.accessor.MixinAbstractButtonWidgetAccessor;
import com.ptsmods.morecommands.mixin.client.accessor.MixinItemGroupButtonWidgetAccessor;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeInventoryScreen.class, priority = 1100) // Higher priority so it gets injected after the Fabric API does.
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private TextFieldWidget searchBox;
	private FabricCreativeGuiComponents.ItemGroupButtonWidget pagerPrev, pagerNext;

	public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"), method = "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V")
	public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
		InventoryScreen.drawEntity(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.85f : 1f)), mouseX, mouseY, entity);
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;isPointWithinBounds(IIIIDD)Z"), method = "renderTabTooltipIfHovered", index = 1)
	private int renderTabTooltipIfHovered_isPointWithinBounds_yPosition(int y) {
		return y + 2;
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void init(CallbackInfo cbi) {
		CreativeInventoryScreen thiz = ReflectionHelper.cast(this);
		for (AbstractButtonWidget button : buttons) {
			if (button instanceof FabricCreativeGuiComponents.ItemGroupButtonWidget) {
				FabricCreativeGuiComponents.Type type = ((MixinItemGroupButtonWidgetAccessor) button).getType();
				if (type == FabricCreativeGuiComponents.Type.PREVIOUS) pagerPrev = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;
				else pagerNext = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;
				if (ClientOptions.Rendering.bigTabPager.getValue()) {
					((MixinAbstractButtonWidgetAccessor) button).setHeight(22);
					button.setWidth(22);
					button.x = thiz.width / 2 - (type == FabricCreativeGuiComponents.Type.PREVIOUS ? button.getWidth() : 0);
					button.y = field_2800 - 50;
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
	private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cbi) {
		if (!searchBox.isFocused() && ClientOptions.Tweaks.creativeKeyPager.getValue()) {
			GameOptions options = MinecraftClient.getInstance().options;
			FabricCreativeGuiComponents.ItemGroupButtonWidget btn = null;
			if ((options.keyLeft.matchesKey(keyCode, scanCode) || options.keyBack.matchesKey(keyCode, scanCode) || keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_DOWN) && pagerPrev != null && pagerPrev.active) btn = pagerPrev;
			else if ((options.keyRight.matchesKey(keyCode, scanCode) || options.keyForward.matchesKey(keyCode, scanCode) || keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_UP) && pagerNext != null && pagerNext.active) btn = pagerNext;
			if (btn != null) {
				btn.onPress();
				btn.playDownSound(MinecraftClient.getInstance().getSoundManager());
				cbi.setReturnValue(true);
			}
		}
	}
}
