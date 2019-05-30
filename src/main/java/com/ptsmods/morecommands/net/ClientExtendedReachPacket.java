package com.ptsmods.morecommands.net;

import com.ptsmods.morecommands.miscellaneous.ReachProvider;
import com.ptsmods.morecommands.miscellaneous.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientExtendedReachPacket extends AbstractPacket {

	private int		activity;
	private boolean	place;
	private boolean	holdingctrl;

	public ClientExtendedReachPacket() {}

	public ClientExtendedReachPacket(int activity) {
		this(activity, false);
	}

	public ClientExtendedReachPacket(int activity /*
													 * 0 = destroy block/damage entity, 1 = place block/entity interact, 2 = pick
													 * block
													 */, boolean place) {
		this(activity, place, false);
	}

	public ClientExtendedReachPacket(int activity, boolean place, boolean holdingctrl) {
		this.activity = activity;
		this.place = place;
		this.holdingctrl = holdingctrl;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		activity = buf.readByte();
		place = buf.readBoolean();
		holdingctrl = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(activity);
		buf.writeBoolean(place);
		buf.writeBoolean(holdingctrl);
	}

	@Override
	public IMessage run(MessageContext ctx) {
		ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
			RayTraceResult result = Reference.rayTrace(ctx.getServerHandler().player, ctx.getServerHandler().player.getCapability(ReachProvider.reachCap, null).get());
			BlockPos pos = result.getBlockPos() == null ? result.entityHit == null ? new BlockPos(result.hitVec.x, result.hitVec.y, result.hitVec.z) : result.entityHit.getPosition() : result.getBlockPos();
			if (pos == null || !ctx.getServerHandler().player.getEntityWorld().isBlockLoaded(pos) || ctx.getServerHandler().player.getEntityWorld().isOutsideBuildHeight(pos)) return;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			place = place || ctx.getServerHandler().player.isSneaking();
			if (place && !ctx.getServerHandler().player.getEntityWorld().getBlockState(pos).getBlock().isReplaceable(ctx.getServerHandler().player.getEntityWorld(), pos)) switch (result.sideHit) {
			case DOWN:
				y -= 1;
				break;
			case UP:
				y += 1;
				break;
			case NORTH:
				z -= 1;
				break;
			case SOUTH:
				z += 1;
				break;
			case WEST:
				x -= 1;
				break;
			case EAST:
				x += 1;
				break;
			}
			pos = new BlockPos(x, y, z);
			if (activity == 0) {
				boolean te = result.typeOfHit == RayTraceResult.Type.ENTITY;
				if (te && result.entityHit != null) ctx.getServerHandler().player.attackTargetEntityWithCurrentItem(result.entityHit); // result.entityHit could be null if the entity is for example a falling sand
																																		 // entity which is often gone before the packet arrives.
				else if (!te && ctx.getServerHandler().player.isCreative() && !(ctx.getServerHandler().player.getHeldItemMainhand().getItem() instanceof ItemSword)) ctx.getServerHandler().player.getEntityWorld().destroyBlock(pos, false);
			} else if (activity == 1) {
				// This should open any containers the player might have clicked on; however,
				// due to Minecraft standards, the container will automatically close if the
				// player gets too far away.
				if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					boolean flag = place && ctx.getServerHandler().player.isSneaking() || !ctx.getServerHandler().player.getEntityWorld().getBlockState(result.getBlockPos()).getBlock().onBlockActivated(ctx.getServerHandler().player.getEntityWorld(), result.getBlockPos(), ctx.getServerHandler().player.getEntityWorld().getBlockState(result.getBlockPos()), ctx.getServerHandler().player, EnumHand.MAIN_HAND, result.sideHit, (float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z);
					if (flag) if (ctx.getServerHandler().player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
						IBlockState iblockstate = ctx.getServerHandler().player.getEntityWorld().getBlockState(pos);
						Block block = iblockstate.getBlock();
						if (!block.isReplaceable(ctx.getServerHandler().player.getEntityWorld(), pos)) pos = pos.offset(result.sideHit);
						ItemStack itemstack = ctx.getServerHandler().player.getHeldItemMainhand();
						if (!itemstack.isEmpty() && ctx.getServerHandler().player.canPlayerEdit(pos, result.sideHit, itemstack) && ctx.getServerHandler().player.getEntityWorld().mayPlace(((ItemBlock) ctx.getServerHandler().player.getHeldItemMainhand().getItem()).getBlock(), pos, false, result.sideHit, ctx.getServerHandler().player)) {
							int i = ctx.getServerHandler().player.getHeldItemMainhand().getItem().getMetadata(itemstack.getMetadata());
							IBlockState iblockstate1 = ((ItemBlock) ctx.getServerHandler().player.getHeldItemMainhand().getItem()).getBlock().getStateForPlacement(ctx.getServerHandler().player.getEntityWorld(), pos, result.sideHit, (float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z, i, ctx.getServerHandler().player, EnumHand.MAIN_HAND);
							if (((ItemBlock) ctx.getServerHandler().player.getHeldItemMainhand().getItem()).placeBlockAt(itemstack, ctx.getServerHandler().player, ctx.getServerHandler().player.getEntityWorld(), pos, result.sideHit, (float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z, iblockstate1)) {
								iblockstate1 = ctx.getServerHandler().player.getEntityWorld().getBlockState(pos);
								SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, ctx.getServerHandler().player.getEntityWorld(), pos, ctx.getServerHandler().player);
								ctx.getServerHandler().player.getEntityWorld().playSound(ctx.getServerHandler().player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								if (!ctx.getServerHandler().player.isCreative()) itemstack.shrink(1); // Had to copy all of this code to change this one line.
							}
						}
					} else {
						ctx.getServerHandler().player.getHeldItemMainhand().getItem().onItemRightClick(ctx.getServerHandler().player.getEntityWorld(), ctx.getServerHandler().player, EnumHand.MAIN_HAND);
						ctx.getServerHandler().player.getHeldItemMainhand().onItemUse(ctx.getServerHandler().player, ctx.getServerHandler().player.getEntityWorld(), pos, EnumHand.MAIN_HAND, result.sideHit, (float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z);
					}
				} else if (result.typeOfHit == RayTraceResult.Type.ENTITY && !MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.EntityInteract(ctx.getServerHandler().player, EnumHand.MAIN_HAND, result.entityHit))) result.entityHit.processInitialInteract(ctx.getServerHandler().player, EnumHand.MAIN_HAND);
			} else if (activity == 2 && result.typeOfHit != RayTraceResult.Type.MISS) {
				ItemStack stack = null;
				int i = ctx.getServerHandler().player.inventory.currentItem;
				if (ctx.getServerHandler().player.inventory.getStackInSlot(i).getItem() != Items.AIR) for (int i0 = 0; i0 < 9; i0++)
					if (ctx.getServerHandler().player.inventory.getStackInSlot(i).getItem() != Items.AIR && ctx.getServerHandler().player.inventory.getStackInSlot(i0).getItem() == Items.AIR) {
						i = i0;
						break;
					}
				if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					TileEntity te = ctx.getServerHandler().player.getEntityWorld().getBlockState(pos).getBlock().hasTileEntity(ctx.getServerHandler().player.getEntityWorld().getBlockState(pos)) ? ctx.getServerHandler().player.getEntityWorld().getTileEntity(pos) : null;
					stack = ctx.getServerHandler().player.getEntityWorld().getBlockState(pos).getBlock().getPickBlock(ctx.getServerHandler().player.getEntityWorld().getBlockState(pos), result, ctx.getServerHandler().player.getEntityWorld(), pos, ctx.getServerHandler().player);
					stack = te != null && holdingctrl ? Reference.storeTE(stack, te) : stack;
				} else stack = result.entityHit.getPickedResult(result);
				int i0 = -1;// Checking it again on the server in case the client has different knowledge of
							// the stack in question than the server does, e.g. a chest with nbt.
				for (int i1 = 0; i1 < ctx.getServerHandler().player.inventory.mainInventory.size(); ++i1)
					if (!ctx.getServerHandler().player.inventory.mainInventory.get(i1).isEmpty() && stack.getItem() == ctx.getServerHandler().player.inventory.mainInventory.get(i1).getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == ctx.getServerHandler().player.inventory.mainInventory.get(i1).getMetadata()) && ItemStack.areItemStackTagsEqual(stack, ctx.getServerHandler().player.inventory.mainInventory.get(i1))) {
						i0 = i1;
						break;
					}
				// Checking it again on the server in case the client has different knowledge of
				// the stack in question than the server does, e.g. a chest with nbt.
				if (InventoryPlayer.isHotbar(i0)) {
					ctx.getServerHandler().player.inventory.currentItem = i0; // Updating on both the server and client.
					ctx.getServerHandler().sendPacket(new SPacketHeldItemChange(i0));
					stack = null; // So it doesn't get added again.
				}
				if (stack != null) {
					// ctx.getServerHandler().player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					// null).insertItem(i, stack, false); // This does not overwrite existing
					// itemstacks in slots.
					ctx.getServerHandler().player.inventory.setInventorySlotContents(i, stack);
					if (i != ctx.getServerHandler().player.inventory.currentItem) {
						ctx.getServerHandler().player.inventory.currentItem = i;
						ctx.getServerHandler().sendPacket(new SPacketHeldItemChange(i));
					}
					ctx.getServerHandler().player.inventoryContainer.detectAndSendChanges();
				}
			}
		});
		return null;
	}
}
