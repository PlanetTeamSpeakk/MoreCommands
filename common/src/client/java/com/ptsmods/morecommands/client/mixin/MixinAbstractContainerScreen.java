package com.ptsmods.morecommands.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ptsmods.morecommands.api.addons.SlotAddon;
import com.ptsmods.morecommands.client.commands.SearchItemCommand;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen extends Screen {

    protected MixinAbstractContainerScreen(Component component) {
        super(component);
    }

    @Inject(method = "renderSlot", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void renderSlotSearchOverlay(PoseStack poseStack, Slot slot, CallbackInfo ci) {
        if (SearchItemCommand.getCurrentPredicate() == null || ((SlotAddon) slot).matchesCurrentSearchItemPredicate()) return;

        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fill(poseStack, slot.x, slot.y, slot.x + 16, slot.y + 16,0xA0000000);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
}
