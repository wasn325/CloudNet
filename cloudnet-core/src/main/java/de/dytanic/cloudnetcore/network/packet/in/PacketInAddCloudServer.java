/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.ScheduledTask;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.priority.PriorityStopTask;

/**
 * Created by Tareko on 23.10.2017.
 */
public class PacketInAddCloudServer extends PacketInHandler {

    @Override
    public void handleInput(final Document data, final PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        final Wrapper cn = ((Wrapper) packetSender);
        final ServerInfo nullServerInfo = data.getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());
        final CloudServerMeta serverProcessMeta = data.getObject("cloudServerMeta", new TypeToken<CloudServerMeta>() {}.getType());
        final CloudServer minecraftServer = new CloudServer(cn, nullServerInfo, serverProcessMeta);
        cn.getCloudServers().put(nullServerInfo.getServiceId().getServerId(), minecraftServer);
        cn.getWaitingServices().remove(minecraftServer.getServerId());

        {
            if (serverProcessMeta.isPriorityStop()) {
                final ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(serverProcessMeta.getServiceId().getGroup());
                if (serverGroup != null) {
                    final PriorityStopTask priorityStopTask = new PriorityStopTask(cn,
                                                                                   minecraftServer,
                                                                                   serverGroup.getPriorityService().getStopTimeInSeconds());
                    final ScheduledTask scheduledTask = CloudNet.getInstance().getScheduler().runTaskRepeatSync(priorityStopTask, 0, 50);
                    priorityStopTask.setScheduledTask(scheduledTask);
                }
            }
        }

        CloudNet.getInstance().getNetworkManager().handleServerAdd(minecraftServer);
    }

}
