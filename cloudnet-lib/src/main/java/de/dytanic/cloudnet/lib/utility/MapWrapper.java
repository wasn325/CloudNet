/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class MapWrapper {

    private MapWrapper() {
    }

    public static <K, V> Map<K, V> collectionCatcherHashMap(final Collection<V> key, final Catcher<K, V> catcher) {
        final HashMap<K, V> kvHashMap = new HashMap<>();
        for (final V value : key) {
            kvHashMap.put(catcher.doCatch(value), value);
        }
        return kvHashMap;
    }

    public static <K, V> Map<K, V> filter(final Map<K, V> map, final Acceptable<V> acceptable) {
        final Map<K, V> filter = NetworkUtils.newConcurrentHashMap();
        for (final Map.Entry<K, V> value : map.entrySet()) {
            if (acceptable.isAccepted(value.getValue())) {
                filter.put(value.getKey(), value.getValue());
            }
        }
        return filter;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> valueableHashMap(final Return<K, V>... returns) {
        final HashMap<K, V> map = new HashMap<>();
        for (final Return<K, V> kvReturn : returns) {
            map.put(kvReturn.getFirst(), kvReturn.getSecond());
        }
        return map;
    }

    public static <K, V, NK, VK> Map<NK, VK> transform(final Map<K, V> values,
                                                       final Catcher<NK, K> keyCatcher,
                                                       final Catcher<VK, V> valueCatcher) {
        final Map<NK, VK> nkvkMap = new HashMap<>();
        for (final Map.Entry<K, V> entry : values.entrySet()) {
            nkvkMap.put(keyCatcher.doCatch(entry.getKey()), valueCatcher.doCatch(entry.getValue()));
        }
        return nkvkMap;
    }

}
