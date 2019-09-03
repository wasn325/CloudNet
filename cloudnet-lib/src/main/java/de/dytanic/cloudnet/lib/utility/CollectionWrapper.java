/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CollectionWrapper {

    private CollectionWrapper() {
    }

    public static <E, X> Collection<X> transform(final Collection<E> collection, final Catcher<X, E> catcher) {
        final Collection<X> xCollection = newCopyOnWriteArrayList();
        for (final E e : collection) {
            xCollection.add(catcher.doCatch(e));
        }
        return xCollection;
    }

    public static <E> java.util.List<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <E> Collection<E> filterMany(final Collection<E> elements, final Acceptable<E> acceptable) {
        final Collection<E> collection = new LinkedList<>();
        for (final E element : elements) {
            if (acceptable.isAccepted(element)) {
                collection.add(element);
            }
        }
        return collection;
    }

    public static <E> E filter(final Collection<E> elements, final Acceptable<E> acceptable) {
        for (final E element : elements) {
            if (acceptable.isAccepted(element)) {
                return element;
            }
        }
        return null;
    }

    public static <E> CopyOnWriteArrayList<E> transform(final Collection<E> defaults) {
        return new CopyOnWriteArrayList<>(defaults);
    }

    public static Collection<String> toCollection(final String input, final String splitter) {
        return new CopyOnWriteArrayList<>(input.split(splitter));
    }

    public static <E> void iterator(final Collection<E> collection, final Runnabled<E>... runnableds) {
        for (final E el : collection) {
            for (final Runnabled<E> runnabled : runnableds) {
                runnabled.run(el);
            }
        }
    }

    public static <E> void iterator(final E[] collection, final Runnabled<E>... runnableds) {
        for (final E el : collection) {
            for (final Runnabled<E> runnabled : runnableds) {
                runnabled.run(el);
            }
        }
    }

    public static <E, X, C> Collection<E> getCollection(final java.util.Map<X, C> map, final Catcher<E, C> catcher) {
        final Collection<E> collection = new LinkedList<>();
        for (final C values : map.values()) {
            collection.add(catcher.doCatch(values));
        }
        return collection;
    }

    public static <E> void checkAndRemove(final Collection<E> collection, final Acceptable<E> acceptable) {
        E e = null;
        for (final E element : collection) {
            if (acceptable.isAccepted(element)) {
                e = element;
            }
        }

        if (e != null) {
            collection.remove(e);
        }

    }

    public static <E> void iterator(final E[] values, final Runnabled<E> handled) {
        for (final E value : values) {
            handled.run(value);
        }
    }

    public static <E> boolean equals(final E[] array, final E value) {
        for (final E a : array) {
            if (a.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <E> int filled(final E[] array) {
        int i = 0;
        for (final E element : array) {
            if (element != null) {
                i++;
            }
        }
        return i;
    }

    public static <E> boolean isEmpty(final E[] array) {
        for (final E element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }

    public static <E> void remove(final E[] array, final E element) {
        final int i = index(array, element);
        array[i] = null;
    }

    public static <E> int index(final E[] array, final E element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return 0;
    }

}
