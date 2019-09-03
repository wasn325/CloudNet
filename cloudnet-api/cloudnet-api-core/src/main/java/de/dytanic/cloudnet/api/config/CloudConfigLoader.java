/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.nio.file.Path;

public class CloudConfigLoader {

    private final Path pathConnectionJson;

    private final Path pathConfigJson;

    private final ConfigTypeLoader type;

    public CloudConfigLoader(final Path pathConnectionJson, final Path pathConfigJson, final ConfigTypeLoader type) {
        this.pathConnectionJson = pathConnectionJson;
        this.pathConfigJson = pathConfigJson;
        this.type = type;
    }

    public ConfigTypeLoader getType() {
        return type;
    }

    public Path getPathConfigJson() {
        return pathConfigJson;
    }

    public Path getPathConnectionJson() {
        return pathConnectionJson;
    }

    public Document loadConfig() {
        return Document.loadDocument(pathConfigJson);
    }

    public ConnectableAddress loadConnnection() {
        return Document.loadDocument(pathConnectionJson).getObject("connection", new TypeToken<ConnectableAddress>() {}.getType());
    }

}
