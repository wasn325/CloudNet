/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;

/**
 * Created by Tareko on 18.08.2017.
 */
public class CloudPriorityGroupStartupHandler implements ICloudHandler {

    @Override
    public void onHandle(final CloudNet cloudNet) {
        for (final ServerGroup group : CloudNet.getInstance().getServerGroups().values()) {
            final double onlineCount = CloudNet.getInstance().getOnlineCount(group.getName());
            if (group.getPriorityService().getGroup().getOnlineServers() == 0 ||
                group.getPriorityService().getGlobal().getOnlineCount() == 0 || group.getGroupMode() == ServerGroupMode.STATIC ||
                group.isMaintenance()) {
                continue;
            }

            final double priority = (group.getPriorityService().getGroup().getOnlineServers() /
                                     ((double) group.getPriorityService().getGroup().getOnlineCount())) *
                                    (onlineCount == 0 ? 1.0D : (onlineCount));
            final Collection<String> servers = CloudNet.getInstance().getServersAndWaitings(group.getName());

            if (servers.isEmpty() && servers.size() < (priority <= 1 ? 1 : priority)) {
                CloudNet.getInstance().startGameServer(group);
                continue;
            }

            if (servers.size() < (priority <= 1 ? 1 : priority)) {
                CloudNet.getInstance().startGameServer(group, new Document(), true);
            }
        }
    }

    @Override
    public int getTicks() {
        return 50;
    }
}
