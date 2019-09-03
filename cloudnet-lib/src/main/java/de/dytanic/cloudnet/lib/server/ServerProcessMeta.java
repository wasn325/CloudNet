package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ServerProcessMeta {

    private final ServiceId serviceId;

    private final int memory;

    private final boolean priorityStop;

    private final String url;

    private final String[] processParameters;

    private final boolean onlineMode;

    private final Collection<ServerInstallablePlugin> downloadablePlugins;

    private final ServerConfig serverConfig;

    private final String customServerDownload;

    private final int port;

    private final Properties serverProperties;

    private final Template template;

    public ServerProcessMeta(final ServiceId serviceId,
                             final int memory,
                             final boolean priorityStop,
                             final String url,
                             final String[] processParameters,
                             final boolean onlineMode,
                             final Collection<ServerInstallablePlugin> downloadablePlugins,
                             final ServerConfig serverConfig,
                             final String customServerDownload,
                             final int port,
                             final Properties serverProperties,
                             final Template template) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.priorityStop = priorityStop;
        this.url = url;
        this.processParameters = processParameters;
        this.onlineMode = onlineMode;
        this.downloadablePlugins = downloadablePlugins;
        this.serverConfig = serverConfig;
        this.customServerDownload = customServerDownload;
        this.port = port;
        this.serverProperties = serverProperties;
        this.template = template;
    }

    public String getUrl() {
        return url;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }

    public Collection<ServerInstallablePlugin> getDownloadablePlugins() {
        return downloadablePlugins;
    }

    public int getPort() {
        return port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public Properties getServerProperties() {
        return serverProperties;
    }

    public String getCustomServerDownload() {
        return customServerDownload;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public boolean isPriorityStop() {
        return priorityStop;
    }
}
