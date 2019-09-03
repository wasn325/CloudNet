
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileCopy {

    public static void copyFileToDirectory(final File file, final File to) throws IOException {
        if (!to.exists()) {
            to.mkdirs();
        }
        final File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
        Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFilesInDirectory(final File from, final File to) throws IOException {
        if (!to.exists()) {
            to.mkdirs();
        }
        for (final File file : from.listFiles()) {
            if (file.isDirectory()) {
                copyFilesInDirectory(file, new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName()));
            } else {
                final File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
                Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void insertData(final String paramString1, final String paramString2) {
        final InputStream localInputStream = FileCopy.class.getClassLoader().getResourceAsStream(paramString1);
        try {
            Files.copy(localInputStream, Paths.get(paramString2));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void rewriteFileUtils(final File file, final String host) throws Exception {
        file.setReadable(true);
        final FileInputStream in = new FileInputStream(file);
        final List<String> liste = new CopyOnWriteArrayList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String input;
        boolean value = false;
        while ((input = reader.readLine()) != null) {
            if (value) {
                liste.add("  host: " + host + '\n');
                value = false;
            } else {
                if (input.startsWith("  query_enabled")) {
                    liste.add(input + '\n');
                    value = true;
                } else {
                    liste.add(input + '\n');
                }
            }
        }
        file.delete();
        file.createNewFile();
        file.setReadable(true);
        final FileOutputStream out = new FileOutputStream(file);
        final PrintWriter w = new PrintWriter(out);
        for (final String wert : liste) {
            w.write(wert);
            w.flush();
        }
        reader.close();
        w.close();
    }

}
