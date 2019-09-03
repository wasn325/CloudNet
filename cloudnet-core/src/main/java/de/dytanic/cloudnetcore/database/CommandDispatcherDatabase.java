/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class CommandDispatcherDatabase extends DatabaseUsable {

    public CommandDispatcherDatabase(final Database database) {
        super(database);
        if (!database.containsDoc("dispatch_commands")) {
            database.insert(new DatabaseDocument("dispatch_commands"));
        }
    }

    public void appendCommand(final String dispatcher, final String commandLine) {
        final Document document = database.getDocument("dispatch_commands");
        document.append(dispatcher, commandLine);
        database.insert(document);
    }

    public void deleteDispatch(final String dispatch) {
        final Document document = database.getDocument("dispatch_commands");
        document.remove(dispatch);
        database.insert(document);
    }

    public String findDispatcher(final String dispatch) {
        final Document document = database.getDocument("dispatch_commands");
        return document.getString(dispatch);
    }

}
