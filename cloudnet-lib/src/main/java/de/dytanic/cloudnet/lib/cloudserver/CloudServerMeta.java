/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.cloudserver;

import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 17.10.2017.
 */
public class CloudServerMeta {

    private static final String[] PROCESS_PRE_PARAMETERS = {};
    private final ServiceId serviceId;

    private final int memory;

    private final boolean priorityStop;

    private final String[] processParameters;

    private final Collection<ServerInstallablePlugin> plugins;

    private final ServerConfig serverConfig;

    private final int port;

    private final String templateName;

    private final Properties serverProperties;

    private final ServerGroupType serverGroupType;

    private final Template template;

    public CloudServerMeta(final ServiceId serviceId,
                           final int memory,
                           final boolean priorityStop,
                           final String[] processParameters,
                           final Collection<ServerInstallablePlugin> plugins,
                           final ServerConfig serverConfig,
                           final int port,
                           final String templateName,
                           final Properties properties,
                           final ServerGroupType serverGroupType) {
        this.serviceId = serviceId;
        this.memory = memory;
        this.priorityStop = priorityStop;
        this.processParameters = processParameters;
        this.plugins = plugins;
        this.serverConfig = serverConfig;
        this.port = port;
        this.templateName = templateName;
        this.serverProperties = properties;
        this.serverGroupType = serverGroupType;
        this.template = new Template(templateName, TemplateResource.MASTER, null, PROCESS_PRE_PARAMETERS, new ArrayList<>());
    }

    public Template getTemplate() {
        return template;
    }

    public int getMemory() {
        return memory;
    }

    public Properties getServerProperties() {
        return serverProperties;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

    public String[] getProcessParameters() {
        return processParameters;
    }

    public ServerGroupType getServerGroupType() {
        return serverGroupType;
    }

    public Collection<ServerInstallablePlugin> getPlugins() {
        return plugins;
    }

    public String getTemplateName() {
        return templateName;
    }

    public boolean isPriorityStop() {
        return priorityStop;
    }
}
