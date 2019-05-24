package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSnow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class RegistryEventHandler extends EventHandler {

	private static final BlockSnow blockSnow = new BlockFactory<>(BlockSnow.class, new ResourceLocation("minecraft:snow_layer"), "snow").getBlockNoExceptions();

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(new SoundEvent(new ResourceLocation("morecommands:easteregg")).setRegistryName(new ResourceLocation("morecommands:easteregg")));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST) // Making sure other mods have registered their blocks first.
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockFactory(BlockIce.class, new ResourceLocation("minecraft:ice"), "ice").getBlockNoExceptions());
		event.getRegistry().register(blockSnow);
		event.getRegistry().register(Reference.lockedChest);
		for (Block block : Block.REGISTRY)
			if (block.getCreativeTab() == null && block != Blocks.AIR) block.setCreativeTab(Reference.unobtainableItems);
		for (Item item : Item.REGISTRY)
			if (item.getCreativeTab() == null && item != Items.AIR) item.setCreativeTab(Reference.unobtainableItems);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemSnow(blockSnow).setTranslationKey("snow").setRegistryName(new ResourceLocation("minecraft:snow_layer")));
		event.getRegistry().register(new ItemBlock(Reference.lockedChest).setRegistryName(Reference.lockedChest.getRegistryName()));
		for (Block block : Block.REGISTRY)
			if (!event.getRegistry().containsKey(block.getRegistryName())) event.getRegistry().register(new ItemBlock(block).setCreativeTab(Reference.unobtainableItems).setTranslationKey(block.getTranslationKey().substring(5)).setRegistryName(block.getRegistryName()));
		for (Item item : event.getRegistry())
			if (item.getCreativeTab() == null && item != Items.AIR) item.setCreativeTab(Reference.unobtainableItems);
	}

}
