package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.SimpledUser;

import java.util.Map;

/**
 * Created by Tareko on 30.07.2017.
 */
public class WrapperExternal {

    private final CloudNetwork cloudNetwork;

    private final SimpledUser user;

    private final java.util.Map<String, ServerGroup> serverGroups;

    private final java.util.Map<String, ProxyGroup> proxyGroups;

    public WrapperExternal(final CloudNetwork cloudNetwork,
                           final SimpledUser user,
                           final Map<String, ServerGroup> serverGroups,
                           final Map<String, ProxyGroup> proxyGroups) {
        this.cloudNetwork = cloudNetwork;
        this.user = user;
        this.serverGroups = serverGroups;
        this.proxyGroups = proxyGroups;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return proxyGroups;
    }

    public Map<String, ServerGroup> getServerGroups() {
        return serverGroups;
    }

    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    public SimpledUser getUser() {
        return user;
    }
}
