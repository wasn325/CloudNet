/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.wrapper.local;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnet.setup.spigot.SetupSpigotVersion;
import de.dytanic.cloudnet.web.client.WebClient;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import joptsimple.OptionSet;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Tareko on 01.10.2017.
 */
public class LocalCloudWrapper implements Runnabled<OptionSet> {

    private static final String WRAPPER_URL = "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-wrapper/target/CloudNet-Wrapper.jar";
    private final StringBuffer stringBuffer = new StringBuffer();
    private final byte[] buffer = new byte[1024];
    private Process process;
    private Thread consoleThread;
    private boolean shutdown;
    private boolean enabled;
    private boolean showConsoleOutput = !Boolean.getBoolean("cloudnet.localwrapper.disableConsole");
    private LocalWrapperConfig config;

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isShowConsoleOutput() {
        return showConsoleOutput;
    }

    public void setShowConsoleOutput(final boolean showConsoleOutput) {
        this.showConsoleOutput = showConsoleOutput;
    }

    public Wrapper getWrapper() {
        final String wrapperId = this.loadWrapperConfiguration().getString("general.wrapperId");
        return CloudNet.getInstance().getWrappers().get(wrapperId);
    }

    public Configuration loadWrapperConfiguration() {
        if (this.config == null || this.config.isOutdated()) {
            try (final InputStream inputStream = Files.newInputStream(Paths.get("wrapper/config.yml"))) {
                this.config = new LocalWrapperConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStream));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return this.config != null ? this.config.getConfiguration() : null;
    }

    public void installUpdate(final WebClient webClient) {
        final Path path = Paths.get("wrapper/CloudNet-Wrapper.jar");
        if (Files.exists(path)) {
            webClient.updateLocalCloudWrapper(path);
        }
    }

    @Override
    public void run(final OptionSet obj) {
        if (obj.has("installWrapper")) {
            try {
                if (!Files.exists(Paths.get("wrapper"))) {
                    Files.createDirectories(Paths.get("wrapper"));
                }

                this.setupWrapperJar();
                this.setupConfig();
                this.setupWrapperKey();
                this.setupSpigot(obj);

            } catch (final IOException e) {
                e.printStackTrace();
            }

            this.startup();
            this.enabled = true;
        }
    }

    // -------------------- SETUP --------------------

    private void setupWrapperJar() {
        final Path path = Paths.get("wrapper/CloudNet-Wrapper.jar");
        if (!Files.exists(path)) {
            try {
                System.out.println("Downloading wrapper...");
                final URLConnection urlConnection = new URL(WRAPPER_URL).openConnection();
                urlConnection.setRequestProperty("User-Agent",
                                                 "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                urlConnection.connect();
                Files.copy(urlConnection.getInputStream(), path);
                System.out.println("Download completed!");
            } catch (final Exception ex) {
                System.err.println("Error on setting up wrapper: " + ex.getMessage());
                return;
            }
        }
    }

    private void setupConfig() {
        final Path path = Paths.get("wrapper/config.yml");
        if (!Files.exists(path)) {
            final ConnectableAddress address = CloudNet.getInstance().getConfig().getAddresses().iterator().next();
            User user = CloudNet.getInstance().getUser("Wrapper-1");
            if (user == null) {
                final String password = NetworkUtils.randomString(32);
                System.out.println("PASSWORD FOR USER \"Wrapper-1\": " + password);
                user = new BasicUser("Wrapper-1", password, Collections.singletonList("*"));
                CloudNet.getInstance().getUsers().add(user);
                CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            }

            final User finalUser = user;
            final WrapperMeta wrapperMeta = CloudNet.getInstance().getConfig().getWrappers().stream().filter(meta -> meta.getId()
                                                                                                                         .equals("Wrapper-1"))
                                                    .findFirst().orElseGet(() -> {
                    final WrapperMeta newMeta = new WrapperMeta("Wrapper-1", address.getHostName(), finalUser.getName());
                    CloudNet.getInstance().getConfig().createWrapper(newMeta);
                    return newMeta;
                });

            final long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            final Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", address.getHostName());
            configuration.set("connection.cloudnet-port", address.getPort());
            configuration.set("connection.cloudnet-web", CloudNet.getInstance().getConfig().getWebServerConfig().getPort());
            configuration.set("general.wrapperId", wrapperMeta.getId());
            configuration.set("general.internalIp", wrapperMeta.getHostName());
            configuration.set("general.proxy-config-host", wrapperMeta.getHostName());
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.viaversion", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.devservicePath", new File("wrapper/Development").getAbsolutePath());
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewCloudServer", 100D);
            configuration.set("general.percentOfCPUForANewProxy", 100D);

            try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path),
                                                                                      StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupWrapperKey() {
        try {
            Files.copy(Paths.get("WRAPPER_KEY.cnd"), Paths.get("wrapper/WRAPPER_KEY.cnd"), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSpigot(final OptionSet obj) {
        final Path path = Paths.get("wrapper/local/spigot.jar");
        if (!obj.has("disallow_bukkit_download") && !Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (final IOException e) {
                e.printStackTrace();
            }
            final SetupSpigotVersion setup = new SetupSpigotVersion();
            setup.setTarget(path);
            setup.accept(CloudNet.getLogger().getReader());
        }
    }

    // -------------------- SETUP --------------------

    // -------------------- PROCESS --------------------

    private void startup() {
        System.out.println("Starting local wrapper...");
        try {
            this.startProcess();
            this.initConsoleThread();

            System.out.println("Successfully started the local wrapper!");
        } catch (final IOException e) {
            System.err.println("Failed to start the local wrapper!");
            e.printStackTrace();
        }
    }

    private void startProcess() throws IOException {
        System.out.println("Starting wrapper process...");
        this.process = new ProcessBuilder("java",
                                          "-Xmx256M",
                                          "-Djline.terminal=jline.UnsupportedTerminal",
                                          "-Dcloudnet.logging.prompt.disabled=true",
                                          "-jar",
                                          "CloudNet-Wrapper.jar").directory(new File("wrapper")).start();
        System.out.println("Successfully started the wrapper process!");
    }

    private void initConsoleThread() {
        this.consoleThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (this.process.isAlive()) {
                    this.readStream(this.process.getInputStream(), s -> {
                        if (this.showConsoleOutput) {
                            System.out.println("LocalWrapper | " + s);
                        }
                    });
                    this.readStream(this.process.getErrorStream(), s -> {
                        if (this.showConsoleOutput) {
                            System.err.println("LocalWrapper | " + s);
                        }
                    });
                } else if (!shutdown) {
                    try {
                        this.startProcess();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    this.enabled = false;
                    Thread.currentThread().interrupt();
                }
            }
        }, "LocalWrapper-Console");
        this.consoleThread.start();
    }

    private void readStream(final InputStream inputStream, final Consumer<String> consumer) {
        try {
            int len;
            while (inputStream.available() > 0 && (len = inputStream.read(this.buffer)) != -1) {
                this.stringBuffer.append(new String(this.buffer, 0, len, StandardCharsets.UTF_8));
            }

            final String stringText = this.stringBuffer.toString();
            if (!stringText.contains("\n") && !stringText.contains("\r")) {
                return;
            }

            for (final String input : stringText.split("\r")) {
                for (final String text : input.split("\n")) {
                    if (!text.trim().isEmpty()) {
                        consumer.accept(text);
                    }
                }
            }

            this.stringBuffer.setLength(0);

        } catch (final Exception ignored) {
            this.stringBuffer.setLength(0);
        }
    }

    public void close() throws IOException {
        this.enabled = false;
        if (this.process != null && this.process.isAlive()) {
            this.shutdown = true;
            this.stop();
        }
    }

    private void stop() throws IOException {
        System.out.println("Stopping the local wrapper...");
        this.executeCommand("stop");
        try {
            if (!this.process.waitFor(30, TimeUnit.SECONDS)) {
                this.process.destroy();
            }
            System.out.println("Successfully stopped the local wrapper!");
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void executeCommand(final String command) throws IOException {
        try (final OutputStream output = this.process.getOutputStream()) {
            output.write((command + '\n').getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }

    public void restart() throws IOException {
        this.stop();
    }

    // -------------------- PROCESS --------------------

}
