/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 23.07.2017.
 */
public class ModuleManager {

    private final Collection<Module> modules = new ConcurrentLinkedQueue<>();
    private final ModuleDetector moduleDetector = new ModuleDetector();
    private final File directory = new File("modules");
    private Collection<String> disabledModuleList = new ArrayList<>();

    public ModuleManager() {
        directory.mkdir();
    }

    public Collection<Module> getModules() {
        return modules;
    }

    public Collection<String> getDisabledModuleList() {
        return disabledModuleList;
    }

    public void setDisabledModuleList(final Collection<String> disabledModuleList) {
        this.disabledModuleList = disabledModuleList;
    }

    public File getDirectory() {
        return directory;
    }

    public ModuleDetector getModuleDetector() {
        return moduleDetector;
    }

    public Collection<ModuleConfig> detect() throws Exception {
        return detect(directory);
    }

    public Collection<ModuleConfig> detect(final File directory) {
        return moduleDetector.detectAvailable(directory);
    }

    public ModuleManager loadModules() throws Exception {
        return loadModules(directory);
    }

    public ModuleManager loadModules(final File directory) throws Exception {
        final Collection<ModuleConfig> configs = detect(directory);

        for (final ModuleConfig config : configs) {
            if (!disabledModuleList.contains(config.getName())) {
                System.out.println("Loading module \"" + config.getName() + "\" version: " + config.getVersion() + "...");

                final ModuleLoader moduleLoader = new ModuleClassLoader(config);
                final Module module = moduleLoader.loadModule();
                module.setModuleLoader(moduleLoader);
                module.setDataFolder(directory);
                this.modules.add(module);
            }
        }
        return this;
    }

    public ModuleManager loadInternalModules(final Set<ModuleConfig> modules) throws Exception {
        return loadInternalModules(modules, this.directory);
    }

    public ModuleManager loadInternalModules(final Set<ModuleConfig> modules, final File dataFolder) throws Exception {
        for (final ModuleConfig moduleConfig : modules) {
            final ModuleLoader moduleLoader = new ModuleInternalLoader(moduleConfig);
            final Module module = moduleLoader.loadModule();
            module.setDataFolder(dataFolder);
            module.setModuleLoader(moduleLoader);
            this.modules.add(module);
        }
        return this;
    }

    public ModuleManager enableModules() {
        for (final Module module : modules) {
            System.out.println(
                "Enabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig().getVersion() + "...");
            module.onBootstrap();
        }
        return this;
    }

    public ModuleManager disableModule(final Module module) {
        System.out.println(
            "Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig().getVersion() + "...");
        module.onShutdown();
        modules.remove(module);
        return this;
    }

    public ModuleManager disableModules() {
        while (!modules.isEmpty()) {
            final Module module = (Module) ((Queue) modules).poll();
            System.out.println(
                "Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig().getVersion() +
                "...");
            module.onShutdown();
        }
        return this;
    }

}
