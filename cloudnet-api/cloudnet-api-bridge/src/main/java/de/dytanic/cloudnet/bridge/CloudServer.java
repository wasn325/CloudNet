/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.out.PacketOutUpdateServerInfo;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.bridge.event.bukkit.*;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cloud-Server represents
 */
public class CloudServer implements ICloudService {

    private static CloudServer instance;

    private final BukkitBootstrap bukkitBootstrap;

    private final Map<UUID, CloudPlayer> cloudPlayers = NetworkUtils.newConcurrentHashMap();
    private final String hostAdress;
    private final int port;
    private final Template template;
    private final int memory;
    /*=================================================*/
    private int maxPlayers;
    private String motd;
    private ServerState serverState;
    private ServerConfig serverConfig;
    private boolean allowAutoStart = true;
    /*=================================================*/

    public CloudServer(final BukkitBootstrap bukkitBootstrap, final CloudAPI cloudAPI) {
        instance = this;
        cloudAPI.setCloudService(this);

        this.bukkitBootstrap = bukkitBootstrap;
        final ServerInfo serverInfo = cloudAPI.getConfig().getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());

        cloudAPI.getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl());
        this.allowAutoStart = !cloudAPI.getConfig().contains("cloudProcess");
        this.maxPlayers = serverInfo.getMaxPlayers();
        this.motd = serverInfo.getMotd();
        this.hostAdress = serverInfo.getHost();
        this.port = serverInfo.getPort();
        this.serverConfig = serverInfo.getServerConfig();
        this.memory = serverInfo.getMemory();
        this.template = serverInfo.getTemplate();
        this.serverState = ServerState.LOBBY;
    }

    /**
     * Returns the instance from the CloudServer
     *
     * @return
     */
    public static CloudServer getInstance() {
        return instance;
    }

    public void updateDisable() {
        final List<String> list = new CopyOnWriteArrayList<>();

        for (final Player all : Bukkit.getOnlinePlayers()) {
            list.add(all.getName());
        }

        final ServerInfo serverInfo = new ServerInfo(CloudAPI.getInstance().getServiceId(),
            hostAdress,
            port,
            false,
            list,
            memory,
            motd,
            Bukkit.getOnlinePlayers().size(),
            maxPlayers,
            serverState,
            serverConfig,
            template);
        CloudAPI.getInstance().getNetworkConnection().sendPacketSynchronized(new PacketOutUpdateServerInfo(serverInfo));
    }

    /**
     * Updates the ServerInfo on a asynchronized BukkitScheduler Task
     */
    public void updateAsync() {
        bukkitBootstrap.getServer().getScheduler().runTaskAsynchronously(bukkitBootstrap, this::update);
    }

    /**
     * Updates the ServerInfo
     */
    public void update() {
        final List<String> list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        final ServerInfo serverInfo = new ServerInfo(CloudAPI.getInstance().getServiceId(),
            hostAdress,
            port,
            true,
            list,
            memory,
            motd,
            Bukkit.getOnlinePlayers().size(),
            maxPlayers,
            serverState,
            serverConfig,
            template);
        CloudAPI.getInstance().update(serverInfo);
    }

    /**
     * Changed the State to INGAME and Start a gameserver
     */
    public void changeToIngame() {
        serverState = ServerState.INGAME;

        if (allowAutoStart) {
            final SimpleServerGroup simpleServerGroup = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup());
            CloudAPI.getInstance().startGameServer(simpleServerGroup, template);
            allowAutoStart = false;

            Bukkit.getScheduler().runTaskLater(bukkitBootstrap, () -> allowAutoStart = true, 6000);
        }

        update();
    }

    /**
     * Checks if this instance can starting game servers auto
     *
     * @return
     */
    public boolean isAllowAutoStart() {
        return allowAutoStart;
    }

    /**
     * You can disable the Autostart funtction from this server
     *
     * @param allowAutoStart
     */
    public void setAllowAutoStart(final boolean allowAutoStart) {
        this.allowAutoStart = allowAutoStart;
    }

    @Deprecated
    public void getPlayerAndCache(final UUID uniqueId) {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer != null) {
            cloudPlayer.setPlayerExecutor(new PlayerExecutorBridge());
            this.cloudPlayers.put(uniqueId, cloudPlayer);
        }
    }

    public int getPort() {
        return port;
    }

    public String getHostAdress() {
        return hostAdress;
    }

    /**
     * Returns the serverConfig from this instance
     *
     * @return
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    /**
     * Sets the serverConfig in a new default style
     *
     * @param serverConfig
     */
    public void setServerConfig(final ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void setServerStateAndUpdate(final ServerState serverStateAndUpdate) {
        this.serverState = serverStateAndUpdate;
        update();
    }

    /**
     * Returns the ServerState from this instance
     *
     * @return
     */
    public ServerState getServerState() {
        return serverState;
    }

    /**
     * Set the serverState INGAME, LOBBY, OFFLINE for switching Signs or your API thinks
     *
     * @param serverState
     */
    public void setServerState(final ServerState serverState) {
        this.serverState = serverState;
    }

    /**
     * Returns the max players from the acceptings
     *
     * @return
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the maxPlayers from this instance
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMaxPlayersAndUpdate(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
        update();
    }

    public void setMotdAndUpdate(final String motd) {
        this.motd = motd;
        update();
    }

    /**
     * Returns the motd from the server marks for the cloud
     *
     * @return
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Sets the Motd for the ServerInfo
     *
     * @param motd
     */
    public void setMotd(final String motd) {
        this.motd = motd;
    }

    /**
     * Returns the Template of the ServerInfo
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Registerd one command
     *
     * @param command
     */
    public void registerCommand(final Command command) {
        try {
            final Class<?> clazz = ReflectionUtil.reflectCraftClazz(".CraftServer");
            final CommandMap commandMap;
            if (clazz != null) {
                commandMap = (CommandMap) clazz.getMethod("getCommandMap").invoke(Bukkit.getServer());
            } else {
                commandMap = (CommandMap) Class.forName("net.glowstone.GlowServer").getMethod("getCommandMap").invoke(Bukkit.getServer());
            }
            commandMap.register("cloudnet", command);
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the SimpleServerGroup of the instance
     *
     * @return
     */
    public SimpleServerGroup getGroupData() {
        return CloudAPI.getInstance().getCloudNetwork().getServerGroups().get(CloudAPI.getInstance().getGroup());
    }

    public double getPercentOfPlayerNowOnline() {
        return (((double) Bukkit.getOnlinePlayers().size()) / (double) maxPlayers) * 100;
    }

    /**
     * Returns the Plugin instance from this CLoud-System
     *
     * @return
     */
    public JavaPlugin getPlugin() {
        return bukkitBootstrap;
    }

    /**
     * Returns the ServerProcessMeta for the bootstrap of the software
     *
     * @return
     */
    public ServerProcessMeta getServerProcessMeta() {
        return CloudAPI.getInstance().getConfig().getObject("serverProcess", new TypeToken<ServerProcessMeta>() {}.getType());
    }

    /**
     * @param player
     */
    public void updateNameTags(final Player player) {
        this.updateNameTags(player, null);
    }

    public void updateNameTags(final Player player, final Function<Player, PermissionGroup> playerPermissionGroupFunction) {
        this.updateNameTags(player, playerPermissionGroupFunction, null);
    }

    public void updateNameTags(final Player player,
                               final Function<Player, PermissionGroup> playerPermissionGroupFunction,
                               final Function<Player, PermissionGroup> allOtherPlayerPermissionGroupFunction) {
        if (CloudAPI.getInstance().getPermissionPool() == null || !CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            return;
        }

        final PermissionGroup playerPermissionGroup =
            playerPermissionGroupFunction != null ? playerPermissionGroupFunction.apply(player) : cloudPlayers.get(player.getUniqueId())
                .getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());

        initScoreboard(player);

        for (final Player all : player.getServer().getOnlinePlayers()) {
            initScoreboard(all);

            if (playerPermissionGroup != null) {
                addTeamEntry(player, all, playerPermissionGroup);
            }

            PermissionGroup targetPermissionGroup =
                allOtherPlayerPermissionGroupFunction != null ? allOtherPlayerPermissionGroupFunction.apply(all) : null;

            if (targetPermissionGroup == null) {
                targetPermissionGroup = getCachedPlayer(all.getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI
                    .getInstance().getPermissionPool());
            }

            if (targetPermissionGroup != null) {
                addTeamEntry(all, player, targetPermissionGroup);
            }

        }
    }

    private void initScoreboard(final Player all) {
        if (all.getScoreboard() == null) {
            all.setScoreboard(all.getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    private void addTeamEntry(final Player target, final Player all, final PermissionGroup permissionGroup) {
        String teamName = permissionGroup.getTagId() + permissionGroup.getName();
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
            CloudAPI.getInstance().dispatchConsoleMessage(
                "In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() +
                " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the name of the group in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() +
                             " was temporarily shortened to 16 characters!", "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the name of the group in your perms.yml", "cloudnet.notify");
        }
        Team team = all.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = all.getScoreboard().registerNewTeam(teamName);
        }

        if (permissionGroup.getPrefix().length() > 16) {
            permissionGroup.setPrefix(permissionGroup.getPrefix().substring(0, 16));
            CloudAPI.getInstance().dispatchConsoleMessage(
                "In order to prevent issues, the prefix of the group " + permissionGroup.getName() +
                " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the prefix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the prefix of the group " + permissionGroup.getName() +
                             " was temporarily shortened to 16 characters!", "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the prefix in your perms.yml", "cloudnet.notify");
        }
        if (permissionGroup.getSuffix().length() > 16) {
            permissionGroup.setSuffix(permissionGroup.getSuffix().substring(0, 16));
            CloudAPI.getInstance().dispatchConsoleMessage(
                "In order to prevent issues, the suffix of the group " + permissionGroup.getName() +
                " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the suffix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the suffix of the group " + permissionGroup.getName() +
                             " was temporarily shortened to 16 characters!", "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the suffix in your perms.yml", "cloudnet.notify");
        }

        try {
            final Method setColor = team.getClass().getDeclaredMethod("setColor", ChatColor.class);
            setColor.setAccessible(true);
            if (!permissionGroup.getColor().isEmpty()) {
                setColor.invoke(team, ChatColor.getByChar(permissionGroup.getColor().replaceAll("&", "").replaceAll("§", "")));
            } else {
                setColor.invoke(team, ChatColor.getByChar(ChatColor.getLastColors(permissionGroup.getPrefix().replace('&', '§'))
                    .replaceAll("&", "").replaceAll("§", "")));
            }
        } catch (final NoSuchMethodException ignored) {
        } catch (final IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix()));
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix()));

        team.addEntry(target.getName());

        target.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay() + target.getName()));
    }

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
        return false;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the cached CloudPlayer Objectives
     *
     * @return
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    public Map<UUID, CloudPlayer> getClonedCloudPlayers() {
        return new HashMap<>(this.cloudPlayers);
    }

    //API Handler
    /*================================================================================================================*/
    private class NetworkHandlerImpl implements NetworkHandler {

        @Override
        public void onServerAdd(final ServerInfo serverInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitServerAddEvent(serverInfo));
        }

        @Override
        public void onServerInfoUpdate(final ServerInfo serverInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitServerInfoUpdateEvent(serverInfo));
        }

        @Override
        public void onServerRemove(final ServerInfo serverInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitServerRemoveEvent(serverInfo));
        }

        @Override
        public void onProxyAdd(final ProxyInfo proxyInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitProxyAddEvent(proxyInfo));
        }

        @Override
        public void onProxyInfoUpdate(final ProxyInfo proxyInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitProxyInfoUpdateEvent(proxyInfo));
        }

        @Override
        public void onProxyRemove(final ProxyInfo proxyInfo) {
            Bukkit.getPluginManager().callEvent(new BukkitProxyRemoveEvent(proxyInfo));
        }

        @Override
        public void onCloudNetworkUpdate(final CloudNetwork cloudNetwork) {
            Bukkit.getPluginManager().callEvent(new BukkitCloudNetworkUpdateEvent(cloudNetwork));
        }

        @Override
        public void onCustomChannelMessageReceive(final String channel, final String message, final Document document) {
            Bukkit.getPluginManager().callEvent(new BukkitCustomChannelMessageReceiveEvent(channel, message, document));
        }

        @Override
        public void onCustomSubChannelMessageReceive(final String channel, final String message, final Document document) {
            Bukkit.getPluginManager().callEvent(new BukkitSubChannelMessageEvent(channel, message, document));

            if (channel.equalsIgnoreCase("cloudnet_internal")) {
                if (message.equalsIgnoreCase("install_plugin")) {
                    final String url = document.getString("url");
                    try {
                        final URLConnection urlConnection = new URL(url).openConnection();
                        urlConnection.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        urlConnection.connect();
                        Files.copy(urlConnection.getInputStream(), Paths.get("plugins/" + document.getString("name") + ".jar"));
                        final File file = new File("plugins/" + document.getString("name") + ".jar");

                        Bukkit.getScheduler().runTask(CloudServer.this.getPlugin(), () -> {
                            try {
                                final Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
                                Bukkit.getPluginManager().enablePlugin(plugin);
                            } catch (final InvalidPluginException | InvalidDescriptionException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        public void onPlayerLoginNetwork(final CloudPlayer cloudPlayer) {
            //cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
            Bukkit.getPluginManager().callEvent(new BukkitPlayerLoginNetworkEvent(cloudPlayer));
        }

        @Override
        public void onPlayerDisconnectNetwork(final CloudPlayer cloudPlayer) {
            Bukkit.getPluginManager().callEvent(new BukkitPlayerDisconnectEvent(cloudPlayer));
        }

        @Override
        public void onPlayerDisconnectNetwork(final UUID uniqueId) {

        }

        @Override
        public void onPlayerUpdate(final CloudPlayer cloudPlayer) {
            if (cloudPlayers.containsKey(cloudPlayer.getUniqueId())) {
                cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
            }
            Bukkit.getPluginManager().callEvent(new BukkitPlayerUpdateEvent(cloudPlayer));
        }

        @Override
        public void onOfflinePlayerUpdate(final OfflinePlayer offlinePlayer) {
            Bukkit.getPluginManager().callEvent(new BukkitOfflinePlayerUpdateEvent(offlinePlayer));
        }

        @Override
        public void onUpdateOnlineCount(final int onlineCount) {
            Bukkit.getPluginManager().callEvent(new BukkitOnlineCountUpdateEvent(onlineCount));
        }
    }
}
