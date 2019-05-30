package com.ptsmods.morecommands.miscellaneous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Ticker extends EventHandler implements ITickable {

	public float									tps				= -1, tps5 = -1, tps15 = -1;
	private List<Long>								ticksPassed		= new ArrayList();
	private long									lastTick		= -1;
	public float									ctps			= -1, ctps5 = -1, ctps15 = -1;
	private List<Long>								cticksPassed	= new ArrayList();
	private long									lastCtick		= -1;
	public static final Ticker						INSTANCE		= new Ticker();
	private Map<TickEvent.Type, List<TickRunnable>>	runnables		= new HashMap();

	private Ticker() {
		for (TickEvent.Type type : TickEvent.Type.values())
			runnables.put(type, new ArrayList());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Deprecated // Unused, just here because I want it to implement ITickable
	@Override
	public void update() {}

	/**
	 * Fired whenever a client tick runs.<br>
	 * Has no extra objects.
	 *
	 * @param event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		run(TickEvent.Type.CLIENT, event.phase);
		if (event.phase == Phase.END) {
			cticksPassed.add(0, System.currentTimeMillis());
			float cticks = 0;
			long start = System.currentTimeMillis();
			for (long l : cticksPassed) {
				l = start - l;
				if (l < 1000) cticks++;
				else {
					start -= 1000;
					if (l < 1000 * 60 * 15) {
						if (l < 1000 * 60) ctps = ctps == -1 ? cticks : (ctps + cticks) / 2;
						if (l < 1000 * 60 * 5) ctps5 = ctps5 == -1 ? cticks : (ctps5 + cticks) / 2;
						ctps15 = ctps15 == -1 ? cticks : (ctps15 + cticks) / 2;
					} else break;
					cticks = 0;
				}
			}
			for (int i = 0; i < cticksPassed.size(); i++)
				if (System.currentTimeMillis() - cticksPassed.get(i) >= 1000 * 60 * 15) cticksPassed.remove(i);
		}
	}

	/**
	 * Fired whenever a server tick runs.<br>
	 * Has no extra objects.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		run(TickEvent.Type.SERVER, event.phase);
		if (event.phase == Phase.END) {
			if (lastCtick != -1 && System.currentTimeMillis() - lastTick > 0) tps = (int) (1000 / (System.currentTimeMillis() - lastTick));
			lastTick = System.currentTimeMillis();
			ticksPassed.add(0, System.currentTimeMillis());
			float ticks = 0;
			long start = System.currentTimeMillis();
			for (long l : ticksPassed) {
				l = start - l;
				if (l < 1000) ticks++;
				else {
					start -= 1000;
					if (l < 1000 * 60 * 15) {
						if (l < 1000 * 60) tps = tps == -1 ? ticks : (tps + ticks) / 2;
						if (l < 1000 * 60 * 5) tps5 = tps5 == -1 ? ticks : (tps5 + ticks) / 2;
						tps15 = tps15 == -1 ? ticks : (tps15 + ticks) / 2;
					} else break;
					ticks = 0;
				}
			}
			for (int i = 0; i < ticksPassed.size(); i++)
				if (System.currentTimeMillis() - ticksPassed.get(i) >= 1000 * 60 * 15) ticksPassed.remove(i);
		}
	}

	/**
	 * Fired whenever a world tick runs.<br>
	 * Has two extra objects:<br>
	 * &emsp;1. Side, the side on which the tick ran.<br>
	 * &emsp;2. World, the world on which the tick ran.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		run(TickEvent.Type.WORLD, event.phase, event.side, event.world);
	}

	/**
	 * Fired whenever a player tick runs.<br>
	 * Has one extra object:<br>
	 * &emsp;1. EntityPlayer, the player which the tick ran for. Can be an instance
	 * of {@link net.minecraft.entity.player.EntityPlayerMP EntityPlayerMP} in which
	 * case the side is {@code SERVER}.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		run(TickEvent.Type.PLAYER, event.phase, event.player);
	}

	/**
	 * Fired whenever a render tick runs.<br>
	 * Has one extra object:<br>
	 * &emsp;1. Float renderTickTime, the time it took to render everything in this
	 * tick.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		run(TickEvent.Type.RENDER, event.phase, event.renderTickTime);
	}

	private void run(TickEvent.Type type, Phase phase, Object... extraArgs) {
		for (TickRunnable runnable : new ArrayList<>(runnables.get(type)))
			if (phase == runnable.getRunPhase() || runnable.runOnBothPhases()) {
				boolean remove = runnable.getRemoveWhenRan();
				try {
					runnable.run(extraArgs);
				} catch (RemoveThisRunnableException e) {
					remove = true;
				}
				if (remove) runnables.get(type).remove(runnable);
			}
	}

	/**
	 * Schedule a task so that it may be ran synchronously rather than
	 * asynchronously to avoid any ConcurrentModificationExceptions in the server
	 * ticking loop.
	 *
	 * @param runnable
	 */
	public void scheduleTask(Runnable runnable) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(runnable);
		else FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	public void addRunnable(TickEvent.Type type, TickRunnable runnable) {
		runnables.get(type).add(runnable);
	}

	/**
	 * <p>
	 * Can only be called from class {@link TickRunnable} and refrains the runnable
	 * calling this method from being ran again. <br>
	 * <br>
	 * This method should be called at the <font color="RED"><b>END</b></font> of
	 * the runnable since it works by throwing an exception which means the code
	 * will exit when this method is called.
	 * </p>
	 *
	 * @throws IllegalAccessException If the calling class does not extend or
	 *                                    implement {@link TickRunnable}
	 */
	public void removeThis() {
		if (Reference.isSuperClass(Reference.getCallerClass(), TickRunnable.class)) throw new RemoveThisRunnableException();
		else Reference.throwWithoutDeclaration(new IllegalAccessException("Calling class " + Reference.getCallerClass().getName() + " does not extend class " + TickRunnable.class.getName() + "."));
	}

	public static interface TickRunnable {

		public Map<TickRunnable, Boolean> removeWhenRan = new HashMap();

		/**
		 * Sets whether or not this runnable should be removed from the queue after it
		 * has been ran.
		 *
		 * @param flag The aforementioned flag.
		 * @return This instance of TickRunnable, useful for having one line less.
		 */
		public default TickRunnable setRemoveWhenRan(boolean flag) {
			removeWhenRan.put(this, flag);
			return this;
		}

		public default boolean getRemoveWhenRan() {
			return removeWhenRan.getOrDefault(this, true);
		}

		/**
		 * The phase on which to run this runnable on, if this returns
		 * {@link Phase#START} then this runnable is ran at the start of the tick, if it
		 * returns {@link Phase#END}, it is ran at the end of the tick.
		 *
		 * @return The phase on which to run this runnable on.
		 */
		public default Phase getRunPhase() {
			return Phase.START;
		}

		/**
		 * Highly unlikely you'll ever need this, but if you ever feel like running
		 * something 40 times a second instead of 20, this is what you'll need.
		 *
		 * @return Whether or not to run this runnable on {@link Phase#START} AND
		 *         {@link Phase#END}, defaults to false.
		 */
		public default boolean runOnBothPhases() {
			return false;
		}

		public void run(Object[] extraArgs);

	}

	private static final class RemoveThisRunnableException extends RuntimeException {
		private static final long serialVersionUID = 563808933741096460L;
	}

}
