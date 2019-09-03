/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import java.util.Collection;
import java.util.List;

public class ProxyConfig {

    private boolean enabled;

    private boolean maintenance;

    private List<Motd> motdsLayouts;

    private Motd maintenanceMotdLayout;

    private String maintenaceProtocol;

    private int maxPlayers;

    private boolean fastConnect;

    private Boolean customPayloadFixer;

    private AutoSlot autoSlot;

    private TabList tabList;

    private String[] playerInfo;

    private Collection<String> whitelist;

    private DynamicFallback dynamicFallback;

    public ProxyConfig(final boolean enabled,
                       final boolean maintenance,
                       final List<Motd> motdsLayouts,
                       final Motd maintenanceMotdLayout,
                       final String maintenaceProtocol,
                       final int maxPlayers,
                       final boolean fastConnect,
                       final Boolean customPayloadFixer,
                       final AutoSlot autoSlot,
                       final TabList tabList,
                       final String[] playerInfo,
                       final Collection<String> whitelist,
                       final DynamicFallback dynamicFallback) {
        this.enabled = enabled;
        this.maintenance = maintenance;
        this.motdsLayouts = motdsLayouts;
        this.maintenanceMotdLayout = maintenanceMotdLayout;
        this.maintenaceProtocol = maintenaceProtocol;
        this.maxPlayers = maxPlayers;
        this.fastConnect = fastConnect;
        this.customPayloadFixer = customPayloadFixer;
        this.autoSlot = autoSlot;
        this.tabList = tabList;
        this.playerInfo = playerInfo;
        this.whitelist = whitelist;
        this.dynamicFallback = dynamicFallback;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(final int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public AutoSlot getAutoSlot() {
        return autoSlot;
    }

    public void setAutoSlot(final AutoSlot autoSlot) {
        this.autoSlot = autoSlot;
    }

    public Boolean getCustomPayloadFixer() {
        return customPayloadFixer;
    }

    public void setCustomPayloadFixer(final Boolean customPayloadFixer) {
        this.customPayloadFixer = customPayloadFixer;
    }

    public Collection<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(final Collection<String> whitelist) {
        this.whitelist = whitelist;
    }

    public DynamicFallback getDynamicFallback() {
        return dynamicFallback;
    }

    public void setDynamicFallback(final DynamicFallback dynamicFallback) {
        this.dynamicFallback = dynamicFallback;
    }

    public List<Motd> getMotdsLayouts() {
        return motdsLayouts;
    }

    public void setMotdsLayouts(final List<Motd> motdsLayouts) {
        this.motdsLayouts = motdsLayouts;
    }

    public Motd getMaintenanceMotdLayout() {
        return maintenanceMotdLayout;
    }

    public void setMaintenanceMotdLayout(final Motd maintenanceMotdLayout) {
        this.maintenanceMotdLayout = maintenanceMotdLayout;
    }

    public String getMaintenaceProtocol() {
        return maintenaceProtocol;
    }

    public void setMaintenaceProtocol(final String maintenaceProtocol) {
        this.maintenaceProtocol = maintenaceProtocol;
    }

    public String[] getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(final String[] playerInfo) {
        this.playerInfo = playerInfo;
    }

    public TabList getTabList() {
        return tabList;
    }

    public void setTabList(final TabList tabList) {
        this.tabList = tabList;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(final boolean maintenance) {
        this.maintenance = maintenance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFastConnect() {
        return fastConnect;
    }

    public void setFastConnect(final boolean fastConnect) {
        this.fastConnect = fastConnect;
    }
}
