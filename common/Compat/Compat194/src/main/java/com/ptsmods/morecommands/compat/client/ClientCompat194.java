package com.ptsmods.morecommands.compat.client;

import com.ptsmods.morecommands.api.util.compat.Compat;
import dev.architectury.registry.CreativeTabRegistry;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClientCompat194 extends ClientCompat193 {
    private static CreativeTabRegistry.TabSupplier unobtainableItemsTab;

    @Override
    public void setFocused(EditBox editBox, boolean focused) {
        editBox.setFocused(focused);
    }

    @Override
    public void registerUnobtainableItemsTab() {
        if (unobtainableItemsTab != null) return;

        unobtainableItemsTab = CreativeTabRegistry.create(new ResourceLocation("morecommands:unobtainable_items"), builder -> builder
                .icon(() -> new ItemStack(Compat.get().<Item>getBuiltInRegistry("item")
                        .get(new ResourceLocation("morecommands:locked_chest"))))
                .displayItems(((flags, output) -> {
                    for (Item item : Compat.get().<Item>getBuiltInRegistry("item"))
                        if (item != Items.AIR && CreativeModeTabs.allTabs().stream().noneMatch(tab -> tab.contains(new ItemStack(item)))) output.accept(item);
                })));
    }
}
