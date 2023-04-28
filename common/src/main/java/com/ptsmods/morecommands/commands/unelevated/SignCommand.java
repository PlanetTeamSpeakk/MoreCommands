package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.mixin.common.accessor.MixinSignBlockEntityAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class SignCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("sign")
                .executes(ctx -> {
                    BlockHitResult result = (BlockHitResult) MoreCommands.getRayTraceTarget(ctx.getSource().getPlayerOrException(), 160, true, true);
                    BlockEntity be = ctx.getSource().getLevel().getBlockEntity(result.getBlockPos());
                    if (Compat.get().tagContains(new ResourceLocation("minecraft:signs"), ctx.getSource().getLevel().getBlockState(result.getBlockPos()).getBlock()) && be instanceof SignBlockEntity sbe) {
                        ((MixinSignBlockEntityAccessor) sbe).setIsEditable(true);
                        sbe.setAllowedPlayerEditor(Compat.get().getUUID(ctx.getSource().getPlayerOrException()));
                        ctx.getSource().getPlayerOrException().openTextEdit(sbe); // Copying content onto edit screen is handled in MixinSignEditScreen.
                        return 1;
                    }
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/sign";
    }
}
