package net.md_5.bungee.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConfiguration extends ConfigurationProvider {

    private final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
        @Override
        protected Yaml initialValue() {
            final Representer representer = new Representer() {
                {
                    representers.put(Configuration.class, new Represent() {
                        @Override
                        public Node representData(final Object data) {
                            return represent(((Configuration) data).self);
                        }
                    });
                }
            };

            final DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            return new Yaml(new Constructor(), representer, options);
        }
    };

    YamlConfiguration() {
    }

    @Override
    public void save(final Configuration config, final File file) throws IOException {
        try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            save(config, writer);
        }
    }

    @Override
    public void save(final Configuration config, final Writer writer) {
        yaml.get().dump(config.self, writer);
    }

    @Override
    public Configuration load(final File file) throws IOException {
        return load(file, null);
    }

    @Override
    public Configuration load(final File file, final Configuration defaults) throws IOException {
        try (final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return load(reader, defaults);
        }
    }

    @Override
    public Configuration load(final Reader reader) {
        return load(reader, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(final Reader reader, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(final InputStream is) {
        return load(is, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(final InputStream is, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(final String string) {
        return load(string, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(final String string, final Configuration defaults) {
        Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }
}
