/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.map;

import java.util.HashMap;

/**
 * Created by Tareko on 14.09.2017.
 */
public final class WrappedMap extends HashMap<String, Object> {

    public WrappedMap append(final String key, final Object value) {
        put(key, value);
        return this;
    }

    public Integer getInt(final String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Integer.parseInt(get(key).toString());
    }

    public Boolean getBoolean(final String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Boolean.parseBoolean(get(key).toString());
    }

    public String getString(final String key) {
        if (!containsKey(key)) {
            return null;
        }
        return get(key).toString();
    }

    public Double getDouble(final String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Double.parseDouble(get(key).toString());
    }

    public Float getFloat(final String key) {
        if (!containsKey(key)) {
            return null;
        }
        return Float.parseFloat(get(key).toString());
    }
}
