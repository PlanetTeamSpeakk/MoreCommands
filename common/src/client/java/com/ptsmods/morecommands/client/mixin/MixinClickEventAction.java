package com.ptsmods.morecommands.client.mixin;

import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;

// Empty mixin, just so we can get the ClassNode to add our own enum values to.
@Mixin(ClickEvent.Action.class)
public class MixinClickEventAction {
}
