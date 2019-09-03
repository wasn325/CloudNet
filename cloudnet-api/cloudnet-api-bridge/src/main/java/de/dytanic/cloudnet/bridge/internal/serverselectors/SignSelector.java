/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.sign.*;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.*;


/**
 * Created by Tareko on 21.08.2017.
 */
public final class SignSelector implements Listener {

    private static SignSelector instance;
    private Map<UUID, Sign> signs;
    private volatile SignLayoutConfig signLayoutConfig;
    private Thread worker;
    private Map<String, ServerInfo> servers = NetworkUtils.newConcurrentHashMap();

    public SignSelector(final Map<UUID, Sign> signs, final SignLayoutConfig signLayoutConfig) {
        instance = this;
        this.signs = signs;
        this.signLayoutConfig = signLayoutConfig;

        Bukkit.getPluginManager().registerEvents(this, CloudServer.getInstance().getPlugin());
    }

    public static SignSelector getInstance() {
        return instance;
    }

    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    public void setServers(final Map<String, ServerInfo> servers) {
        this.servers = servers;
    }

    public Thread getWorker() {
        return worker;
    }

    public void setWorker(final Thread worker) {
        this.worker = worker;
    }

    public Map<UUID, Sign> getSigns() {
        return signs;
    }

    public void setSigns(final Map<UUID, Sign> signs) {
        this.signs = signs;
    }

    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    public void setSignLayoutConfig(final SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    @Deprecated
    public void start() {
        CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(new NetworkHandlerAdapterImpl());
        worker = new ThreadImpl();
        worker.setDaemon(true);
        worker.start();

        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                NetworkUtils.addAll(servers,
                                    MapWrapper
                                        .collectionCatcherHashMap(CloudAPI.getInstance().getServers(), new Catcher<String, ServerInfo>() {
                                            @Override
                                            public String doCatch(final ServerInfo key) {
                                                return key.getServiceId().getServerId();
                                            }
                                        }));
            }
        });
    }

    @EventHandler
    public void handleInteract(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null &&
            e.getClickedBlock().getState() instanceof org.bukkit.block.Sign) {
            if (containsPosition(e.getClickedBlock().getLocation())) {
                final Sign sign = getSignByPosition(e.getClickedBlock().getLocation());
                if (sign.getServerInfo() != null) {
                    final String s = sign.getServerInfo().getServiceId().getServerId();
                    final ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF("Connect");
                    output.writeUTF(s);
                    e.getPlayer().sendPluginMessage(CloudServer.getInstance().getPlugin(), "BungeeCord", output.toByteArray());
                }
            }
        }
    }

    public boolean containsPosition(final Location location) {
        final Position position = toPosition(location);
        for (final Sign sign : signs.values()) {
            if (sign.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    public Sign getSignByPosition(final Location location) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(final Sign value) {
                return value.getPosition().equals(toPosition(location));
            }
        });
    }

    public Position toPosition(final Location location) {
        return new Position(CloudAPI.getInstance().getGroup(),
                            location.getWorld().getName(),
                            location.getX(),
                            location.getY(),
                            location.getZ());
    }

    public boolean containsPosition(final Position position) {
        for (final Sign sign : signs.values()) {
            if (sign.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    private Sign findFreeSign(final String group) {
        return CollectionWrapper.filter(this.signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(final Sign value) {
                return value.getTargetGroup().equals(group) && value.getServerInfo() == null;
            }
        });
    }

    public Collection<String> freeServers(final String group) {
        final List<String> servers = new ArrayList<>();

        for (final ServerInfo serverInfo : getServers(group)) {
            servers.add(serverInfo.getServiceId().getServerId());
        }

        for (final Sign sign : signs.values()) {
            if (sign.getServerInfo() != null) {
                servers.remove(sign.getServerInfo().getServiceId().getServerId());
            }
        }

        final List<String> x = new ArrayList<>();

        ServerInfo serverInfo;
        for (short i = 0; i < servers.size(); i++) {
            serverInfo = this.servers.get(servers.get(i));
            if (serverInfo != null) {
                if (!serverInfo.isOnline() || serverInfo.getServerState() != ServerState.LOBBY ||
                    serverInfo.getServerConfig().isHideServer() || serverInfo.getMotd().contains("INGAME") || serverInfo.getMotd().contains(
                    "RUNNING")) {
                    x.add(serverInfo.getServiceId().getServerId());
                }
            } else {
                x.add(servers.get(i));
            }
        }

        for (final String b : x) {
            servers.remove(b);
        }

        Collections.sort(servers);
        return servers;
    }

    private Collection<ServerInfo> getServers(final String group) {
        return CollectionWrapper.filterMany(servers.values(), new Acceptable<ServerInfo>() {
            @Override
            public boolean isAccepted(final ServerInfo value) {
                return value.getServiceId().getGroup().equals(group);
            }
        });
    }

    public Sign filter(final ServerInfo serverInfo) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(final Sign value) {
                return value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(serverInfo.getServiceId()
                                                                                                                            .getServerId());
            }
        });
    }

    public void sendUpdateSynchronized(final Location location, final String[] layout) {
        final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
        sign.setLine(0, layout[0]);
        sign.setLine(1, layout[1]);
        sign.setLine(2, layout[2]);
        sign.setLine(3, layout[3]);
        sign.update();
    }

    public Sign getSign(final ServerInfo serverInfo) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(final Sign value) {
                return value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(serverInfo.getServiceId()
                                                                                                                            .getServerId());
            }
        });
    }

    public boolean containsGroup(final String group) {
        for (final SignGroupLayouts signLayouts : signLayoutConfig.getGroupLayouts()) {
            if (signLayouts.getName().equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    public void handleUpdate(final Sign sign, final ServerInfo serverInfo) {
        if (!exists(sign)) {
            return;
        }
        final Location location = toLocation(sign.getPosition());
        final SignLayout searchLayer = getSearchingLayout(((ThreadImpl) worker).animationTick);
        if (isMaintenance(sign.getTargetGroup())) {
            final SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
            final String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
            sign.setServerInfo(serverInfo);
            updateArray(layout, serverInfo);
            for (final Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
            changeBlock(location, _signLayout.getBlockName(), _signLayout.getBlockId(), _signLayout.getSubId());
            return;
        }

        if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
            if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                serverInfo.getServerConfig().isHideServer()) {
                sign.setServerInfo(null);
                String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                layout = updateOfflineAndMaintenance(layout, sign);
                for (final Player all : Bukkit.getOnlinePlayers()) {
                    sendUpdate(all, location, layout);
                }
                sendUpdateSynchronizedTask(location, layout);
                return;
            }
            final String[] layout;
            final SignLayout signLayout;
            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                signLayout = getLayout(sign.getTargetGroup(), "full");
                layout = signLayout.getSignLayout().clone();
            } else if (serverInfo.getOnlineCount() == 0) {
                signLayout = getLayout(sign.getTargetGroup(), "empty");
                layout = signLayout.getSignLayout().clone();
            } else {
                signLayout = getLayout(sign.getTargetGroup(), "online");
                layout = signLayout.getSignLayout().clone();
            }

            sign.setServerInfo(serverInfo);
            updateArray(layout, serverInfo);
            for (final Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(location, layout);
            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
        } else {
            sign.setServerInfo(null);
            final String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
            for (final Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(location, layout);
        }
    }

    public boolean exists(final Sign sign) {
        try {
            if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
                final Location location = toLocation(sign.getPosition());
                return location.getBlock().getState() instanceof org.bukkit.block.Sign;
            } else {
                return false;
            }
        } catch (final Throwable ex) {
            return false;
        }
    }

    public Location toLocation(final Position position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }

    public SignLayout getSearchingLayout(final int id) {
        for (final SignLayout signLayout : signLayoutConfig.getSearchingAnimation().getSearchingLayouts()) {
            if (signLayout.getName().equals("loading" + id)) {
                return signLayout;
            }
        }
        return null;
    }

    public boolean isMaintenance(final String group) {
        if (CloudAPI.getInstance().getServerGroupMap().containsKey(group)) {
            return CloudAPI.getInstance().getServerGroupMap().get(group).isMaintenance();
        } else {
            return true;
        }
    }

    public SignLayout getLayout(final String group, final String name) {
        SignGroupLayouts signGroupLayouts = getGroupLayout(group);
        if (signGroupLayouts == null) {
            signGroupLayouts = getGroupLayout("default");
        }
        return CollectionWrapper.filter(signGroupLayouts.getLayouts(), new Acceptable<SignLayout>() {
            @Override
            public boolean isAccepted(final SignLayout value) {
                return value.getName().equals(name);
            }
        });
    }

    public String[] updateOfflineAndMaintenance(final String[] value, final Sign sign) {
        for (short i = 0; i < value.length; i++) {
            value[i] = ChatColor.translateAlternateColorCodes('&',
                                                              value[i].replace("%group%", sign.getTargetGroup())
                                                                      .replace("%from%", sign.getPosition().getGroup()));
        }
        return value;
    }

    public void updateArray(final String[] value, final ServerInfo serverInfo) {
        short i = 0;
        for (final String x : value) {
            value[i] = ChatColor.translateAlternateColorCodes('&', x.replace("%server%",
                                                                             serverInfo.getServiceId().getServerId() +
                                                                             NetworkUtils.EMPTY_STRING).replace("%id%",
                                                                                                                serverInfo.getServiceId()
                                                                                                                          .getId() +
                                                                                                                NetworkUtils.EMPTY_STRING)
                                                                    .replace("%host%", serverInfo.getHost()).replace("%port%",
                                                                                                                     serverInfo.getPort() +
                                                                                                                     NetworkUtils.EMPTY_STRING)
                                                                    .replace("%memory%", serverInfo.getMemory() + "MB").replace(
                    "%online_players%",
                    serverInfo.getOnlineCount() + NetworkUtils.EMPTY_STRING).replace("%max_players%",
                                                                                     serverInfo.getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%motd%",
                                                                             ChatColor
                                                                                 .translateAlternateColorCodes('&', serverInfo.getMotd()))
                                                                    .replace("%state%",
                                                                             serverInfo.getServerState().name() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%wrapper%",
                                                                             serverInfo.getServiceId().getWrapperId() +
                                                                             NetworkUtils.EMPTY_STRING).replace("%extra%",
                                                                                                                serverInfo.getServerConfig()
                                                                                                                          .getExtra())
                                                                    .replace("%template%", serverInfo.getTemplate().getName())
                                                                    .replace("%group%", serverInfo.getServiceId().getGroup()));
            i++;
        }
    }

    public void sendUpdate(final Player player, final Location location, final String[] layout) {
        if (player.getLocation().distance(location) < 32) {
            player.sendSignChange(location, layout);
        }
    }

    public void sendUpdateSynchronizedTask(final Location location, final String[] layout) {
        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
                sign.setLine(0, layout[0]);
                sign.setLine(1, layout[1]);
                sign.setLine(2, layout[2]);
                sign.setLine(3, layout[3]);
                sign.update();
            }
        });
    }

    public void changeBlock(final Location location, final String blockName, final int blockId, final int subId) {
        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
            final Material material = ItemStackBuilder.getMaterialIgnoreVersion(blockName, blockId);
            final BlockState signBlockState = location.getBlock().getState();

            if (material != null && subId != -1 && signBlockState instanceof org.bukkit.block.Sign) {
                final MaterialData materialData = signBlockState.getData();

                if (materialData instanceof org.bukkit.material.Sign) { // this will return false in newer 1.14 spigot versions, even if it's a sign
                    final org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) materialData;
                    if (materialSign.isWallSign()) {
                        final Block backBlock = location.getBlock().getRelative(materialSign.getAttachedFace());
                        final BlockState blockState = backBlock.getState();
                        blockState.setType(material);
                        blockState.setData(new MaterialData(material, (byte) subId));
                        blockState.update(true);
                    }
                }
            }
        });
    }

    public SignGroupLayouts getGroupLayout(final String group) {
        return CollectionWrapper.filter(signLayoutConfig.getGroupLayouts(), new Acceptable<SignGroupLayouts>() {
            @Override
            public boolean isAccepted(final SignGroupLayouts value) {
                return value.getName().equals(group);
            }
        });
    }

    private class ThreadImpl extends Thread {

        int animationTick = 1;
        private boolean valueTick;

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (signLayoutConfig != null && signLayoutConfig.isKnockbackOnSmallDistance()) {
                    try {
                        for (final Sign sign : signs.values()) {
                            if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
                                final Location location = SignSelector.this.toLocation(sign.getPosition());
                                for (final Entity entity : location.getWorld().getNearbyEntities(location,
                                                                                                 signLayoutConfig.getDistance(),
                                                                                                 signLayoutConfig.getDistance(),
                                                                                                 signLayoutConfig.getDistance())) {
                                    if (entity instanceof Player && !entity.hasPermission("cloudnet.signs.knockback.bypass")) {
                                        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                                            @Override
                                            public void run() {
                                                if (location.getBlock().getState() instanceof org.bukkit.block.Sign) {
                                                    try {
                                                        final Location entityLocation = entity.getLocation();
                                                        entity.setVelocity(new Vector(entityLocation.getX() - location.getX(),
                                                                                      entityLocation.getY() - location.getY(),
                                                                                      entityLocation.getZ() - location.getZ()).normalize()
                                                                                                                              .multiply(
                                                                                                                                  signLayoutConfig
                                                                                                                                      .getStrength())
                                                                                                                              .setY(0.2D));
                                                    } catch (final Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }

                final SearchingAnimation searchingAnimation = signLayoutConfig.getSearchingAnimation();

                final SignLayout searchLayer = getSearchingLayout(animationTick);
                Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        for (final Sign sign : signs.values()) {
                            final boolean exists = exists(sign);

                            if (!exists) {
                                sign.setServerInfo(null);
                                continue;
                            }

                            if (isMaintenance(sign.getTargetGroup())) {
                                final SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
                                final String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
                                sign.setServerInfo(null);
                                sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                changeBlock(toLocation(sign.getPosition()),
                                            _signLayout.getBlockName(),
                                            _signLayout.getBlockId(),
                                            _signLayout.getSubId());
                                continue;
                            }

                            final Location location = toLocation(sign.getPosition());
                            if (sign.getServerInfo() == null) {
                                final List<String> servers = new ArrayList<>(freeServers(sign.getTargetGroup()));
                                if (!servers.isEmpty()) {
                                    final String server = servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                                    final ServerInfo serverInfo = SignSelector.this.getServers().get(server);
                                    if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
                                        if (signLayoutConfig.isFullServerHide() &&
                                            serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                            layout = updateOfflineAndMaintenance(layout, sign);
                                            sendUpdateSynchronized(location, layout);
                                            changeBlock(location,
                                                        searchLayer.getBlockName(),
                                                        searchLayer.getBlockId(),
                                                        searchLayer.getSubId());
                                            continue;
                                        }

                                        sign.setServerInfo(serverInfo);
                                        final String[] layout;
                                        final SignLayout signLayout;
                                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            signLayout = getLayout(sign.getTargetGroup(), "full");
                                            layout = signLayout.getSignLayout().clone();
                                        } else if (serverInfo.getOnlineCount() == 0) {
                                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                                            layout = signLayout.getSignLayout().clone();
                                        } else {
                                            signLayout = getLayout(sign.getTargetGroup(), "online");
                                            layout = signLayout.getSignLayout().clone();
                                        }
                                        updateArray(layout, serverInfo);
                                        sendUpdateSynchronized(location, layout);
                                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                    } else {
                                        sign.setServerInfo(null);
                                        final String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                    }
                                } else {
                                    sign.setServerInfo(null);
                                    final String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, searchLayer.getBlockName(), searchLayer.getBlockId(), searchLayer.getSubId());
                                }

                                continue;
                            }

                            if (valueTick) {
                                if (sign.getServerInfo() != null) {
                                    final ServerInfo serverInfo = sign.getServerInfo();
                                    if (!isMaintenance(sign.getTargetGroup())) {
                                        if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
                                            if ((signLayoutConfig.isFullServerHide() &&
                                                 serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                                                serverInfo.getServerConfig().isHideServer()) {
                                                sign.setServerInfo(null);
                                                String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                  .getSignLayout().clone(), sign);
                                                layout = updateOfflineAndMaintenance(layout, sign);
                                                sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                                return;
                                            }
                                            final String[] layout;
                                            final SignLayout signLayout;
                                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                                signLayout = getLayout(sign.getTargetGroup(), "full");
                                                layout = signLayout.getSignLayout().clone();
                                            } else if (serverInfo.getOnlineCount() == 0) {
                                                signLayout = getLayout(sign.getTargetGroup(), "empty");
                                                layout = signLayout.getSignLayout().clone();
                                            } else {
                                                signLayout = getLayout(sign.getTargetGroup(), "online");
                                                layout = signLayout.getSignLayout().clone();
                                            }
                                            sign.setServerInfo(serverInfo);
                                            updateArray(layout, serverInfo);
                                            sendUpdateSynchronized(location, layout);
                                            changeBlock(location,
                                                        signLayout.getBlockName(),
                                                        signLayout.getBlockId(),
                                                        signLayout.getSubId());
                                        } else {
                                            sign.setServerInfo(null);
                                            final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                    .getSignLayout().clone(), sign);
                                            sendUpdateSynchronized(location, layout);
                                        }
                                    } else {
                                        sign.setServerInfo(null);
                                        final SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
                                        final String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                        changeBlock(location, _signLayout.getBlockName(), _signLayout.getBlockId(), _signLayout.getSubId());
                                    }
                                }
                            }
                        }
                    }
                });

                if (searchingAnimation.getAnimations() <= animationTick) {
                    animationTick = 1;
                }

                animationTick++;
                valueTick = !valueTick;

                try {
                    Thread.sleep(1000 / searchingAnimation.getAnimationsPerSecond());
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class NetworkHandlerAdapterImpl extends NetworkHandlerAdapter {

        @Override
        public void onServerAdd(final ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            final Sign sign = filter(serverInfo);

            if (sign != null) {
                if (exists(sign)) {
                    sign.setServerInfo(serverInfo);
                    final Location location = toLocation(sign.getPosition());
                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                        if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                            serverInfo.getServerConfig().isHideServer()) {
                            sign.setServerInfo(null);
                            final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            layout = updateOfflineAndMaintenance(layout, sign);
                            for (final Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                        final String[] layout;
                        final SignLayout signLayout;
                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                            signLayout = getLayout(sign.getTargetGroup(), "full");
                            layout = signLayout.getSignLayout().clone();
                        } else if (serverInfo.getOnlineCount() == 0) {
                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                            layout = signLayout.getSignLayout().clone();
                        } else {
                            signLayout = getLayout(sign.getTargetGroup(), "online");
                            layout = signLayout.getSignLayout().clone();
                        }
                        sign.setServerInfo(serverInfo);
                        updateArray(layout, serverInfo);
                        for (final Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    } else {
                        sign.setServerInfo(null);
                        final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                .getSignLayout().clone(), sign);
                        for (final Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                    }

                } else {
                    sign.setServerInfo(null);

                    final Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                    final Location location = toLocation(next.getPosition());
                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                        if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                            serverInfo.getServerConfig().isHideServer()) {
                            sign.setServerInfo(null);
                            final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            for (final Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(next.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                        final String[] layout;
                        final SignLayout signLayout;
                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                            signLayout = getLayout(sign.getTargetGroup(), "full");
                            layout = signLayout.getSignLayout().clone();
                        } else if (serverInfo.getOnlineCount() == 0) {
                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                            layout = signLayout.getSignLayout().clone();
                        } else {
                            signLayout = getLayout(sign.getTargetGroup(), "online");
                            layout = signLayout.getSignLayout().clone();
                        }
                        sign.setServerInfo(serverInfo);
                        updateArray(layout, serverInfo);
                        for (final Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    } else {
                        sign.setServerInfo(null);
                        final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                        final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                        for (final Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    }
                }
            } else {
                final Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
                if (newSign != null) {
                    if (exists(newSign)) {
                        final Location location = toLocation(newSign.getPosition());
                        if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                            if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                                serverInfo.getServerConfig().isHideServer()) {
                                sign.setServerInfo(null);
                                final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                for (final Player all : Bukkit.getOnlinePlayers()) {
                                    sendUpdate(all, location, layout);
                                }
                                sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
                                changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                return;
                            }

                            final SignLayout signLayout;
                            final String[] layout;
                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                signLayout = getLayout(sign.getTargetGroup(), "full");
                                layout = signLayout.getSignLayout().clone();
                            } else if (serverInfo.getOnlineCount() == 0) {
                                signLayout = getLayout(sign.getTargetGroup(), "empty");
                                layout = signLayout.getSignLayout().clone();
                            } else {
                                signLayout = getLayout(sign.getTargetGroup(), "online");
                                layout = signLayout.getSignLayout().clone();
                            }
                            sign.setServerInfo(serverInfo);
                            updateArray(layout, serverInfo);
                            for (final Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        } else {
                            newSign.setServerInfo(null);
                            final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            for (final Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        }
                    }
                }
            }
        }

        @Override
        public void onServerInfoUpdate(final ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            final Sign sign = filter(serverInfo);

            if (sign != null) {
                if (CloudServer.getInstance().getPlugin() != null && CloudServer.getInstance().getPlugin().isEnabled()) {
                    Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {

                        @Override
                        public void run() {
                            if (exists(sign)) {
                                sign.setServerInfo(serverInfo);
                                final Location location = toLocation(sign.getPosition());
                                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                    if ((signLayoutConfig.isFullServerHide() &&
                                         serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                                        serverInfo.getServerConfig().isHideServer()) {
                                        sign.setServerInfo(null);
                                        final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                        final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                        return;
                                    }
                                    final SignLayout signLayout;
                                    final String[] layout;
                                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                        signLayout = getLayout(sign.getTargetGroup(), "full");
                                        layout = signLayout.getSignLayout().clone();
                                    } else if (serverInfo.getOnlineCount() == 0) {
                                        signLayout = getLayout(sign.getTargetGroup(), "empty");
                                        layout = signLayout.getSignLayout().clone();
                                    } else {
                                        signLayout = getLayout(sign.getTargetGroup(), "online");
                                        layout = signLayout.getSignLayout().clone();
                                    }
                                    sign.setServerInfo(serverInfo);
                                    updateArray(layout, serverInfo);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                } else {
                                    sign.setServerInfo(null);
                                    final SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                    final String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                }

                            } else {
                                sign.setServerInfo(null);

                                final Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                                final Location location = toLocation(next.getPosition());
                                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                    if ((signLayoutConfig.isFullServerHide() &&
                                         serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                                        serverInfo.getServerConfig().isHideServer()) {
                                        sign.setServerInfo(null);
                                        final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                .getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(toLocation(next.getPosition()), layout);
                                        return;
                                    }
                                    final String[] layout;
                                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                        layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                                    } else if (serverInfo.getOnlineCount() == 0) {
                                        layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                                    } else {
                                        layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                                    }
                                    sign.setServerInfo(serverInfo);
                                    updateArray(layout, serverInfo);
                                    sendUpdateSynchronized(location, layout);
                                } else {
                                    sign.setServerInfo(null);
                                    final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                            .getSignLayout().clone(), sign);
                                    sendUpdateSynchronized(location, layout);
                                }
                            }
                        }
                    });
                } else {
                    Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            final Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
                            if (newSign != null) {
                                if (exists(newSign)) {
                                    final Location location = toLocation(newSign.getPosition());
                                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                        if ((signLayoutConfig.isFullServerHide() &&
                                             serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) ||
                                            serverInfo.getServerConfig().isHideServer()) {
                                            sign.setServerInfo(null);
                                            final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                    .getSignLayout().clone(), sign);
                                            sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                            return;
                                        }
                                        final String[] layout;
                                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                                        } else if (serverInfo.getOnlineCount() == 0) {
                                            layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                                        } else {
                                            layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                                        }
                                        sign.setServerInfo(serverInfo);
                                        updateArray(layout, serverInfo);
                                        sendUpdateSynchronized(location, layout);
                                    } else {
                                        sign.setServerInfo(null);
                                        final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                .getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onServerRemove(final ServerInfo serverInfo) {
            servers.remove(serverInfo.getServiceId().getServerId(), serverInfo);

            final Sign sign = filter(serverInfo);
            if (sign != null) {
                sign.setServerInfo(null);
                if (!exists(sign)) {
                    return;
                }
                final String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick).getSignLayout()
                                                                                                                           .clone(), sign);
                sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
            }
        }
    }
}
