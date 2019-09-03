/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.zip.ZipConverter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tareko on 24.09.2017.
 */
public class MasterTemplateLoader {

    private final String url;

    private final String dest;

    private final SimpledUser simpledUser;

    private final Template template;

    private final String group;

    private final String customName;

    public MasterTemplateLoader(final String url,
                                final String dest,
                                final SimpledUser simpledUser,
                                final Template template,
                                final String group,
                                final String customName) {
        this.url = url;
        this.dest = dest;
        this.simpledUser = simpledUser;
        this.template = template;
        this.group = group;
        this.customName = customName;
    }

    public Template getTemplate() {
        return template;
    }

    public String getUrl() {
        return url;
    }

    public String getGroup() {
        return group;
    }

    public SimpledUser getSimpledUser() {
        return simpledUser;
    }

    public String getCustomName() {
        return customName;
    }

    public String getDest() {
        return dest;
    }

    public MasterTemplateLoader load() {
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
            urlConnection.setRequestProperty("-Xmessage", customName != null ? "custom" : "template");
            urlConnection.setRequestProperty("-Xvalue", customName != null ? customName : new Document("template",
                                                                                                       template.getName()).append("group",
                                                                                                                                  group)
                                                                                                                          .convertToJsonString());
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            if (urlConnection.getHeaderField("-Xresponse") == null) {
                Files.copy(urlConnection.getInputStream(), Paths.get(dest));
            }

            urlConnection.disconnect();

        } catch (final IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MasterTemplateLoader unZip(final String dest) {
        try {
            ZipConverter.extract(Paths.get(this.dest), Paths.get(dest));
            new File(this.dest).delete();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
