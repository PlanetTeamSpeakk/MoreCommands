package com.ptsmods.morecommands.mixin.forge;

import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements CreativeInventoryScreenAddon {
    private @Unique Button pagerPrev, pagerNext;

    public MixinCreativeInventoryScreen(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Override
    public Button mc$getPagerPrev() {
        return pagerPrev;
    }

    @Override
    public Button mc$getPagerNext() {
        return pagerNext;
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo cbi) {
        for (AbstractWidget button : ((ScreenAddon) this).mc$getButtons()) {
            if (!(button instanceof Button)) continue;
            if (button.x == leftPos && button.y == topPos - 50) pagerPrev = (Button) button;
            else if (button.x == leftPos + imageWidth - 20 && button.y == topPos - 50) pagerNext = (Button) button;
        }
    }
}
