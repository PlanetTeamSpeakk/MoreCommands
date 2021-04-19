package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.AbstractTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

	@Overwrite
	public boolean hasLabel(T livingEntity) {
		double d = MinecraftClient.getInstance().getEntityRenderDispatcher().getSquaredDistanceToCamera(livingEntity);
		float f = livingEntity.isSneaky() && !ClientOptions.Rendering.seeTagSneaking.getValue() ? 32.0F : 64.0F;
		if (d >= (double)(f * f)) {
			return false;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
			if (ClientOptions.Rendering.renderOwnTag.getValue() || livingEntity != clientPlayerEntity) {
				AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
				AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
				if (abstractTeam != null) {
					AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
					switch(visibilityRule) {
						case ALWAYS:
							return bl;
						case NEVER:
							return false;
						case HIDE_FOR_OTHER_TEAMS:
							return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
						case HIDE_FOR_OWN_TEAM:
							return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
						default:
							return true;
					}
				}
			}

			return MinecraftClient.isHudEnabled() && (ClientOptions.Rendering.renderOwnTag.getValue() || livingEntity != minecraftClient.getCameraEntity()) && bl && !livingEntity.hasPassengers();
		}
	}

}
