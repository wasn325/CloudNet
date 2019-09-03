/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import java.util.Collection;

/**
 * Container for the CloudFlare configuration.
 */
public class CloudFlareConfig {

    /**
     * Whether or not the module is enabled
     */
    private final boolean enabled;

    /**
     * The E-Mail address of the account to use.
     */
    private final String email;

    /**
     * The token to authenticate at the CloudFlare API with.
     */
    private final String token;

    /**
     * The domain to create records for.
     */
    private final String domainName;

    /**
     * The internal zone ID at CloudFlare
     */
    private final String zoneId;

    /**
     * All configured BungeeCord groups and their sub-domains
     */
    private final Collection<CloudFlareProxyGroup> groups;

    public CloudFlareConfig(final boolean enabled,
                            final String email,
                            final String token,
                            final String domainName,
                            final String zoneId,
                            final Collection<CloudFlareProxyGroup> groups) {
        this.enabled = enabled;
        this.email = email;
        this.token = token;
        this.domainName = domainName;
        this.zoneId = zoneId;
        this.groups = groups;
    }

    public Collection<CloudFlareProxyGroup> getGroups() {
        return groups;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getZoneId() {
        return zoneId;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
