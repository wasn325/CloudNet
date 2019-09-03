/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.modules;

import de.dytanic.cloudnetcore.CloudNet;

import java.io.InputStream;

/**
 * Created by Tareko on 22.10.2017.
 */
public final class DefaultModule {

    private final String moduleName;

    private final String moduleVersion;

    public DefaultModule(final String moduleName, final String moduleVersion) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    protected InputStream stream() {
        return CloudNet.class.getClassLoader().getResourceAsStream("modules/" + moduleName + ".jar");
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }
}
