package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.client.gui.ClientOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
    protected MixinOptionsScreen(Component title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init()V")
    public void init(CallbackInfo cbi) {
        int x, y;
        //  under accessibility settings                                                     under resourcepacks
        if (getButtonAt(x = this.width / 2 + 5, y = this.height / 6 + 144 - 6) != null && getButtonAt(x = this.width / 2 - 155, y) != null) {
            x = this.width / 2 + 5; // above sounds
            y = this.height / 6 + 24 - 6;
        }

        ((ScreenAddon) this).mc$addButton(new Button(x, y, 150, 20, LiteralTextBuilder.literal("MoreCommands", MoreCommands.DS),
                button -> Minecraft.getInstance().setScreen(new ClientOptionsScreen(this))));
    }

    @Unique
    private AbstractWidget getButtonAt(int x, int y) {
        for (AbstractWidget b : ((ScreenAddon) this).mc$getButtons())
            if (b.x == x && b.y == y) return b;
        return null;
    }
}
