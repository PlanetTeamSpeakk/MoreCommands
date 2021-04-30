package com.ptsmods.morecommands.clientoption;

public class IntegerClientOption extends ClientOption<Integer> {
    IntegerClientOption(Integer defaultValue) {
        super(defaultValue);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
