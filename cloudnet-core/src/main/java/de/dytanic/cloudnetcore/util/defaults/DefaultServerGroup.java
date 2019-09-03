/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util.defaults;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class DefaultServerGroup extends ServerGroup {

    private static final String[] PROCESS_PRE_PARAMETERS = {};

    public DefaultServerGroup(final String name,
                              final Collection<String> wrapper,
                              final int memory,
                              final int startup,
                              final int percentForNewServerAutomatically,
                              final ServerGroupType serverType,
                              final ServerGroupMode groupMode,
                              final AdvancedServerConfig advancedServerConfig) {
        super(name,
              wrapper,
              false,
              memory,
              memory,
              0,
              true,
              startup,
              0,
              1,
              180,
              100,
              100,
              percentForNewServerAutomatically,
              serverType,
              groupMode,
              Collections.singletonList(new Template("default", TemplateResource.LOCAL, null, PROCESS_PRE_PARAMETERS, new ArrayList<>())),
              advancedServerConfig);
    }
}
