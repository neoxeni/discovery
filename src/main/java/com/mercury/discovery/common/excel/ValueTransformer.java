package com.mercury.discovery.common.excel;

@FunctionalInterface
public interface ValueTransformer<T> {
    Object transform(String name, Object value, T row);
}
