package com.mercury.discovery.common.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * HashMap의 형변환을 좀더 쉽게 하기 위해 유틸 method를 가지고 있는 Map
 */
@Slf4j
public class DataMap extends HashMap<Object, Object> {
    private static final long serialVersionUID = -7700790403928325864L;

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(Object key, Integer defaultValue) {
        Object x = get(key);
        if(x == null){
            return defaultValue;
        }
        try {
            if (x instanceof Integer) {
                return (Integer) x;
            } else if (x instanceof Long) {
                return ((Long) x).intValue();
            } else {
                String val = x.toString().trim();
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException nfe) {
            log.error("CamelMap NumberFormatException in getInt({}) {}", key, x);
            return defaultValue;
        }
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(Object key, Long defaultValue) {
        Object x = get(key);
        if(x == null){
            return defaultValue;
        }

        try {
            if (x instanceof Integer) {
                return ((Integer) x).longValue();
            } else if (x instanceof Long) {
                return (Long) x;
            } else {
                String val = x.toString().trim();
                return Long.parseLong(val);
            }
        } catch (NumberFormatException nfe) {
            log.error("CamelMap NumberFormatException in getLong({}) {}", key, x);
            return defaultValue;
        }
    }

    public Float getFloat(Object key) {
        return getFloat(key, null);
    }

    public Float getFloat(Object key, Float defaultValue) {
        Object x = get(key);
        if(x == null){
            return defaultValue;
        }

        try {
            if (x instanceof Float) {
                return (Float) x;
            } else if (x instanceof Double) {
                return ((Double) x).floatValue();
            } else {
                String val = x.toString().trim();
                return Float.parseFloat(val);
            }
        } catch (NumberFormatException nfe) {
            log.error("CamelMap NumberFormatException in getFloat({}) {}", key, x);
            return defaultValue;
        }
    }

    public Double getDouble(Object key) {
        return getDouble(key, null);
    }

    public Double getDouble(Object key, Double defaultValue) {
        Object x = get(key);
        if(x == null){
            return defaultValue;
        }

        try {
            return Double.parseDouble(x.toString());
        } catch (NumberFormatException e) {
            log.error("CamelMap NumberFormatException in getDouble({}) {}", key, x);
            return defaultValue;
        }
    }

    public String getString(Object key) {
        return getString(key, null);
    }

    public String getString(Object key, String defaultValue) {
        Object x = get(key);
        if(x == null){
            return defaultValue;
        }

        try {
            return String.valueOf(x);
        } catch (Exception e) {
            log.error("CamelMap Exception in getString({}) {}", key, x);
            return defaultValue;
        }
    }
}
