package com.ptsmods.morecommands.api.util.text;

import com.ptsmods.morecommands.api.util.compat.Compat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.text.MutableText;

public interface EmptyTextBuilder extends TextBuilder<EmptyTextBuilder> {

	static EmptyTextBuilder builder() {
		return new EmptyTextBuilderImpl();
	}

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	class EmptyTextBuilderImpl extends TextBuilderImpl<EmptyTextBuilder> implements EmptyTextBuilder {

		@Override
		public MutableText build() {
			return Compat.get().buildText(this);
		}

		@Override
		public EmptyTextBuilder copy() {
			return builder()
					.withStyle(getStyle())
					.withChildren(getChildren());
		}

		@Override
		public EmptyTextBuilder upcast() {
			return this;
		}
	}
}
