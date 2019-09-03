/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;

/**
 * Calls if a proxy server updates the proxyInfo
 */
public class ProxiedProxyInfoUpdateEvent extends ProxiedCloudEvent {

    private final ProxyInfo proxyInfo;

    public ProxiedProxyInfoUpdateEvent(final ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
