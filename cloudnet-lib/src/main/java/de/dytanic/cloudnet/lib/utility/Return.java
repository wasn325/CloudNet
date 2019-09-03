package de.dytanic.cloudnet.lib.utility;

/**
 * Created by Tareko on 25.05.2017.
 */
public class Return<F, S> {

    private final F first;
    private final S second;

    public Return(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
