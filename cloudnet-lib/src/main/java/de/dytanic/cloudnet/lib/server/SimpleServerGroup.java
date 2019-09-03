package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;

import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
public class SimpleServerGroup implements Nameable {

    private final String name;

    private final boolean kickedForceFallback;

    private final int joinPower;

    private final int memory;

    private final ServerGroupMode mode;

    private final boolean maintenance;

    private final int percentForNewServerAutomatically;

    private final Map<String, Object> settings;

    private final AdvancedServerConfig advancedServerConfig;

    public SimpleServerGroup(final String name,
                             final boolean kickedForceFallback,
                             final int joinPower,
                             final int memory,
                             final ServerGroupMode mode,
                             final boolean maintenance,
                             final int percentForNewServerAutomatically,
                             final Map<String, Object> settings,
                             final AdvancedServerConfig advancedServerConfig) {
        this.name = name;
        this.kickedForceFallback = kickedForceFallback;
        this.joinPower = joinPower;
        this.memory = memory;
        this.mode = mode;
        this.maintenance = maintenance;
        this.percentForNewServerAutomatically = percentForNewServerAutomatically;
        this.settings = settings;
        this.advancedServerConfig = advancedServerConfig;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMemory() {
        return memory;
    }

    public AdvancedServerConfig getAdvancedServerConfig() {
        return advancedServerConfig;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public int getPercentForNewServerAutomatically() {
        return percentForNewServerAutomatically;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public ServerGroupMode getMode() {
        return mode;
    }

    public boolean isKickedForceFallback() {
        return kickedForceFallback;
    }

    public boolean isMaintenance() {
        return maintenance;
    }
}
