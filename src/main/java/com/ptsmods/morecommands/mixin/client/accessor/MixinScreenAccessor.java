package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface MixinScreenAccessor {
    @Invoker
    <T extends AbstractButtonWidget> T callAddButton(T button);
}
