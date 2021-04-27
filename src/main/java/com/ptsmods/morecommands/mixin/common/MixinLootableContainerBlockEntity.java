package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LootableContainerBlockEntity.class)
public class MixinLootableContainerBlockEntity {

	@ModifyConstant(method = "canPlayerUse(Lnet/minecraft/entity/player/PlayerEntity;)Z", constant = @Constant(doubleValue = 64.0D))
	public double canPlayerUse_maxReach(double d, PlayerEntity player) {
		return ReachCommand.getReach(player, true);
	}

}
