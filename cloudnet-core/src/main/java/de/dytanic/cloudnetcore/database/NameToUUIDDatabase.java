/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.database.DatabaseImpl;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class NameToUUIDDatabase extends DatabaseUsable {

    public NameToUUIDDatabase(final Database database) {
        super(database);
    }

    public void append(final MultiValue<String, UUID> values) {
        database.insert(new DatabaseDocument(values.getFirst().toLowerCase()).append("uniqueId", values.getSecond()));

        database.insert(new DatabaseDocument(values.getSecond().toString()).append("name", values.getFirst()));
    }

    public void replace(final MultiValue<UUID, String> replacer) {
        final Document document = database.getDocument(replacer.getFirst().toString());
        document.append("name", replacer.getSecond());
        database.insert(document);
    }

    public UUID get(final String name) {
        if (name == null) {
            return null;
        }

        if (getDatabaseImplementation().containsDoc(name.toLowerCase())) {
            final Document document = database.getDocument(name.toLowerCase());
            if (!document.contains("uniqueId")) {
                database.delete(name.toLowerCase());
                return null;
            }
            return document.getObject("uniqueId", new TypeToken<UUID>() {}.getType());
        }
        return null;
    }

    public DatabaseImpl getDatabaseImplementation() {
        return ((DatabaseImpl) database);
    }

    public String get(final UUID uniqueId) {
        if (uniqueId == null) {
            return null;
        }

        if (getDatabaseImplementation().containsDoc(uniqueId.toString())) {
            final Document document = database.getDocument(uniqueId.toString());
            if (!document.contains("name")) {
                database.delete(uniqueId.toString());
                return null;
            }
            return document.getString("name");
        }
        return null;
    }

    public void handleUpdate(final UpdateConfigurationDatabase updateConfigurationDatabase) {
        final String updateKey = "updated_database_from_2_1_Pv29";
        if (!updateConfigurationDatabase.get().contains(updateKey)) {
            final Collection<Document> documents = new LinkedList<>(database.loadDocuments().getDocs());

            documents.forEach(document -> {
                final String name = document.getString(Database.UNIQUE_NAME_KEY);
                if (name != null && name.length() < 32) {
                    database.delete(name);
                    database.insert(document.append(Database.UNIQUE_NAME_KEY, name.toLowerCase()));
                }
            });

            updateConfigurationDatabase.set(updateConfigurationDatabase.get().append(updateKey, true));
            ((DatabaseImpl) database).save();
            ((DatabaseImpl) database).clear();
        }
    }
}
