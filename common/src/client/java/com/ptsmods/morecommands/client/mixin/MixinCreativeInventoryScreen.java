package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreativeModeInventoryScreen.class, priority = 1100) // Higher priority so it gets injected after the Fabric API does.
public abstract class MixinCreativeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Shadow private EditBox searchBox;

    public MixinCreativeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(IIIFFLnet/minecraft/world/entity/LivingEntity;)V"), method = "renderBg")
    public void drawBackground_drawEntity(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.renderEntityInInventory(x, y, (int) (size * (ClientOptions.Rendering.renderOwnTag.getValue() ? 0.85f : 1f)), mouseX, mouseY, entity);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;isHovering(IIIIDD)Z"), method = "checkTabHovering", index = 1)
    private int renderTabTooltipIfHovered_isPointWithinBounds_yPosition(int y) {
        return y + 2;
    }

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cbi) {
        if (searchBox.isFocused() || !ClientOptions.Tweaks.creativeKeyPager.getValue() || !(this instanceof CreativeInventoryScreenAddon addon)) return;

        Options options = Minecraft.getInstance().options;
        Button btn = null;
        if ((options.keyLeft.matches(keyCode, scanCode) || options.keyDown.matches(keyCode, scanCode) ||
                keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_DOWN) && addon.mc$getPagerPrev() != null && addon.mc$getPagerPrev().active)
            btn = addon.mc$getPagerPrev();
        else if ((options.keyRight.matches(keyCode, scanCode) || options.keyUp.matches(keyCode, scanCode) ||
                keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_UP) && addon.mc$getPagerNext() != null && addon.mc$getPagerNext().active)
            btn = addon.mc$getPagerNext();
        if (btn != null) {
            btn.onPress();
            btn.playDownSound(Minecraft.getInstance().getSoundManager());
            cbi.setReturnValue(true);
        }
    }
}
