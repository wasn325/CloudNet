/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnetcore.network.wrapper.WrapperSession;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperSessionDatabase extends DatabaseUsable {

    public WrapperSessionDatabase(final Database database) {
        super(database);
    }

    public void addSession(final WrapperSession session) {
        final DatabaseDocument databaseDocument = new DatabaseDocument(session.getUniqueId().toString());
        databaseDocument.append("session", session);
        databaseDocument.insert(database);
    }
}
