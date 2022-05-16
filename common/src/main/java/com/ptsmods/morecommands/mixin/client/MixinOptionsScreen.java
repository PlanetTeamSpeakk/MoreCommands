package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.gui.ClientOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
	protected MixinOptionsScreen(Text title) {
		super(title);
	}

	@Inject(at = @At("TAIL"), method = "init()V")
	public void init(CallbackInfo cbi) {
		int x, y;
		//  under accessibility settings													 under resourcepacks
		if (getButtonAt(x = this.width / 2 + 5, y = this.height / 6 + 144 - 6) != null && getButtonAt(x = this.width / 2 - 155, y) != null) {
			x = this.width / 2 + 5; // above sounds
			y = this.height / 6 + 24 - 6;
		}

		((ScreenAddon) this).mc$addButton(new ButtonWidget(x, y, 150, 20, LiteralTextBuilder.builder("MoreCommands", MoreCommands.DS).build(),
				button -> MinecraftClient.getInstance().setScreen(new ClientOptionsScreen(this))));
	}

	@Unique
	private ClickableWidget getButtonAt(int x, int y) {
		for (ClickableWidget b : ((ScreenAddon) this).mc$getButtons())
			if (b.x == x && b.y == y) return b;
		return null;
	}
}
