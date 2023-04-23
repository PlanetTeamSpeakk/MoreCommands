package com.ptsmods.morecommands.client.mixin.accessor;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    @Accessor UUID getEntityUUID();
    @Accessor Function<Vec3, Vec3> getPosition();
    @Accessor boolean getCurrentEntity();
    @Accessor BiConsumer<Vec3, List<? extends Entity>> getOrder();
    @Accessor boolean getIncludesEntities();
    @Accessor Predicate<Entity> getPredicate();
    @Accessor AABB getAabb();
    @Accessor MinMaxBounds.Doubles getRange();
    @Invoker Predicate<Entity> callGetPredicate(Vec3 vec);

}
