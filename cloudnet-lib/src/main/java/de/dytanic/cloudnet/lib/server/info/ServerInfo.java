package de.dytanic.cloudnet.lib.server.info;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Tareko on 24.05.2017.
 */
public class ServerInfo {
    public static final Type TYPE = new TypeToken<ServerInfo>() {}.getType();
    private final ServiceId serviceId;
    private final String host;
    private final int port;
    private final boolean online;
    private final List<String> players;
    private final int memory;
    private String motd;
    private final int onlineCount;
    private final int maxPlayers;
    private ServerState serverState;
    private final ServerConfig serverConfig;
    private final Template template;

    public ServerInfo(final ServiceId serviceId,
                      final String host,
                      final int port,
                      final boolean online,
                      final List<String> players,
                      final int memory,
                      final String motd,
                      final int onlineCount,
                      final int maxPlayers,
                      final ServerState serverState,
                      final ServerConfig serverConfig,
                      final Template template) {
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
        this.online = online;
        this.players = players;
        this.memory = memory;
        this.motd = motd;
        this.onlineCount = onlineCount;
        this.maxPlayers = maxPlayers;
        this.serverState = serverState;
        this.serverConfig = serverConfig;
        this.template = template;
    }

    @Override
    public String toString() {
        return "ServerInfo{" + "serviceId=" + serviceId + ", host='" + host + '\'' + ", port=" + port + ", online=" + online + ", players=" + players + ", memory=" + memory + ", motd='" + motd + '\'' + ", onlineCount=" + onlineCount + ", maxPlayers=" + maxPlayers + ", serverState=" + serverState + ", serverConfig=" + serverConfig + ", template=" + template + '}';
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public int getPort() {
        return port;
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public String getHost() {
        return host;
    }

    public String getMotd() {
        return motd;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isIngame() {

        if (serverState == null) {
            serverState = ServerState.LOBBY;
        }

        if (motd == null) {
            motd = "null";
        }

        return serverState == ServerState.INGAME || (motd.equalsIgnoreCase("INGAME") || motd.equalsIgnoreCase("RUNNING"));
    }

    public SimpleServerInfo toSimple() {
        return new SimpleServerInfo(serviceId, host, port, onlineCount, maxPlayers);
    }

}
