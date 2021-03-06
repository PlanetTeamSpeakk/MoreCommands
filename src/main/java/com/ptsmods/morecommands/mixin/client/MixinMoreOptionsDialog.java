package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.compat.client.ClientCompat;
import com.ptsmods.morecommands.gui.WorldInitCommandsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoreOptionsDialog.class)
public class MixinMoreOptionsDialog {
    @Shadow private GeneratorOptions generatorOptions;
    @Unique private ButtonWidget wicBtn;

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CreateWorldScreen parent, MinecraftClient client, TextRenderer textRenderer, CallbackInfo ci) {
        ClientCompat.getCompat().addButton(parent, wicBtn = new ButtonWidget(parent.width / 2 + 5, 151, 150, 20, new LiteralText("Initialisation Commands"), btn -> MinecraftClient.getInstance().openScreen(new WorldInitCommandsScreen(parent)))).visible = false;
    }

    @Inject(at = @At("TAIL"), method = "setVisible")
    public void setVisible(boolean visible, CallbackInfo ci) {
        wicBtn.visible = !generatorOptions.isDebugWorld() && visible;
    }
}
