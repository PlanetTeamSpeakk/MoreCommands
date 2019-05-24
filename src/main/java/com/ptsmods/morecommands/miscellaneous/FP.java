package com.ptsmods.morecommands.miscellaneous;

public class FP {

	public boolean	isFake	= false;
	public String	name	= "";

	public FP() {}

	public void set(boolean isFake, String name) {
		this.isFake = isFake;
		this.name = name;
	}

	public void copyFrom(FP source) {
		isFake = source.isFake;
		name = source.name;
	}

}
