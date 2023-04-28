package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.commands.elevated.FireballCommand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeFireball.class)
public class MixinFireballEntity extends Fireball {
    @Shadow private int explosionPower;

    public MixinFireballEntity(EntityType<? extends Fireball> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void setDeltaMovement(Vec3 velocity) {
        LargeFireball thiz = ReflectionHelper.cast(this);
        super.setDeltaMovement(FireballCommand.fireballs.containsKey(thiz) ? FireballCommand.fireballs.get(thiz).getLeft() : velocity);
    }

    @Inject(at = @At("HEAD"), method = "onHit", cancellable = true)
    private void onHit(HitResult result, CallbackInfo cbi) {
        LargeFireball thiz = ReflectionHelper.cast(this);
        if (!FireballCommand.fireballs.containsKey(thiz)) return;

        cbi.cancel();
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) this.onHitEntity((EntityHitResult) result);
        else if (type == HitResult.Type.BLOCK) this.onHitBlock((BlockHitResult) result);

        if (this.level.isClientSide) return;

        Compat.get().explode(level, this, getX(), getY(), getZ(), explosionPower, true, Explosion.BlockInteraction.DESTROY);

        if (FireballCommand.fireballs.get(thiz).getMiddle().addAndGet(1) < FireballCommand.fireballs.get(thiz).getRight()) return;
        setRemoved(RemovalReason.DISCARDED);
        FireballCommand.fireballs.remove(thiz);
    }
}
