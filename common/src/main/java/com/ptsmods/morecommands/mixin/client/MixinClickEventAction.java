package com.ptsmods.morecommands.mixin.client;

import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClickEvent.Action.class)
public class MixinClickEventAction {
}
