package com.ptsmods.morecommands.mixin.compat;

import com.ptsmods.morecommands.commands.server.elevated.FireballCommand;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireballEntity.class)
public class MixinFireballEntity extends AbstractFireballEntity {
    @Shadow private int explosionPower;

    public MixinFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        FireballEntity thiz = ReflectionHelper.cast(this);
        super.setVelocity(FireballCommand.fireballs.containsKey(thiz) ? FireballCommand.fireballs.get(thiz).getLeft() : velocity);
    }

    @Inject(at = @At("HEAD"), method = "onCollision", cancellable = true)
    private void onCollision(HitResult result, CallbackInfo cbi) {
        FireballEntity thiz = ReflectionHelper.cast(this);
        if (FireballCommand.fireballs.containsKey(thiz)) {
            cbi.cancel();
            HitResult.Type type = result.getType();
            if (type == HitResult.Type.ENTITY) this.onEntityHit((EntityHitResult) result);
            else if (type == HitResult.Type.BLOCK) this.onBlockHit((BlockHitResult) result);
            if (!this.world.isClient) {
                this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), explosionPower, true, Explosion.DestructionType.DESTROY);
                if (FireballCommand.fireballs.get(thiz).getMiddle().addAndGet(1) >= FireballCommand.fireballs.get(thiz).getRight()) Compat.getCompat().setRemoved(this, 1);
            }
        }
    }
}
