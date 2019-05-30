package com.ptsmods.morecommands.miscellaneous;

public class Percentage extends Number {
	public static final Percentage	MAX_VALUE			= new Percentage(100F);
	public static final Percentage	MIN_VALUE			= new Percentage(0F);
	private static final long		serialVersionUID	= 2937645220837168877L;
	private float					value;

	public Percentage(float value) {
		if (value < 0) throw new IllegalArgumentException("The given value, " + value + ", was smaller than 0.");
		this.value = value;
	}

	public Percentage(double value) {
		this((float) value);
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public String toString() {
		return Float.toString(value) + "%";
	}

	public void add(float arg1) {
		if (value + arg1 < 100 && value + arg1 > 0) value += arg1;
		else if (value + arg1 > 100) value = 100F;
		else value = 0F;
	}

	public void add(Percentage arg1) {
		add(arg1.floatValue());
	}

	public void subtract(float arg1) {
		if (value - arg1 < 100 && value - arg1 > 0) value -= arg1;
		else if (value - arg1 > 100) value = 100F;
		else value = 0F;
	}

	public void subtract(Percentage arg1) {
		subtract(arg1.floatValue());
	}

}
