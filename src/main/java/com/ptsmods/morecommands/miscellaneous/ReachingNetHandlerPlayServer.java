package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class ReachingNetHandlerPlayServer extends NetHandlerPlayServer {

	private final MinecraftServer server;

	public ReachingNetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
		super(server, networkManagerIn, playerIn);
		this.server = server;
	}

	/**
	 * Processes left and right clicks on entities. Copied from
	 * NetHandlerPlayServer, modified so it properly takes reach into account for
	 * entities.
	 */
	@Override
	public void processUseEntity(CPacketUseEntity packetIn) {
		PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, player.getServerWorld());
		WorldServer worldserver = server.getWorld(player.dimension);
		Entity entity = packetIn.getEntityFromWorld(worldserver);
		player.markPlayerActive();
		if (entity != null) {
			double d0 = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + .5;
			d0 *= d0;
			if (player.getDistanceSq(entity) < d0) if (packetIn.getAction() == CPacketUseEntity.Action.INTERACT) {
				EnumHand enumhand = packetIn.getHand();
				player.interactOn(entity, enumhand);
			} else if (packetIn.getAction() == CPacketUseEntity.Action.INTERACT_AT) {
				EnumHand enumhand1 = packetIn.getHand();
				if (net.minecraftforge.common.ForgeHooks.onInteractEntityAt(player, entity, packetIn.getHitVec(), enumhand1) != null) return;
				entity.applyPlayerInteraction(player, packetIn.getHitVec(), enumhand1);
			} else if (packetIn.getAction() == CPacketUseEntity.Action.ATTACK) {
				if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity == player) {
					disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
					server.logWarning("Player " + player.getName() + " tried to attack an invalid entity");
					return;
				}
				player.attackTargetEntityWithCurrentItem(entity);
			}
		}
	}

}
