package com.ptsmods.morecommands.compat;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class Compat18Plus extends Compat17Plus {
    static final Compat18Plus instance;

    static {
        instance = Compat.getIVer() >= 18 ? null : new Compat18Plus();
    }

    Compat18Plus() {} // Package-private constructor

    @Override
    public NbtCompound writeSpawnerLogicNbt(MobSpawnerLogic logic, World world, BlockPos pos, NbtCompound nbt) {
        return logic.writeNbt(nbt);
    }

    @Override
    public NbtCompound writeBENBT(BlockEntity be) {
        return be.createNbtWithIdentifyingData();
    }
}
