package com.mercury.discovery.common.excel;

public class DefaultValueTransformer<T> implements ValueTransformer<T> {

    @Override
    public Object transform(String name, Object value, T row) {
        return value;
    }
}
