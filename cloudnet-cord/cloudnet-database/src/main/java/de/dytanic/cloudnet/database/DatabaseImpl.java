/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.database;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Implementation of {@link Database}.
 */
public class DatabaseImpl implements Database {

    private final String name;
    private final java.util.Map<String, Document> documents;
    private final File backendDir;

    public DatabaseImpl(final String name, final Map<String, Document> documents, final File backendDir) {
        this.name = name;
        this.documents = documents;
        this.backendDir = backendDir;
    }

    public String getName() {
        return name;
    }

    public File getBackendDir() {
        return backendDir;
    }

    public Map<String, Document> getDocuments() {
        return documents;
    }

    @Override
    public Database loadDocuments() {
        final File[] files = backendDir.listFiles();
        if (files == null) {
            return this;
        }
        for (final File file : files) {
            if (!this.documents.containsKey(file.getName())) {
                final Document document = Document.loadDocument(file);
                if (document.contains(UNIQUE_NAME_KEY)) {
                    this.documents.put(file.getName(), document);
                }
            }
        }
        return this;
    }

    @Override
    public Collection<Document> getDocs() {
        return documents.values();
    }

    @Override
    public Document getDocument(final String name) {
        if (name == null) {
            return null;
        }

        Document document = documents.get(name);

        if (document == null) {
            final File doc = new File("database/" + this.name + NetworkUtils.SLASH_STRING + name);
            if (doc.exists()) {
                document = Document.loadDocument(doc);
                this.documents.put(doc.getName(), document);
                return document;
            }
        }
        return document;
    }

    @Override
    public Database insert(final Document... documents) {
        for (final Document document : documents) {
            if (document.contains(UNIQUE_NAME_KEY)) {
                this.documents.put(document.getString(UNIQUE_NAME_KEY), document);
                final Path path = Paths.get("database/" + this.name + '/' + document.getString(UNIQUE_NAME_KEY));
                if (!Files.exists(path)) {
                    try {
                        Files.createFile(path);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                document.saveAsConfig(path);
            }
        }
        return this;
    }

    @Override
    public Database delete(final String name) {
        if (name == null) {
            return this;
        }

        final Document document = getDocument(name);
        if (document != null) {
            documents.remove(name);
        }
        try {
            Files.delete(Paths.get("database", this.name, name));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Database delete(final Document document) {
        if (document.contains(UNIQUE_NAME_KEY)) {
            delete(document.getString(UNIQUE_NAME_KEY));
        }
        return this;
    }

    @Override
    public Document load(final String name) {
        return Document.loadDocument(new File("database/" + this.name + NetworkUtils.SLASH_STRING + name));
    }

    @Override
    public boolean contains(final Document document) {
        return contains(document.getString(UNIQUE_NAME_KEY));
    }

    @Override
    public boolean contains(final String name) {
        return getDocument(name) != null;
    }

    @Override
    public int size() {
        final String[] files = backendDir.list();
        return files == null ? 0 : files.length;
    }

    @Override
    public boolean containsDoc(final String name) {
        if (name == null) {
            return false;
        }
        return new File("database/" + this.name + NetworkUtils.SLASH_STRING + name).exists();
    }

    @Override
    public Database insertAsync(final Document... documents) {
        TaskScheduler.runtimeScheduler().schedule(() -> {
            insert(documents);
        });
        return this;
    }

    @Override
    public Database deleteAsync(final String name) {
        TaskScheduler.runtimeScheduler().schedule(() -> {
            delete(name);
        });
        return this;
    }

    @Override
    public FutureTask<Document> getDocumentAsync(final String name) {
        return new FutureTask<>(() -> getDocument(name));
    }

    /**
     * Saves the currently loaded documents to their files.
     */
    public void save() {
        for (final Document document : documents.values()) {
            if (document.contains(UNIQUE_NAME_KEY)) {
                document.saveAsConfig(Paths.get("database", this.name, document.getString(UNIQUE_NAME_KEY)));
            }
        }
    }

    /**
     * Clears the currently loaded documents.
     */
    public void clear() {
        this.documents.clear();
    }
}
