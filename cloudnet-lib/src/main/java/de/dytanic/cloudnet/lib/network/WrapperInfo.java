package de.dytanic.cloudnet.lib.network;

/**
 * Created by Tareko on 29.06.2017.
 */
public class WrapperInfo {

    private final String serverId;
    private final String hostName;
    private final String version;
    private final boolean ready;
    private final int availableProcessors;
    private final int startPort;
    private final int process_queue_size;
    private final int memory;

    public WrapperInfo(final String serverId,
                       final String hostName,
                       final String version,
                       final boolean ready,
                       final int availableProcessors,
                       final int startPort,
                       final int process_queue_size,
                       final int memory) {
        this.serverId = serverId;
        this.hostName = hostName;
        this.version = version;
        this.ready = ready;
        this.availableProcessors = availableProcessors;
        this.startPort = startPort;
        this.process_queue_size = process_queue_size;
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "WrapperInfo{" + "serverId='" + serverId + '\'' + ", hostName='" + hostName + '\'' + ", version='" + version + '\'' +
               ", ready=" + ready + ", availableProcessors=" + availableProcessors + ", startPort=" + startPort + ", process_queue_size=" +
               process_queue_size + ", memory=" + memory + '}';
    }

    public String getServerId() {
        return serverId;
    }

    public int getStartPort() {
        return startPort;
    }

    public int getProcess_queue_size() {
        return process_queue_size;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public int getMemory() {
        return memory;
    }

    public String getHostName() {
        return hostName;
    }

    public String getVersion() {
        return version;
    }
}
