package com.ptsmods.morecommands.mixin.common.accessor;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SignBlockEntity.class)
public interface MixinSignBlockEntityAccessor {
	@Accessor
	Text[] getText();

	@Accessor
	void setEditable(boolean editable);
}
