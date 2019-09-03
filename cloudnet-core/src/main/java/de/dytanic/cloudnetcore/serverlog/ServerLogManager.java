/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.serverlog;

import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.map.NetorHashMap;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 04.10.2017.
 */
public final class ServerLogManager implements Runnable {

    private final NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> screenInfos = new NetorHashMap<>();

    public void append(final String rnd, final String serverId) {
        for (final String key : screenInfos.keySet()) {
            if (this.screenInfos.getF(key).getFirst().equals(serverId)) {
                screenInfos.add(rnd,
                                new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)),
                                new ConcurrentLinkedQueue<>(this.screenInfos.getS(key)));
                return;
            }
        }

        final MinecraftServer minecraftServer = CloudNet.getInstance().getServer(serverId);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().enableScreen(minecraftServer.getServerInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
            return;
        }

        final ProxyServer proxyServer = CloudNet.getInstance().getProxy(serverId);
        if (proxyServer != null) {
            proxyServer.getWrapper().enableScreen(proxyServer.getProxyInfo());
            screenInfos.add(rnd, new MultiValue<>(serverId, (System.currentTimeMillis() + 600000L)), new ConcurrentLinkedQueue<>());
        }
    }

    public void appendScreenData(final Collection<ScreenInfo> screenInfos) {
        if (!screenInfos.isEmpty()) {
            for (final ScreenInfo screenInfo : screenInfos) {
                for (final String key : this.screenInfos.keySet()) {
                    if (this.screenInfos.getF(key).getFirst().equalsIgnoreCase(screenInfo.getServiceId().getServerId())) {
                        this.screenInfos.getS(key).addAll(screenInfos);

                        while (this.screenInfos.getS(key).size() >= 64) {
                            this.screenInfos.getS(key).poll();
                        }
                    }
                }
            }
        }
    }

    public String dispatch(final String rnd) {
        if (!this.screenInfos.contains(rnd)) {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();

        for (final ScreenInfo screenInfo : this.screenInfos.getS(rnd)) {
            stringBuilder.append("<p>").append(screenInfo.getLine()).append("</p>");
        }

        return stringBuilder.toString();
    }

    @Deprecated
    @Override
    public void run() {
        for (final String key : screenInfos.keySet()) {
            if (screenInfos.getF(key).getSecond() < System.currentTimeMillis()) {
                final String server = screenInfos.getF(key).getFirst();

                final MinecraftServer minecraftServer = CloudNet.getInstance().getServer(server);
                if (minecraftServer != null) {
                    minecraftServer.getWrapper().disableScreen(minecraftServer.getServerInfo());
                }

                final ProxyServer proxyServer = CloudNet.getInstance().getProxy(server);
                if (proxyServer != null) {
                    proxyServer.getWrapper().disableScreen(proxyServer.getProxyInfo());
                }

                screenInfos.remove(key);
            }
        }
    }

    public NetorHashMap<String, MultiValue<String, Long>, Queue<ScreenInfo>> getScreenInfos() {
        return screenInfos;
    }
}
