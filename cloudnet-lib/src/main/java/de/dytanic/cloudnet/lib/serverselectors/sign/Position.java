/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.sign;

import java.util.Objects;

/**
 * Created by Tareko on 26.05.2017.
 */
public class Position {

    private final String group;
    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public Position(final String group, final String world, final double x, final double y, final double z) {
        this.group = group;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = group != null ? group.hashCode() : 0;
        result = 31 * result + (world != null ? world.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        final Position position = (Position) o;
        return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0 && Double.compare(position.z, z) == 0 &&
               Objects.equals(group, position.group) && Objects.equals(world, position.world);
    }
}
