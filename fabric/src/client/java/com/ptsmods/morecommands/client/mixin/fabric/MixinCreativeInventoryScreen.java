package com.ptsmods.morecommands.client.mixin.fabric;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.client.mixin.accessor.MixinAbstractWidgetAccessor;
import com.ptsmods.morecommands.client.mixin.accessor.MixinClickableWidgetAccessor;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
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

@SuppressWarnings("UnstableApiUsage")
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

            MixinAbstractWidgetAccessor accessor = (MixinAbstractWidgetAccessor) button;
            accessor.setX_(thiz.width / 2 - (type == FabricCreativeGuiComponents.Type.PREVIOUS ? button.getWidth() : 0));
            accessor.setY_(topPos - 50);
        }
    }
}
