package com.ptsmods.morecommands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;

public interface ServerSideArgumentType {
    ArgumentType<?> toVanillaArgumentType();
}
