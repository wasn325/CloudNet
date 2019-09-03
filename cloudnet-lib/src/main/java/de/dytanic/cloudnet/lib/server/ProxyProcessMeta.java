package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ProxyProcessMeta {

    private final ServiceId serviceId;

    private final int memory;

    private final int port;

    private final String[] processParameters;

    private final String url;

    private final Collection<ServerInstallablePlugin> downloadablePlugins;

    private final Document properties;

    public ProxyProcessMeta(final ServiceId serviceId,
                            final int memory,
                            final int port,
                            final String[] processParameters,
                            final String url,
                            final Collection<ServerInstallablePlugin> downloadablePlugins,
                            final Document properties) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.port = port;
        this.processParameters = processParameters;
        this.url = url;
        this.downloadablePlugins = downloadablePlugins;
        this.properties = properties;
    }

    public int getMemory() {
        return memory;
    }

    public Document getProperties() {
        return properties;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

    public Collection<ServerInstallablePlugin> getDownloadablePlugins() {
        return downloadablePlugins;
    }

    public String getUrl() {
        return url;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }
}
