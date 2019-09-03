package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitServerAddEvent;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedServerAddEvent;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.event.EventHandler;

/**
 * Created by Tareko on 16.12.2017.
 */
public class ServerGetExample {


    public void handle(final ProxiedServerAddEvent e) {
        if (e.getServerInfo().getServerConfig().getProperties().contains("myUUID") &&
            e.getServerInfo().getServerConfig().getProperties().getString("myUUID").equals("test")) {
            final ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }

        if (e.getServerInfo().getServiceId().getServerId().equals("Lobby-1")) {
            final ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }
    }

    @EventHandler
    public void handle(final BukkitServerAddEvent e) {
        /* - // - */
    }

    public void start() {
        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData("Lobby"),
                                               new ServerConfig(true, "4820", new Document("myUUID", "test"), System.currentTimeMillis()));
        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData("Lobby"),
                                               new ServerConfig(true, "4820", new Document("myUUID", "test"), System.currentTimeMillis()),
                                               "Lobby-1");
    }

}
