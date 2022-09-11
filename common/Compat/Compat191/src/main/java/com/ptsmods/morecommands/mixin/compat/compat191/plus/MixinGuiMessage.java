package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.addons.GuiMessageAddon;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMessage.class)
public abstract class MixinGuiMessage implements GuiMessageAddon {

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(int addedTime, Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo cbi) {
        mc$setStringContent(IMoreCommands.get().textToString(message));
    }

    @Override
    public Component mc$getRichContent() {
        return content();
    }

    @Shadow public abstract Component content();
}
