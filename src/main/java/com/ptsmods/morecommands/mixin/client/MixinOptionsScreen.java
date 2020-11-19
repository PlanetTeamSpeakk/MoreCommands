package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.gui.ClientOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {

    protected MixinOptionsScreen(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init()V")
    public void init(CallbackInfo cbi) {
        OptionsScreen thiz = MoreCommands.cast(this);
        int x, y;
        //  under accessibility settings                                                  under resourcepacks
        if (getButtonAt(x = this.width / 2 + 5, y = this.height / 6 + 144 - 6) != null && getButtonAt(x = this.width / 2 - 155, y) != null) {
            x = this.width / 2 + 5; // above sounds
            y = this.height / 6 + 24 - 6;
        }
        addButton(new ButtonWidget(x, y, 150, 20, new LiteralText("MoreCommands").setStyle(MoreCommands.DS), button -> {
            MinecraftClient.getInstance().openScreen(new ClientOptionsScreen(this));
        }));
    }

    private AbstractButtonWidget getButtonAt(int x, int y) {
        for (AbstractButtonWidget b : buttons)
            if (b.x == x && b.y == y) return b;
        return null;
    }

}
