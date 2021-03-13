package com.ptsmods.morecommands.mixin.client.accessor;

import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public interface MixinEntitySelectorAccessor {

    @Accessor String getPlayerName();
    @Accessor UUID getUuid();
    @Accessor Function<Vec3d, Vec3d> getPositionOffset();
    @Accessor boolean getSenderOnly();
    @Accessor BiConsumer<Vec3d, List<? extends Entity>> getSorter();
    @Accessor boolean getIncludesNonPlayers();
    @Accessor Predicate<Entity> getBasePredicate();
    @Accessor Box getBox();
    @Accessor NumberRange.FloatRange getDistance();
    @Accessor TypeFilter<Entity, ?> getEntityFilter();
    @Invoker Predicate<Entity> callGetPositionPredicate(Vec3d vec);

}
