package com.ptsmods.morecommands.mixin.compat.compat191.plus;

import com.ptsmods.morecommands.api.addons.GuiMessageLineAddon;
import net.minecraft.client.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.Line.class)
public class MixinGuiMessageLine implements GuiMessageLineAddon {
    private @Unique int parentId = -1;

    @Override
    public void mc$setParentId(int id) {
        parentId = id;
    }

    @Override
    public int mc$getParentId() {
        return parentId;
    }
}
