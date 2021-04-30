package com.ptsmods.morecommands.clientoption;

public class BooleanClientOption extends ClientOption<Boolean> {
    BooleanClientOption(Boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
