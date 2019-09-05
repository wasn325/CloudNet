package de.dytanic.cloudnet.setup.spigot;

import jline.console.ConsoleReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class SpigotBuilder {

    private final static String buildToolsUrl = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
    private final static String versionsUrl = "https://hub.spigotmc.org/versions/";
    private static Process exec;

    /**
     * Start the process to choice the Spigot version And build after choice
     *
     * @param reader to read the answer
     */
    public static void start(final ConsoleReader reader) {
        System.out.println("Fetching Spigot versions");
        final List<String> versions = loadVersions();
        System.out.println("Available Spigot versions:");
        System.out.println("-----------------------------------------------------------------------------");
        versions.forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Please select a version to continue the install process");
        String answer = null;
        while (answer == null) {
            String name = null;
            try {
                name = reader.readLine().toLowerCase();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            final String finalAnswer = name;
            if (versions.stream().anyMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
                answer = name;
                buildSpigot(finalAnswer);
            } else if (versions.stream().noneMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
                System.out.println("This version does not exist!");
            }
        }
    }

    /**
     * @return A list for available versions of spigot
     */
    private static LinkedList<String> loadVersions() {
        final LinkedList<String> array = new LinkedList<>();
        try {
            final Document doc = Jsoup.connect(versionsUrl).userAgent(
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11").get();
            for (final Element file : doc.select("a")) {
                final String rawName = file.attr("href");
                if (rawName.contains("../")) {
                    continue;
                }
                if (!(rawName.contains("latest.json") || rawName.startsWith("1."))) {
                    continue;
                }
                array.add(rawName.replace(".json", ""));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * Build the spigot or copy if exists
     *
     * @param version the version of spigot
     */
    private static void buildSpigot(final String version) {
        final File builder = new File("local/builder/spigot");
        final File buildFolder = new File(builder, version);
        try {
            Files.createDirectories(buildFolder.toPath());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        final File buildTools = new File(buildFolder, "buildtools.jar");
        if (!buildTools.exists()) {
            runBuildTools(version, buildFolder, buildTools);
        } else {
            if (Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0) {
                System.out.println("Skipping build");
                System.out.println("Copying spigot.jar");
                try {
                    Files.copy(new FileInputStream(Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName()
                            .startsWith("spigot-")))[0]),
                        Paths.get("local/spigot.jar"),
                        StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                runBuildTools(version, buildFolder, buildTools);
            }
        }

    }

    /**
     * Run the build tools of the jar not exists
     *
     * @param version     the version of spigot
     * @param buildFolder the folder in there are build
     * @param buildTools  the path of the build tools
     */
    private static void runBuildTools(final String version, final File buildFolder, final File buildTools) {
        try {
            final long startTime = System.currentTimeMillis();
            Files.createDirectories(buildFolder.toPath());
            System.out.println("Downloading BuildTools.jar...");
            final URLConnection connection = new URL(buildToolsUrl).openConnection();
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (final InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, Paths.get(buildTools.toURI()), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Download was successfully completed!");
            System.out.println("Building Spigot " + version);
            exec = Runtime.getRuntime().exec(String.format("java -jar buildtools.jar --rev %s", version), null, buildFolder);
            PaperBuilder.printProcessOutputToConsole(exec);
            if (Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0) {
                Files.copy(new FileInputStream(Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName()
                        .startsWith("spigot-")))[0]),
                    Paths.get("local/spigot.jar"),
                    StandardCopyOption.REPLACE_EXISTING);
                final long endTime = System.currentTimeMillis();
                final long minutes = ((endTime - startTime) / 1000) / 60;
                final long seconds = ((endTime - startTime) / 1000) % 60;
                System.out.printf("Total Build Time %dMin %dSec\n", minutes, seconds);
            } else {
                deleteBuildFolder(buildFolder);
                buildSpigot(version);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the build folder
     *
     * @param buildFolder foldr path
     *
     * @throws IOException throws if tree throws
     */
    static void deleteBuildFolder(final File buildFolder) throws IOException {
        Files.walkFileTree(buildFolder.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                final File toFile = file.toFile();
                toFile.setExecutable(true, false);
                toFile.setWritable(true, false);
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {

                final File file = dir.toFile();
                file.setExecutable(true, false);
                file.setWritable(true, false);
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println("Build failed. Retry Build");
    }

    public static Process getExec() {
        return exec;
    }
}
