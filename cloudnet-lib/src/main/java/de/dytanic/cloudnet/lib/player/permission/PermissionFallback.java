package de.dytanic.cloudnet.lib.player.permission;

/**
 * Created by Tareko on 06.07.2017.
 */
public class PermissionFallback {

    private final boolean enabled;
    private final String fallback;

    public PermissionFallback(final boolean enabled, final String fallback) {
        this.enabled = enabled;
        this.fallback = fallback;
    }

    public String getFallback() {
        return fallback;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
