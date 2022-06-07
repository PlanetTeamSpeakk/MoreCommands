package com.ptsmods.morecommands.forge.mixin;

import com.ptsmods.morecommands.api.addons.CreativeInventoryScreenAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
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
        for (ClickableWidget button : ((ScreenAddon) this).mc$getButtons()) {
            if (!(button instanceof ButtonWidget)) continue;
            if (button.x == x && button.y == y - 50) pagerPrev = (ButtonWidget) button;
            else if (button.x == x + backgroundWidth - 20 && button.y == y - 50) pagerNext = (ButtonWidget) button;
        }
    }
}
