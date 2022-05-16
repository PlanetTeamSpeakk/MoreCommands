package com.ptsmods.morecommands.api;

public interface IRainbow {

	@SuppressWarnings("deprecation") // Not API
	static IRainbow get() {
		return Holder.getRainbow();
	}

	int getRainbowColour(boolean includeIndex);

	int getRainbowColour(boolean includeIndex, float transparency);
}
