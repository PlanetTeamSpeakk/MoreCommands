package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Overwrite
    public Text getPlayerListName() {
        return MoreCommands.<ServerPlayerEntity>cast(this).getDisplayName();
    }

}
