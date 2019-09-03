
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileUtility {

    private FileUtility() {
    }

    public static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        final byte[] buffer = new byte[8192];
        copy(inputStream, outputStream, buffer);

        try {

            final Method method = byte[].class.getMethod("finalize");
            method.setAccessible(true);
            method.invoke(buffer);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void copy(final InputStream inputStream, final OutputStream outputStream, final byte[] buffer) throws IOException {
        int len;

        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
            outputStream.flush();
        }
    }

    public static void copyFileToDirectory(final File from, final File to) throws IOException {
        copy(from.toPath(), new File(to.getPath(), from.getName()).toPath());
    }

    public static void copy(final Path from, final Path to) throws IOException {
        copy(from, to, new byte[16384]);
    }

    public static void copy(final Path from, final Path to, final byte[] buffer) throws IOException {
        if (from == null || to == null || !Files.exists(from)) {
            return;
        }

        if (!Files.exists(to)) {
            to.toFile().getParentFile().mkdirs();
            to.toFile().delete();

            Files.createFile(to);
        }

        try (final InputStream inputStream = Files.newInputStream(from); final OutputStream outputStream = Files.newOutputStream(to)) {
            copy(inputStream, outputStream, buffer);
        }
    }

    public static void copyFilesInDirectory(final File from, final File to) throws IOException {
        if (to == null || from == null || !from.exists()) {
            return;
        }

        if (!to.exists()) {
            to.mkdirs();
        }

        if (!from.isDirectory()) {
            return;
        }

        final File[] list = from.listFiles();
        final byte[] buffer = new byte[16384];
        if (list != null) {
            for (final File file : list) {
                if (file == null) {
                    continue;
                }

                if (file.isDirectory()) {
                    copyFilesInDirectory(file, new File(to.getAbsolutePath() + '/' + file.getName()));
                } else {
                    final File n = new File(to.getAbsolutePath() + '/' + file.getName());
                    copy(file.toPath(), n.toPath(), buffer);
                }
            }
        }
    }

    public static void insertData(final String paramString1, final String paramString2) {
        final File file = new File(paramString2);
        file.delete();

        try (final InputStream localInputStream = FileUtility.class.getClassLoader().getResourceAsStream(paramString1)) {
            Files.copy(localInputStream, Paths.get(paramString2), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(final File file) {
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            final File[] files = file.listFiles();

            if (files != null) {
                for (final File entry : files) {
                    if (entry.isDirectory()) {
                        deleteDirectory(entry);
                    } else {
                        entry.delete();
                    }
                }
            }
        }

        file.delete();
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
