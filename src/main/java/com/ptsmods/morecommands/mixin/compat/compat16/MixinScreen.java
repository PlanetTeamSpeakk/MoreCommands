package com.ptsmods.morecommands.mixin.compat.compat16;

import com.ptsmods.morecommands.mixin.addons.ScreenAddon;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Pseudo
@Mixin(targets = "net/minecraft/class_437", remap = false)
public class MixinScreen implements ScreenAddon {
	@Shadow protected @Final List<Element> field_22786; // children
	@Shadow protected @Final List<ClickableWidget> field_22791; // buttons

	@Override
	public void mc$clear() {
		field_22786.clear();
		field_22791.clear();
	}

	@Override
	public List<ClickableWidget> mc$getButtons() {
		return field_22791;
	}

	@Override
	public <T extends ClickableWidget> T mc$addButton(T button) {
		field_22791.add(button);
		field_22786.add(button);
		return button;
	}
}
