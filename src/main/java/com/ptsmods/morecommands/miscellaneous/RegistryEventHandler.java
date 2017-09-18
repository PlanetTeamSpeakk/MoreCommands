package com.ptsmods.morecommands.miscellaneous;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid=Reference.MOD_ID)
public class RegistryEventHandler extends EventHandler {

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(new SoundEvent(new ResourceLocation("morecommands:easteregg")).setRegistryName(new ResourceLocation("morecommands:easteregg")));
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new BlockFactory(BlockIce.class, "minecraft:ice", "ice").getBlockNoExceptions());
		event.getRegistry().register(new BlockFactory(BlockSnow.class, "minecraft:snow_layer", "snow").getBlockNoExceptions());
		event.getRegistry().register(Reference.lockedChest);
		for (Block block : Block.REGISTRY) if (block.getCreativeTabToDisplayOn() == null) block.setCreativeTab(Reference.unobtainableItems);
		for (Item item : Item.REGISTRY) if (item.getCreativeTab() == null) item.setCreativeTab(Reference.unobtainableItems);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(Reference.lockedChest).setRegistryName(Reference.lockedChest.getRegistryName()));
		for (Block block : Block.REGISTRY) if (!Item.REGISTRY.containsKey(block.getRegistryName())) event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

}
