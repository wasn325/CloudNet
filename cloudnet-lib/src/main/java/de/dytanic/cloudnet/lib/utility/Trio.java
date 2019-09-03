/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

public class Trio<F, S, T> {

    private final F first;

    private final S second;

    private final T third;

    public Trio(final F first, final S second, final T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getThird() {
        return third;
    }

    public S getSecond() {
        return second;
    }

    public F getFirst() {
        return first;
    }
}
