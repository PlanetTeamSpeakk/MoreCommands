package com.ptsmods.morecommands.mixin.compat.compat17.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MixinAccessWidener;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {
    @Shadow @Final private String[] text;

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;Z)V")
    private void init(SignBlockEntity sbe, boolean filtered, CallbackInfo cbi) {
        Text[] text = MixinAccessWidener.get().signBlockEntity$getTexts(sbe);
        for (int i = 0; i < text.length; i++)
            this.text[i] = IMoreCommands.get().textToString(text[i], null, true).replace("\u00A7", "&");
    }
}
