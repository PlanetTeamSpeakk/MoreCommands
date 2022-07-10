package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.MixinAccessWidener;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen {
    @Shadow @Final private String[] messages;

    @Inject(at = @At("RETURN"), method = "<init>", remap = false)
    private void init(SignBlockEntity sbe, CallbackInfo cbi) {
        Component[] text = MixinAccessWidener.get().signBlockEntity$getTexts(sbe);
        for (int i = 0; i < text.length; i++)
            this.messages[i] = IMoreCommands.get().textToString(text[i], null, true).replace("\u00A7", "&");
    }
}
