package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LootableContainerBlockEntity.class)
public class MixinLootableContainerBlockEntity {

    @Overwrite
    public boolean canPlayerUse(PlayerEntity player) {
        LootableContainerBlockEntity thiz = MoreCommands.cast(this);
        if (thiz.getWorld().getBlockEntity(thiz.getPos()) != thiz) return false;
        else return player.squaredDistanceTo(thiz.getPos().getX() + 0.5D, thiz.getPos().getY() + 0.5D, thiz.getPos().getZ() + 0.5D) <= ReachCommand.getReach(player, true);
    }

}
