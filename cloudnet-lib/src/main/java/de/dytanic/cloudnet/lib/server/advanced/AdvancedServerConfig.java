/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.advanced;

/**
 * Created by Tareko on 16.09.2017.
 */
public class AdvancedServerConfig {

    private final boolean notifyPlayerUpdatesFromNoCurrentPlayer;

    private final boolean notifyProxyUpdates;

    private final boolean notifyServerUpdates;

    private final boolean disableAutoSavingForWorlds;

    public AdvancedServerConfig(final boolean notifyPlayerUpdatesFromNoCurrentPlayer,
                                final boolean notifyProxyUpdates,
                                final boolean notifyServerUpdates,
                                final boolean disableAutoSavingForWorlds) {
        this.notifyPlayerUpdatesFromNoCurrentPlayer = notifyPlayerUpdatesFromNoCurrentPlayer;
        this.notifyProxyUpdates = notifyProxyUpdates;
        this.notifyServerUpdates = notifyServerUpdates;
        this.disableAutoSavingForWorlds = disableAutoSavingForWorlds;
    }

    public boolean isDisableAutoSavingForWorlds() {
        return disableAutoSavingForWorlds;
    }

    public boolean isNotifyPlayerUpdatesFromNoCurrentPlayer() {
        return notifyPlayerUpdatesFromNoCurrentPlayer;
    }

    public boolean isNotifyProxyUpdates() {
        return notifyProxyUpdates;
    }

    public boolean isNotifyServerUpdates() {
        return notifyServerUpdates;
    }
}
