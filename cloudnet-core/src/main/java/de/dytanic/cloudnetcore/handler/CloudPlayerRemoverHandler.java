package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by Tareko on 20.11.2017.
 */
public class CloudPlayerRemoverHandler implements ICloudHandler {

    @Override
    public void onHandle(final CloudNet cloudNet) {

        final Collection<UUID> collection = new ArrayList<>();

        for (final ProxyServer proxyServer : CloudNet.getInstance().getProxys().values()) {
            CollectionWrapper.iterator(proxyServer.getProxyInfo().getPlayers(), new Runnabled<MultiValue<UUID, String>>() {
                @Override
                public void run(final MultiValue<UUID, String> obj) {
                    collection.add(obj.getFirst());
                }
            });
        }

        for (final CloudPlayer entries : CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values()) {
            if (!collection.contains(entries.getUniqueId())) {
                CloudNet.getInstance().getNetworkManager().getOnlinePlayers().remove(entries.getUniqueId());
            }
        }

    }

    @Override
    public int getTicks() {
        return 10;
    }
}
