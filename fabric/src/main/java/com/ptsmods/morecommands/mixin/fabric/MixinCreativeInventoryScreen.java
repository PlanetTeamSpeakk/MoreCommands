package com.ptsmods.morecommands.mixin.fabric;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.mixin.client.accessor.MixinClickableWidgetAccessor;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
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
        CreativeModeInventoryScreen thiz = ReflectionHelper.cast(this);
        for (AbstractWidget button : ((ScreenAddon) this).mc$getButtons()) {
            if (!(button instanceof FabricCreativeGuiComponents.ItemGroupButtonWidget)) continue;

            FabricCreativeGuiComponents.Type type = ((MixinItemGroupButtonWidgetAccessor) button).getType();
            if (type == FabricCreativeGuiComponents.Type.PREVIOUS) pagerPrev = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;
            else pagerNext = (FabricCreativeGuiComponents.ItemGroupButtonWidget) button;

            if (!ClientOptions.Rendering.bigTabPager.getValue()) continue;
            ((MixinClickableWidgetAccessor) button).setHeight(22);
            button.setWidth(22);
            button.x = thiz.width / 2 - (type == FabricCreativeGuiComponents.Type.PREVIOUS ? button.getWidth() : 0);
            button.y = topPos - 50;
        }
    }
}
