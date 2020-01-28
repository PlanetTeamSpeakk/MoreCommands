package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.ptsmods.morecommands.MoreCommands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ReachingEntityRenderer extends EntityRenderer {

	private static final Field	pointedEntityField;
	private final Minecraft		mc;

	static {
		pointedEntityField = Reference.getFieldMapped(EntityRenderer.class, "pointedEntity", "field_78528_u");
		pointedEntityField.setAccessible(true);
	}

	public ReachingEntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		super(mcIn, resourceManagerIn);
		mc = mcIn;
	}

	/**
	 * Gets the block or object that is being moused over. Copied from
	 * EntityRenderer, modified so that it properly takes reach into account for
	 * entities.
	 */
	@Override
	public void getMouseOver(float partialTicks) {
		if (MoreCommands.modInstalledServerSide) {
			Entity entity = mc.getRenderViewEntity();
			if (entity != null && mc.world != null) {
				mc.profiler.startSection("pick");
				mc.pointedEntity = null;
				double d0 = mc.playerController.getBlockReachDistance();
				mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
				Vec3d vec3d = entity.getPositionEyes(partialTicks);
				double d1 = d0;
				if (mc.objectMouseOver != null) d1 = mc.objectMouseOver.hitVec.distanceTo(vec3d);
				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
				setPointedEntity(null);
				Vec3d vec3d3 = null;
				List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, (Predicate<Entity>) (@Nullable Entity p_apply_1_) -> p_apply_1_ != null && p_apply_1_.canBeCollidedWith()));
				double d2 = d1;
				for (Entity entity1 : list) {
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
					if (axisalignedbb.contains(vec3d)) {
						if (d2 >= 0.0D) {
							setPointedEntity(entity1);
							vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
							d2 = 0.0D;
						}
					} else if (raytraceresult != null) {
						double d3 = vec3d.distanceTo(raytraceresult.hitVec);

						if (d3 < d2 || d2 == 0.0D) if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
							if (d2 == 0.0D) {
								setPointedEntity(entity1);
								vec3d3 = raytraceresult.hitVec;
							}
						} else {
							setPointedEntity(entity1);
							vec3d3 = raytraceresult.hitVec;
							d2 = d3;
						}
					}
				}
				if (getPointedEntity() != null && (d2 < d1 || mc.objectMouseOver == null)) {
					mc.objectMouseOver = new RayTraceResult(getPointedEntity(), vec3d3);
					if (getPointedEntity() instanceof EntityLivingBase || getPointedEntity() instanceof EntityItemFrame) mc.pointedEntity = getPointedEntity();
				}
				mc.profiler.endSection();
			}
			// if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit ==
			// RayTraceResult.Type.ENTITY) Reference.print(LogType.INFO,
			// mc.objectMouseOver);
		} else super.getMouseOver(partialTicks);
	}

	private Entity getPointedEntity() {
		try {
			return (Entity) pointedEntityField.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setPointedEntity(Entity entity) {
		try {
			pointedEntityField.set(this, entity);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
