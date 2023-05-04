package com.ptsmods.morecommands.mixin.compat.compat194.plus;

import com.ptsmods.morecommands.api.addons.ChatScreenEditBoxMarker;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net/minecraft/client/gui/screens/ChatScreen$1")
public class MixinChatScreenEditBox implements ChatScreenEditBoxMarker {}
