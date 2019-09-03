package de.dytanic.cloudnet.lib.zip;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipConverter 2.0
 */
public final class ZipConverter {

    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private ZipConverter() {
    }

    public static Path convert(final Path zipPath, final Path... directories) throws IOException {
        if (directories == null) {
            return null;
        }

        if (!Files.exists(zipPath)) {
            Files.createFile(zipPath);
        }

        try (final OutputStream outputStream = Files.newOutputStream(zipPath); final ZipOutputStream zipOutputStream = new ZipOutputStream(
            outputStream,
            StandardCharsets.UTF_8)) {
            for (final Path dir : directories) {
                if (Files.exists(dir)) {
                    convert0(zipOutputStream, dir);
                }
            }
        }
        return zipPath;
    }

    private static void convert0(final ZipOutputStream zipOutputStream, final Path directory) throws IOException {
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(directory.relativize(file).toString()));
                    Files.copy(file, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (final Exception ex) {
                    zipOutputStream.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static byte[] convert(final Path... directories) {
        if (directories == null) {
            return EMPTY_BYTE_ARRAY;
        }

        try (final ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream(); final ZipOutputStream zipOutputStream = new ZipOutputStream(
            byteBuffer,
            StandardCharsets.UTF_8)) {

            for (final Path dir : directories) {
                if (Files.exists(dir)) {
                    convert0(zipOutputStream, dir);
                }
            }

            return byteBuffer.toByteArray();

        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Path extract(final Path zipPath, final Path targetDirectory) throws IOException {
        if (zipPath == null || targetDirectory == null || !Files.exists(zipPath)) {
            return targetDirectory;
        }

        try (final InputStream inputStream = Files.newInputStream(zipPath)) {
            extract0(inputStream, targetDirectory);
        }

        return targetDirectory;
    }

    public static void extract0(final InputStream inputStream, final Path targetDirectory) throws IOException {
        try (final ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry zipEntry = null;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                extract1(zipInputStream, zipEntry, targetDirectory);
                zipInputStream.closeEntry();
            }
        }
    }

    private static void extract1(final ZipInputStream zipInputStream, final ZipEntry zipEntry, final Path targetDirectory) throws
        IOException {
        final byte[] buffer = new byte[0x1FFF];
        final Path file = Paths.get(targetDirectory.toString(), zipEntry.getName());

        if (zipEntry.isDirectory()) {
            if (!Files.exists(file)) {
                Files.createDirectories(file);
            }
        } else {
            final Path parent = file.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.createFile(file);
            try (final OutputStream outputStream = Files.newOutputStream(file)) {
                int len;
                while ((len = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }
        }
    }

    public static Path extract(final byte[] zipData, final Path targetDirectory) throws IOException {
        if (zipData == null || zipData.length == 0 || targetDirectory == null) {
            return targetDirectory;
        }

        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipData)) {
            extract0(byteArrayInputStream, targetDirectory);
        }

        return targetDirectory;
    }

}
