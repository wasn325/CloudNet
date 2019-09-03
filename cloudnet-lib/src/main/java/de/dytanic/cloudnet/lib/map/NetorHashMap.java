package de.dytanic.cloudnet.lib.map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NetorHashMap<Key, VF, VS> implements NetorMap<Key> {

    private final ConcurrentHashMap<Key, NetorSet<VF, VS>> values = new ConcurrentHashMap<>();

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void remove(final Key key) {
        values.remove(key);
    }

    @Override
    public boolean contains(final Key key) {
        return values.containsKey(key);
    }

    @Override
    public Set<Key> keySet() {
        return values.keySet();
    }

    public void add(final Key key, final VF valueF, final VS valueS) {
        values.put(key, new NetorSet<>(valueF, valueS));
    }

    public VF getF(final Key key) {
        return values.get(key).getFirstValue();
    }

    public VS getS(final Key key) {
        return values.get(key).getSecondValue();
    }

    public void updateF(final Key key, final VF value) {
        values.get(key).updateFirst(value);
    }

    public void updateS(final Key key, final VS value) {
        values.get(key).updateSecond(value);
    }

}
