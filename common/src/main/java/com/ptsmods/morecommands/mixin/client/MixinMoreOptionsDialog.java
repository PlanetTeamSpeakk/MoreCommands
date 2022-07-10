package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.gui.WorldInitCommandsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldGenSettingsComponent.class)
public abstract class MixinMoreOptionsDialog {
    @Unique private Button wicBtn;

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CreateWorldScreen parent, Minecraft client, Font textRenderer, CallbackInfo ci) {
        ((ScreenAddon) parent).mc$addButton(wicBtn = new Button(parent.width / 2 + 5, 151, 150, 20, LiteralTextBuilder.literal("Initialisation Commands"),
                btn -> Minecraft.getInstance().setScreen(new WorldInitCommandsScreen(parent)))).visible = false;
    }

    @Inject(at = @At("TAIL"), method = "setVisibility")
    public void setVisible(boolean visible, CallbackInfo ci) {
        wicBtn.visible = isDebug() && visible;
    }

    @Shadow public abstract boolean isDebug();
}
