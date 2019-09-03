/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server.process;

import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnetwrapper.server.ServerStage;

public class ServerProcess {

    private final ServerProcessMeta meta;

    private ServerStage serverStage;

    public ServerProcess(final ServerProcessMeta meta, final ServerStage serverStage) {
        this.meta = meta;
        this.serverStage = serverStage;
    }

    public ServerProcessMeta getMeta() {
        return meta;
    }

    public ServerStage getServerStage() {
        return serverStage;
    }

    public void setServerStage(final ServerStage serverStage) {
        this.serverStage = serverStage;
    }
}
