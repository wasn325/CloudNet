package de.dytanic.cloudnet.lib.utility.document;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Tareko on 21.05.2017.
 */
public class Document implements DocumentAbstract<Document> {

    protected static final JsonParser PARSER = new JsonParser();
    public static Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    protected String name;
    protected JsonObject dataCatcher;
    private File file;

    public Document(final String name) {
        this.name = name;
        this.dataCatcher = new JsonObject();
    }

    public Document(final String name, final JsonObject source) {
        this.name = name;
        this.dataCatcher = source;
    }

    public Document(final File file, final JsonObject jsonObject) {
        this.file = file;
        this.dataCatcher = jsonObject;
    }

    public Document(final String key, final String value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document append(final String key, final String value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(final String key, final Number value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(final String key, final Boolean value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    @Override
    public Document append(final String key, final JsonElement value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.add(key, value);
        return this;
    }

    @Deprecated
    public Document append(final String key, final Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof Document) {
            this.append(key, (Document) value);
            return this;
        }
        this.dataCatcher.add(key, GSON.toJsonTree(value));
        return this;
    }

    @Override
    public Document remove(final String key) {
        this.dataCatcher.remove(key);
        return this;
    }

    public Set<String> keys() {
        final Set<String> c = new HashSet<>();

        for (final Map.Entry<String, JsonElement> x : dataCatcher.entrySet()) {
            c.add(x.getKey());
        }

        return c;
    }

    public String getString(final String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return dataCatcher.get(key).getAsString();
    }

    public int getInt(final String key) {
        if (!dataCatcher.has(key)) {
            return 0;
        }
        return dataCatcher.get(key).getAsInt();
    }

    public long getLong(final String key) {
        if (!dataCatcher.has(key)) {
            return 0L;
        }
        return dataCatcher.get(key).getAsLong();
    }

    public double getDouble(final String key) {
        if (!dataCatcher.has(key)) {
            return 0D;
        }
        return dataCatcher.get(key).getAsDouble();
    }

    public boolean getBoolean(final String key) {
        if (!dataCatcher.has(key)) {
            return false;
        }
        return dataCatcher.get(key).getAsBoolean();
    }

    public float getFloat(final String key) {
        if (!dataCatcher.has(key)) {
            return 0F;
        }
        return dataCatcher.get(key).getAsFloat();
    }

    public short getShort(final String key) {
        if (!dataCatcher.has(key)) {
            return 0;
        }
        return dataCatcher.get(key).getAsShort();
    }

    public String convertToJson() {
        return GSON.toJson(dataCatcher);
    }

    public boolean saveAsConfig(final File backend) {
        if (backend == null) {
            return false;
        }

        if (backend.exists()) {
            backend.delete();
        }

        try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), StandardCharsets.UTF_8)) {
            GSON.toJson(dataCatcher, (writer));
            return true;
        } catch (final IOException ex) {
            ex.getStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(final String path) {
        return saveAsConfig(Paths.get(path));
    }

    public Document getDocument(final String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return new Document(dataCatcher.get(key).getAsJsonObject());
    }

    public boolean saveAsConfig(final Path path) {
        try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            GSON.toJson(dataCatcher, outputStreamWriter);
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Document(final String key, final Object value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document append(final String key, final Document value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.add(key, value.dataCatcher);
        return this;
    }

    public Document(final String key, final Number value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document(final Document defaults) {
        this.dataCatcher = defaults.dataCatcher;
    }

    public Document(final Document defaults, final String name) {
        this.dataCatcher = defaults.dataCatcher;
        this.name = name;
    }

    public Document() {
        this.dataCatcher = new JsonObject();
    }

    public Document(final JsonObject source) {
        this.dataCatcher = source;
    }

    public static Document load(final String input) {
        try (final InputStreamReader reader = new InputStreamReader(new StringBufferInputStream(input), StandardCharsets.UTF_8)) {
            return new Document(PARSER.parse(new BufferedReader(reader)).getAsJsonObject());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new Document();
    }

    public static Document loadDocument(final File backend) {
        return loadDocument(backend.toPath());
    }

    public static Document loadDocument(final Path backend) {

        try (final InputStreamReader reader = new InputStreamReader(Files.newInputStream(backend),
            StandardCharsets.UTF_8); final BufferedReader bufferedReader = new BufferedReader(
            reader)) {
            final JsonObject object = PARSER.parse(bufferedReader).getAsJsonObject();
            return new Document(object);
        } catch (final Exception ex) {
            ex.getStackTrace();
        }
        return new Document();

        /*
        try
        {
            return new Document(PARSER.parse(new String(Files.readAllBytes(backend), StandardCharsets.UTF_8)).getAsJsonObject());
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Document();
        */
    }

    public static Document $loadDocument(final File backend) throws Exception {
        try {
            return new Document(PARSER.parse(new String(Files.readAllBytes(backend.toPath()), StandardCharsets.UTF_8)).getAsJsonObject());
        } catch (final Exception ex) {
            throw new Exception(ex);
        }
    }

    public static Document load(final JsonObject input) {
        return new Document(input);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public JsonObject obj() {
        return dataCatcher;
    }

    public Document append(final String key, final List<String> value) {
        if (value == null) {
            return this;
        }
        final JsonArray jsonElements = new JsonArray();

        for (final String b : value) {
            jsonElements.add(b);
        }

        this.dataCatcher.add(key, jsonElements);
        return this;
    }

    public Document appendValues(final java.util.Map<String, Object> values) {
        for (final java.util.Map.Entry<String, Object> valuess : values.entrySet()) {
            append(valuess.getKey(), valuess.getValue());
        }
        return this;
    }

    public JsonElement get(final String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return dataCatcher.get(key);
    }

    public <T> T getObject(final String key, final Class<T> class_) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        final JsonElement element = dataCatcher.get(key);

        return GSON.fromJson(element, class_);
    }

    public Document clear() {
        for (final String key : keys()) {
            remove(key);
        }
        return this;
    }

    public int size() {
        return this.dataCatcher.size();
    }

    public Document loadProperties(final Properties properties) {
        final Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            final Object x = enumeration.nextElement();
            this.append(x.toString(), properties.getProperty(x.toString()));
        }
        return this;
    }

    public boolean isEmpty() {
        return this.dataCatcher.size() == 0;
    }

    public JsonArray getArray(final String key) {
        return dataCatcher.get(key).getAsJsonArray();
    }

    @Deprecated
    public boolean saveAsConfig0(final File backend) {
        if (backend == null) {
            return false;
        }

        if (backend.exists()) {
            backend.delete();
        }

        try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), StandardCharsets.UTF_8)) {
            NetworkUtils.GSON.toJson(dataCatcher, (writer));
            return true;
        } catch (final IOException ex) {
            ex.getStackTrace();
        }
        return false;
    }

    public Document loadToExistingDocument(final File backend) {
        try (final InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), StandardCharsets.UTF_8)) {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            this.file = backend;
            return this;
        } catch (final Exception ex) {
            ex.getStackTrace();
        }
        return new Document();
    }

    public Document loadToExistingDocument(final Path path) {
        try (final InputStreamReader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            return this;
        } catch (final Exception ex) {
            ex.getStackTrace();
        }
        return new Document();
    }

    @Override
    public String toString() {
        return convertToJsonString();
    }

    public String convertToJsonString() {
        return dataCatcher.toString();
    }

    public <T> T getObject(final String key, final Type type) {
        if (!contains(key)) {
            return null;
        }

        return GSON.fromJson(dataCatcher.get(key), type);
    }

    public boolean contains(final String key) {
        return this.dataCatcher.has(key);
    }

    public byte[] toBytesAsUTF_8() {
        return convertToJsonString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toBytes() {
        return convertToJsonString().getBytes();
    }
}
