/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import de.dytanic.cloudnet.lib.map.WrappedMap;

/**
 * Data class that holds information for a dynamic path like in Spring
 */
public class PathProvider {

    /**
     * The path where a request has been sent to.
     */
    private final String path;

    /**
     * The parameters of a request to {@code path}.
     */
    private final WrappedMap pathParameters;

    public PathProvider(final String path, final WrappedMap pathParameters) {
        this.path = path;
        this.pathParameters = pathParameters;
    }

    public String getPath() {
        return path;
    }

    public WrappedMap getPathParameters() {
        return pathParameters;
    }
}
