package com.ptsmods.morecommands.api.text;

import com.ptsmods.morecommands.api.Holder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

public interface LiteralTextBuilder extends TextBuilder<LiteralTextBuilder> {

	String getLiteral();
	void setLiteral(String literal);

	static LiteralTextBuilder builder(String literal) {
		return new LiteralTextBuilderImpl(literal);
	}

	static LiteralTextBuilder builder(String literal, Style style) {
		return new LiteralTextBuilderImpl(literal, style);
	}

	class LiteralTextBuilderImpl extends TextBuilderImpl<LiteralTextBuilder> implements LiteralTextBuilder {
		private String literal;

		LiteralTextBuilderImpl(String literal) {
			this.literal = literal;
		}

		LiteralTextBuilderImpl(String literal, Style style) {
			this(literal);
			withStyle(style);
		}

		@Override
		public MutableText build() {
			return Holder.getCompat().buildText(this);
		}

		@Override
		public LiteralTextBuilder copy() {
			return builder(getLiteral())
					.withStyle(getStyle())
					.withChildren(getChildren());
		}

		@Override
		public final LiteralTextBuilder upcast() {
			return this;
		}

		@Override
		public String getLiteral() {
			return literal;
		}

		@Override
		public void setLiteral(String literal) {
			this.literal = literal;
		}
	}
}
