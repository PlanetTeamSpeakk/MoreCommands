package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net/minecraft/client/gui/screens/worldselection/CreateWorldScreen$MoreTab")
public abstract class MixinMoreTab {
    @Inject(at = @At("RETURN"), method = "<init>", locals = LocalCapture.CAPTURE_FAILHARD)
    private void init(CreateWorldScreen parent, CallbackInfo cbi, GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(Button.builder(LiteralTextBuilder.literal("Initialisation Commands"), btn ->
                        Minecraft.getInstance().setScreen(IMoreCommandsClient.get().newWorldInitScreen(parent)))
                .width(210)
                .build());
    }
}
