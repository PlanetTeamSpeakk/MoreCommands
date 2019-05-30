package com.ptsmods.morecommands.miscellaneous;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.ptsmods.morecommands.miscellaneous.Reference.LogType;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSnow;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryDelegate.Delegate;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class RegistryEventHandler extends EventHandler {

	private static final BlockSnow	blockSnow	= new BlockFactory<>(BlockSnow.class, new ResourceLocation("minecraft:snow_layer"), "snow").getBlockNoExceptions();
	private static boolean			initialised	= false;
	private static Field			availabilityMap, minId, addCallback, slaves, underlyingIntegerMap, values, intKeys, registryObjects;
	private static Method			setName, getIndex;

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(new SoundEvent(new ResourceLocation("morecommands:easteregg")).setRegistryName(new ResourceLocation("morecommands:easteregg")));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST) // Making sure other mods have registered their blocks first.
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		register((FMLControlledNamespacedRegistry) event.getRegistry(), new BlockFactory(BlockIce.class, new ResourceLocation("minecraft:ice"), "ice").getBlockNoExceptions());
		register((FMLControlledNamespacedRegistry) event.getRegistry(), blockSnow);
		event.getRegistry().register(Reference.lockedChest);
		for (Block block : Block.REGISTRY)
			if (block.getCreativeTabToDisplayOn() == null && block != Blocks.AIR) block.setCreativeTab(Reference.unobtainableItems);
		for (Item item : Item.REGISTRY)
			if (item.getCreativeTab() == null && item != Items.AIR) item.setCreativeTab(Reference.unobtainableItems);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerItems(RegistryEvent.Register<Item> event) {
		register((FMLControlledNamespacedRegistry) event.getRegistry(), new ItemSnow(blockSnow).setUnlocalizedName("snow").setRegistryName(new ResourceLocation("minecraft:snow_layer")));
		event.getRegistry().register(new ItemBlock(Reference.lockedChest).setRegistryName(Reference.lockedChest.getRegistryName()));
		for (Block block : Block.REGISTRY)
			if (!event.getRegistry().containsKey(block.getRegistryName())) event.getRegistry().register(new ItemBlock(block).setCreativeTab(Reference.unobtainableItems).setUnlocalizedName(block.getUnlocalizedName().substring(5)).setRegistryName(block.getRegistryName()));
		for (Item item : event.getRegistry())
			if (item.getCreativeTab() == null && item != Items.AIR) item.setCreativeTab(Reference.unobtainableItems);
	}

	@SuppressWarnings("null")
	public static <V extends IForgeRegistryEntry<V>> void register(FMLControlledNamespacedRegistry<V> registry, V thing) {
		if (thing != null || registry != null) return; // This does not seem to work, I've tried my best, but it just seems impossible
														// to overwrite existing entries.
		try {
			if (!initialised) {
				Class<FMLControlledNamespacedRegistry> c = FMLControlledNamespacedRegistry.class;
				Class<IntIdentityHashBiMap> c0 = IntIdentityHashBiMap.class;
				Class<RegistryNamespaced> c1 = RegistryNamespaced.class;
				availabilityMap = c.getDeclaredField("availabilityMap");
				availabilityMap.setAccessible(true);
				minId = c.getDeclaredField("minId");
				minId.setAccessible(true);
				addCallback = c.getDeclaredField("addCallback");
				addCallback.setAccessible(true);
				slaves = c.getDeclaredField("slaves");
				slaves.setAccessible(true);
				underlyingIntegerMap = c1.getDeclaredField("underlyingIntegerMap");
				underlyingIntegerMap.setAccessible(true);
				values = c0.getDeclaredField("values");
				values.setAccessible(true);
				intKeys = c0.getDeclaredField("intKeys");
				intKeys.setAccessible(true);
				registryObjects = RegistrySimple.class.getDeclaredField("registryObjects");
				registryObjects.setAccessible(true);

				setName = Delegate.class.getDeclaredMethod("setName", ResourceLocation.class);
				setName.setAccessible(true);
				for (Method m : IntIdentityHashBiMap.class.getDeclaredMethods())
					if (m.getName().equals("getIndex")) {
						getIndex = m;
						getIndex.setAccessible(true);
						break;
					}
				initialised = true;
			}
			if (thing.getRegistryName() == null) Reference.print(LogType.ERROR, "Attempted to register an object without a registry name!", thing);
			else {
				V oldThing = registry.getObject(thing.getRegistryName());
				if (oldThing == null) registry.register(thing);
				else {
					int id = registry.getId(oldThing);
					setName.invoke((Delegate<V>) ((IForgeRegistryEntry.Impl<V>) thing).delegate, thing.getRegistryName());
					IntIdentityHashBiMap<V> map = (IntIdentityHashBiMap<V>) underlyingIntegerMap.get(registry);
					int index = -1;
					Object[] valuesArray = (Object[]) values.get(map);
					for (int i = 0; i < valuesArray.length; i++)
						if (valuesArray[i] == oldThing) {
							index = i;
							break;
						}
					if (index == -1) Reference.print(LogType.WARN, "No index for an old object could be found, object could not be registered.");
					else {
						Reference.print(LogType.INFO, map.get(id));
						valuesArray[index] = thing;
						map.put(thing, ((int[]) intKeys.get(map))[index]);
						Reference.print(LogType.INFO, map.get(id));
						Map<ResourceLocation, V> registryObjectsMap = (Map<ResourceLocation, V>) registryObjects.get(registry);
						registryObjectsMap.remove(registry.getNameForObject(oldThing));
						registryObjectsMap.put(thing.getRegistryName(), thing);
						((AddCallback<V>) addCallback.get(registry)).onAdd(thing, id, (Map<ResourceLocation, V>) slaves.get(registry));
						Reference.print(LogType.INFO, map.get(id));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
