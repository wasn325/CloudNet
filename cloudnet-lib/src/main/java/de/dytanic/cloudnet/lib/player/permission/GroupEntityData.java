package de.dytanic.cloudnet.lib.player.permission;

/**
 * Created by Tareko on 28.07.2017.
 */
public class GroupEntityData {

    private final String group;

    private final long timeout;

    public GroupEntityData(final String group, final long timeout) {
        this.group = group;
        this.timeout = timeout;
    }

    public String getGroup() {
        return group;
    }

    public long getTimeout() {
        return timeout;
    }
}
