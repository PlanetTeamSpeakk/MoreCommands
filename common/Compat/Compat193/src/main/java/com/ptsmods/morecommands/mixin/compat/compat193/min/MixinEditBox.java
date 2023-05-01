package com.ptsmods.morecommands.mixin.compat.compat193.min;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.gui.PlaceholderEditBox;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public class MixinEditBox {

    @Inject(at = @At("TAIL"), method = "renderButton")
    private void renderWidget(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        EditBox thiz = ReflectionHelper.cast(this);
        if (thiz instanceof PlaceholderEditBox peb) peb.renderPlaceholder(poseStack);
    }
}
