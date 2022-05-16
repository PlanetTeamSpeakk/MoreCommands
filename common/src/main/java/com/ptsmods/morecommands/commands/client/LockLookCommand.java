package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.callbacks.RenderTickEvent;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.mixin.common.accessor.MixinEntityAccessor;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LockLookCommand extends ClientCommand {
	private Entity target = null;
	private boolean smooth = false;

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		RenderTickEvent.PRE.register(tick -> {
			if (target != null) {
				if (Compat.get().isRemoved(target)) target = null;
				else {
					Vec3d ctr = getPlayer().getPos();
					Vec3d pos = target.getPos();
					double dx = pos.getX() - ctr.getX();
					double dz = pos.getZ() - ctr.getZ();
					getPlayer().changeLookDirection(
							(clampAngle(((MixinEntityAccessor) getPlayer()).getYaw_(), (float) Math.toDegrees(-Math.atan2(pos.getX() - ctr.getX(), pos.getZ() - ctr.getZ())), false) - ((MixinEntityAccessor) getPlayer()).getYaw_()) / 0.15,
							(clampAngle(((MixinEntityAccessor) getPlayer()).getPitch_(), (float) -Math.toDegrees(Math.atan2(target.getEyeY() - getPlayer().getEyeY(), Math.sqrt(dx * dx + dz * dz))), true) - ((MixinEntityAccessor) getPlayer()).getPitch_()) / 0.15);
				}
			}
		});
		dispatcher.register(cLiteral("locklook").executes(ctx -> {
			HitResult hit = MoreCommands.getRayTraceTarget(getPlayer(), getWorld(), 160, false, true);
			if (hit instanceof EntityHitResult && target != ((EntityHitResult) hit).getEntity()) {
				target = ((EntityHitResult) hit).getEntity();
				sendMsg("Your eyes have now been locked onto " + IMoreCommands.get().textToString(target.getDisplayName(), SS, true) + DF + ".");
				return 1;
			} else if (target == null) sendMsg(Formatting.RED + "You're not looking at an entity.");
			else {
				target = null;
				sendMsg("Your eyes are now unlocked.");
			}
			return 0;
		}).then(cLiteral("smooth").executes(ctx -> {
			sendMsg("Looking will " + Util.formatFromBool(smooth = !smooth, Formatting.GREEN + "now", Formatting.RED + "no longer") + DF + " be done smoothly.");
			return smooth ? 2 : 1;
		})));
	}


	private float clampAngle(float from, float to, boolean isPitch) {
		return smooth ? from + MathHelper.clamp(MathHelper.subtractAngles(from, to), isPitch ? -40 : -10, isPitch ? 40 : 10) : to;
	}
}
