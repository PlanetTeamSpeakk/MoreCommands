package com.ptsmods.morecommands.api.clientoptions;

import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class BooleanClientOption extends ClientOption<Boolean> {
	public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue) {
		super(category, name, defaultValue);
	}

	public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, BiConsumer<Boolean, Boolean> updateConsumer) {
		super(category, name, defaultValue, updateConsumer);
	}

	public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, String... comments) {
		super(category, name, defaultValue, comments);
	}

	public BooleanClientOption(ClientOptionCategory category, String name, boolean defaultValue, BiConsumer<Boolean, Boolean> updateConsumer, String... comments) {
		super(category, name, defaultValue, updateConsumer, comments);
	}

	@Override
	public String getValueString() {
		return String.valueOf(getValueRaw());
	}

	@Override
	public void setValueString(String s) {
		setValue("true".equals(s));
	}

	@Override
	public Object createButton(int x, int y, String name, Runnable init, Runnable save) {
		return new ButtonWidget(x, y, 150, 20, createButtonText(name), btn -> {
			setValue(!getValueRaw());
			btn.setMessage(createButtonText(name));
			init.run();
			save.run();
		});
	}

	@Override
	public Text createButtonText(String name) {
		return LiteralTextBuilder.builder(name + " : " + Util.formatFromBool(getValueRaw()) + String.valueOf(getValueRaw()).toUpperCase()).build();
	}
}
