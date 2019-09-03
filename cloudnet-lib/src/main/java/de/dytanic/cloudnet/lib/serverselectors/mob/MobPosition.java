/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.mob;

/**
 * Created by Tareko on 02.09.2017.
 */
public class MobPosition {

    private final String group;

    private final String world;

    private final double x;

    private final double y;

    private final double z;

    private final float yaw;

    private final float pitch;

    public MobPosition(final String group,
                       final String world,
                       final double x,
                       final double y,
                       final double z,
                       final float yaw,
                       final float pitch) {
        this.group = group;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getGroup() {
        return group;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }
}
