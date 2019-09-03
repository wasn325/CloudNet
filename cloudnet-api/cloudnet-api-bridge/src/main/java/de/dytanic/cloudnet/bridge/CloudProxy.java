/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.bridge.event.proxied.*;
import de.dytanic.cloudnet.bridge.internal.chat.PlayerChatExecutor;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This Class represents the Proxy Instance on based on cloudnet
 */
public class CloudProxy implements ICloudService, PlayerChatExecutor {

    private static CloudProxy instance;

    private final ProxiedBootstrap proxiedBootstrap;
    private final ProxyProcessMeta proxyProcessMeta;

    private final Map<String, ServerInfo> cachedServers = NetworkUtils.newConcurrentHashMap();
    private final Map<UUID, CloudPlayer> cloudPlayers = NetworkUtils.newConcurrentHashMap();

    public CloudProxy(final ProxiedBootstrap proxiedBootstrap, final CloudAPI cloudAPI) {
        instance = this;

        this.proxiedBootstrap = proxiedBootstrap;
        this.proxyProcessMeta = cloudAPI.getConfig().getObject("proxyProcess", new TypeToken<ProxyProcessMeta>() {}.getType());
        cloudAPI.getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl());
        ProxyServer.getInstance().getScheduler().schedule(proxiedBootstrap, new Runnable() {
            @Override
            public void run() {
                NetworkUtils.addAll(cachedServers,
                                    MapWrapper.collectionCatcherHashMap(cloudAPI.getServers(), new Catcher<String, ServerInfo>() {
                                        @Override
                                        public String doCatch(final ServerInfo key) {
                                            ProxyServer.getInstance().getServers().put(key.getServiceId().getServerId(),
                                                                                       ProxyServer.getInstance()
                                                                                                  .constructServerInfo(key.getServiceId()
                                                                                                                          .getServerId(),
                                                                                                                       new InetSocketAddress(
                                                                                                                           key.getHost(),
                                                                                                                           key.getPort()),
                                                                                                                       "CloudNet2 Game-Server",
                                                                                                                       false));

                                            if (key.getServiceId().getGroup().equalsIgnoreCase(getProxyGroup().getProxyConfig()
                                                                                                              .getDynamicFallback()
                                                                                                              .getDefaultFallback())) {
                                                CollectionWrapper.iterator(ProxyServer.getInstance().getConfig().getListeners(),
                                                                           new Runnabled<ListenerInfo>() {
                                                                               @Override
                                                                               public void run(final ListenerInfo obj) {
                                                                                   obj.getServerPriority().add(key.getServiceId()
                                                                                                                  .getServerId());
                                                                               }
                                                                           });
                                            }
                                            return key.getServiceId().getServerId();
                                        }
                                    }));

                cloudAPI.setCloudService(CloudProxy.this);

            }
        }, 250, TimeUnit.MILLISECONDS);
    }

    public ProxyGroup getProxyGroup() {
        return CloudAPI.getInstance().getProxyGroupData(CloudAPI.getInstance().getServiceId().getGroup());
    }

    public String fallback(final ProxiedPlayer cloudPlayer) {

        for (final ServerFallback serverFallback : CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                             .getFallbacks()) {
            if (serverFallback.getGroup().equals(CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                           .getDefaultFallback())) {
                continue;
            }

            if (serverFallback.getPermission() != null) {
                if (!cloudPlayer.hasPermission(serverFallback.getPermission())) {
                    continue;
                }

                final List<String> servers = CloudProxy.getInstance().getServers(serverFallback.getGroup());
                if (!servers.isEmpty()) {
                    return servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                }
            }
        }

        final String fallback = getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback();
        final List<String> liste = new ArrayList<>(MapWrapper.filter(cachedServers, new Acceptable<ServerInfo>() {
            @Override
            public boolean isAccepted(final ServerInfo value) {
                return value.getServiceId().getGroup().equalsIgnoreCase(fallback);
            }
        }).keySet());

        if (liste.isEmpty()) {
            return null;
        } else {
            return liste.get(NetworkUtils.RANDOM.nextInt(liste.size()));
        }
    }

    /**
     * Returns the instance which respens the api
     *
     * @return
     */
    public static CloudProxy getInstance() {
        return instance;
    }

    public List<String> getServers(final String group) {
        final List<String> x = new ArrayList<>();
        for (final ServerInfo server : this.getCachedServers().values()) {
            if (server.getServiceId().getGroup().equalsIgnoreCase(group)) {
                x.add(server.getServiceId().getServerId());
            }
        }
        return x;
    }

    /**
     * Returns the Servers on cloudnet
     *
     * @return
     */
    public Map<String, ServerInfo> getCachedServers() {
        return cachedServers;
    }

    public String fallback(final ProxiedPlayer cloudPlayer, final String kickedFrom) {

        for (final ServerFallback serverFallback : CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                             .getFallbacks()) {
            if (serverFallback.getGroup().equals(CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                           .getDefaultFallback())) {
                continue;
            }

            if (serverFallback.getPermission() != null) {
                if (!cloudPlayer.hasPermission(serverFallback.getPermission())) {
                    continue;
                }

                final List<String> servers = CloudProxy.getInstance().getServers(serverFallback.getGroup());
                servers.remove(kickedFrom);
                if (!servers.isEmpty()) {
                    return servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                }
            }
        }

        final String fallback = getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback();
        final List<String> liste = new ArrayList<>(MapWrapper.filter(cachedServers, new Acceptable<ServerInfo>() {
            @Override
            public boolean isAccepted(final ServerInfo value) {
                return value.getServiceId().getGroup().equalsIgnoreCase(fallback);
            }
        }).keySet());
        liste.remove(kickedFrom);

        if (liste.isEmpty()) {
            return null;
        } else {
            return liste.get(NetworkUtils.RANDOM.nextInt(liste.size()));
        }
    }

    public String fallbackOnEnabledKick(final ProxiedPlayer cloudPlayer, final String group, final String kickedFrom) {

        for (final ServerFallback serverFallback : CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                             .getFallbacks()) {
            if (serverFallback.getGroup().equals(CloudProxy.getInstance().getProxyGroup().getProxyConfig().getDynamicFallback()
                                                           .getDefaultFallback())) {
                continue;
            }

            if (serverFallback.getPermission() != null) {
                if (!cloudPlayer.hasPermission(serverFallback.getPermission())) {
                    continue;
                }

                final List<String> servers = CloudProxy.getInstance().getServers(serverFallback.getGroup());
                servers.remove(kickedFrom);
                if (!servers.isEmpty()) {
                    return servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                }
            }
        }

        {
            final List<String> liste = new ArrayList<>(MapWrapper.filter(cachedServers, new Acceptable<ServerInfo>() {
                @Override
                public boolean isAccepted(final ServerInfo value) {
                    return value.getServiceId().getGroup().equalsIgnoreCase(group);
                }
            }).keySet());
            liste.remove(kickedFrom);
            if (!liste.isEmpty()) {
                return liste.get(NetworkUtils.RANDOM.nextInt(liste.size()));
            }
        }

        final String fallback = getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback();
        final List<String> liste = new ArrayList<>(MapWrapper.filter(cachedServers, new Acceptable<ServerInfo>() {
            @Override
            public boolean isAccepted(final ServerInfo value) {
                return value.getServiceId().getGroup().equalsIgnoreCase(fallback);
            }
        }).keySet());
        liste.remove(kickedFrom);
        if (liste.isEmpty()) {
            return null;
        } else {
            return liste.get(NetworkUtils.RANDOM.nextInt(liste.size()));
        }
    }

    public void updateAsync() {
        proxiedBootstrap.getProxy().getScheduler().runAsync(proxiedBootstrap, new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    public void update() {
        final ProxyInfo proxyInfo = new ProxyInfo(CloudAPI.getInstance().getServiceId(),
                                                  CloudAPI.getInstance().getConfig().getString("host"),
                                                  0,
                                                  true,
                                                  new ArrayList<>(CollectionWrapper.transform(ProxyServer.getInstance().getPlayers(),
                                                                                              new Catcher<MultiValue<UUID, String>, ProxiedPlayer>() {
                                                                                                  @Override
                                                                                                  public MultiValue<UUID, String> doCatch(
                                                                                                      final ProxiedPlayer key) {
                                                                                                      return new MultiValue<>(key.getUniqueId(),
                                                                                                                              key.getName());
                                                                                                  }
                                                                                              })),
                                                  proxyProcessMeta.getMemory(),
                                                  ProxyServer.getInstance().getOnlineCount());
        CloudAPI.getInstance().update(proxyInfo);
    }

    /**
     * Returns the cloudPlayers online
     *
     * @return
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    /**
     * Returns the API of the plugin instance
     *
     * @return
     */
    public Plugin getPlugin() {
        return proxiedBootstrap;
    }

    @Override
    public CloudPlayer getCachedPlayer(final UUID uniqueId) {
        return cloudPlayers.get(uniqueId);
    }

    public CloudPlayer getCachedPlayer(final String name) {
        return CollectionWrapper.filter(this.cloudPlayers.values(), new Acceptable<CloudPlayer>() {
            @Override
            public boolean isAccepted(final CloudPlayer cloudPlayer) {
                return cloudPlayer.getName().equalsIgnoreCase(name);
            }
        });
    }

    @Override
    public boolean isProxyInstance() {
        return true;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return this.getCachedServers();
    }

    private class NetworkHandlerImpl implements NetworkHandler {
        @Override
        public void onServerAdd(final ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerAddEvent(serverInfo));
            ProxyServer.getInstance().getServers().put(serverInfo.getServiceId().getServerId(),
                                                       ProxyServer.getInstance()
                                                                  .constructServerInfo(serverInfo.getServiceId().getServerId(),
                                                                                       new InetSocketAddress(serverInfo.getHost(),
                                                                                                             serverInfo.getPort()),
                                                                                       "CloudNet2 Game-Server",
                                                                                       false));
            if (serverInfo.getServiceId().getGroup().equalsIgnoreCase(getProxyGroup().getProxyConfig().getDynamicFallback()
                                                                                     .getDefaultFallback())) {
                CollectionWrapper.iterator(ProxyServer.getInstance().getConfig().getListeners(), new Runnabled<ListenerInfo>() {
                    @Override
                    public void run(final ListenerInfo obj) {
                        obj.getServerPriority().add(serverInfo.getServiceId().getServerId());
                    }
                });
            }
            cachedServers.put(serverInfo.getServiceId().getServerId(), serverInfo);

            if (CloudAPI.getInstance().getModuleProperties().contains("notifyService") &&
                CloudAPI.getInstance().getModuleProperties().getBoolean("notifyService")) {
                for (final ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork()
                                                                                                      .getMessages()
                                                                                                      .getString("notify-message-server-add")
                                                                                                      .replace("%server%",
                                                                                                          serverInfo.getServiceId()
                                                                                                                    .getServerId())));
                    }
                }
            }

        }

        @Override
        public void onServerInfoUpdate(final ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerInfoUpdateEvent(serverInfo));
            cachedServers.put(serverInfo.getServiceId().getServerId(), serverInfo);
        }

        @Override
        public void onServerRemove(final ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerRemoveEvent(serverInfo));

            try {
                ProxyServer.getInstance().getServers().remove(serverInfo.getServiceId().getServerId());
            } catch (final Throwable ignored) {
            }

            cachedServers.remove(serverInfo.getServiceId().getServerId());

            if (serverInfo.getServiceId().getGroup().equalsIgnoreCase(getProxyGroup().getProxyConfig().getDynamicFallback()
                                                                                     .getDefaultFallback())) {
                CollectionWrapper.iterator(ProxyServer.getInstance().getConfig().getListeners(), new Runnabled<ListenerInfo>() {
                    @Override
                    public void run(final ListenerInfo obj) {
                        obj.getServerPriority().remove(serverInfo.getServiceId().getServerId());
                    }
                });
            }

            if (CloudAPI.getInstance().getModuleProperties().contains("notifyService") &&
                CloudAPI.getInstance().getModuleProperties().getBoolean("notifyService")) {
                for (final ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork()
                                                                                                      .getMessages()
                                                                                                      .getString("notify-message-server-remove")
                                                                                                      .replace("%server%",
                                                                                                          serverInfo.getServiceId()
                                                                                                                    .getServerId())));
                    }
                }
            }
        }

        @Override
        public void onProxyAdd(final ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyAddEvent(proxyInfo));
        }

        @Override
        public void onProxyInfoUpdate(final ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyInfoUpdateEvent(proxyInfo));
        }

        @Override
        public void onProxyRemove(final ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyRemoveEvent(proxyInfo));
        }

        @Override
        public void onCloudNetworkUpdate(final CloudNetwork cloudNetwork) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedCloudNetworkUpdateEvent(cloudNetwork));

            if (cloudNetwork.getProxyGroups().containsKey(CloudAPI.getInstance().getGroup())) {
                final ProxyGroup proxyGroup = cloudNetwork.getProxyGroups().get(CloudAPI.getInstance().getGroup());
                if (proxyGroup.getProxyConfig().isEnabled() && proxyGroup.getProxyConfig().isMaintenance()) {
                    for (final ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                        if (!proxyGroup.getProxyConfig().getWhitelist().contains(proxiedPlayer.getName()) && !proxiedPlayer.hasPermission(
                            "cloudnet.maintenance")) {
                            proxiedPlayer.disconnect(ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork()
                                                                                                         .getMessages()
                                                                                                         .getString("kick-maintenance")));
                        }
                    }
                }
            }

            if (CloudProxy.getInstance().getProxyGroup() != null &&
                CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled()) {
                final TabList tabList = CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList();

                for (final ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    proxiedPlayer.setTabHeader(new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getHeader().replace(
                        "%proxy%",
                        CloudAPI.getInstance().getServerId()).replace("%server%",
                                                                      (proxiedPlayer.getServer() != null
                                                                       ? proxiedPlayer.getServer().getInfo().getName()
                                                                       : CloudProxy.getInstance().getProxyGroup().getName())).replace(
                        "%online_players%",
                        CloudAPI.getInstance().getOnlineCount() + NetworkUtils.EMPTY_STRING).replace("%max_players%",
                                                                                                     CloudProxy.getInstance()
                                                                                                               .getProxyGroup()
                                                                                                               .getProxyConfig()
                                                                                                               .getMaxPlayers() +
                                                                                                     NetworkUtils.EMPTY_STRING).replace(
                        "%group%",
                        (proxiedPlayer.getServer() != null &&
                         CloudProxy.getInstance().getCachedServers().containsKey(proxiedPlayer.getServer().getInfo().getName())
                         ? CloudProxy.getInstance().getCachedServers().get(proxiedPlayer.getServer().getInfo().getName()).getServiceId()
                                     .getGroup()
                         : "Hub")).replace("%proxy_group%", CloudProxy.getInstance().getProxyGroup().getName()))),
                                               new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getFooter().replace(
                                                   "%proxy%",
                                                   CloudAPI.getInstance().getServerId()).replace("%server%",
                                                                                                 (proxiedPlayer.getServer() != null
                                                                                                  ? proxiedPlayer.getServer().getInfo()
                                                                                                                 .getName()
                                                                                                  : CloudProxy.getInstance().getProxyGroup()
                                                                                                              .getName())).replace(
                                                   "%online_players%",
                                                   CloudAPI.getInstance().getOnlineCount() + NetworkUtils.EMPTY_STRING).replace(
                                                   "%max_players%",
                                                   CloudProxy.getInstance().getProxyGroup().getProxyConfig().getMaxPlayers() +
                                                   NetworkUtils.EMPTY_STRING).replace("%group%",
                                                                                      (proxiedPlayer.getServer() != null &&
                                                                                       CloudProxy.getInstance().getCachedServers()
                                                                                                 .containsKey(proxiedPlayer.getServer()
                                                                                                                           .getInfo()
                                                                                                                           .getName())
                                                                                       ? CloudProxy.getInstance().getCachedServers().get(
                                                                                          proxiedPlayer.getServer().getInfo().getName())
                                                                                                   .getServiceId().getGroup()
                                                                                       : "Hub")).replace("%proxy_group%",
                                                                                                         CloudProxy.getInstance()
                                                                                                                   .getProxyGroup()
                                                                                                                   .getName()))));
                }
            }

        }

        @Override
        public void onCustomChannelMessageReceive(final String channel, final String message, final Document document) {
            if (handle(channel, message, document)) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedCustomChannelMessageReceiveEvent(channel, message, document));
        }

        @Override
        public void onCustomSubChannelMessageReceive(final String channel, final String message, final Document document) {
            if (handle(channel, message, document)) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedSubChannelMessageEvent(channel, message, document));
        }

        @Override
        public void onPlayerLoginNetwork(final CloudPlayer cloudPlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLoginEvent(cloudPlayer));
        }

        @Override
        public void onPlayerDisconnectNetwork(final CloudPlayer cloudPlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLogoutEvent(cloudPlayer));
            cloudPlayers.remove(cloudPlayer.getUniqueId());
        }

        @Override
        public void onPlayerDisconnectNetwork(final UUID uniqueId) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLogoutUniqueEvent(uniqueId));
            cloudPlayers.remove(uniqueId);
        }

        @Override
        public void onPlayerUpdate(final CloudPlayer cloudPlayer) {
            if (cloudPlayers.containsKey(cloudPlayer.getUniqueId())) {
                cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerUpdateEvent(cloudPlayer));
        }

        @Override
        public void onOfflinePlayerUpdate(final OfflinePlayer offlinePlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedOfflinePlayerUpdateEvent(offlinePlayer));
        }

        @Override
        public void onUpdateOnlineCount(final int onlineCount) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedOnlineCountUpdateEvent(onlineCount));
        }

        private boolean handle(final String channel, final String message, final Document document) {

            if (channel.equalsIgnoreCase("cloudnet_internal")) {

                if (message == null) {
                    return false;
                }

                if (message.equalsIgnoreCase("sendMessage")) {
                    final UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(new TextComponent(TextComponent.fromLegacyText(document.getString("message"))));
                        }
                    }
                    return true;
                }


                if (message.equalsIgnoreCase("sendMessage_basecomponent")) {
                    final UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(document.getObject("baseComponent", BaseComponent.class));
                        }
                    }
                }

                if (message.equalsIgnoreCase("kickPlayer")) {
                    final UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.disconnect(document.getString("reason"));
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendActionbar")) {
                    final UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR,
                                                      TextComponent.fromLegacyText(document.getString("message")));
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendTitle")) {
                    if (!document.contains("stay") || !document.contains("fadeIn") || !document.contains("fadeOut") || !document.contains(
                        "uniqueId")) {
                        return true;
                    }

                    final UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (proxiedPlayer != null) {
                        final Title title = ProxyServer.getInstance().createTitle();

                        if (document.contains("title")) {
                            title.title(TextComponent.fromLegacyText(document.getString("title")));
                        }

                        if (document.contains("subTitle")) {
                            title.subTitle(TextComponent.fromLegacyText(document.getString("subTitle")));
                        }

                        title.fadeIn(document.getInt("fadeIn")).fadeOut(document.getInt("fadeOut")).stay(document.getInt("stay"));

                        proxiedPlayer.sendTitle(title);
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendPlayer")) {
                    final net.md_5.bungee.api.config.ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(document
                                                                                                                         .getString("server"));
                    if (serverInfo != null) {
                        final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(document.getObject("uniqueId", UUID.class));
                        if (proxiedPlayer != null) {
                            proxiedPlayer.connect(serverInfo);
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("player_server_switch")) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerServerSwitchEvent(document.getObject("player",
                                                                                                                                 CloudPlayer.TYPE),
                                                                                                              document
                                                                                                                  .getString("server")));

                    return true;
                }


                return true;
            } else {
                return false;
            }
        }

    }
}
