package de.dytanic.cloudnet.lib.utility.document;

import com.google.gson.JsonElement;
import de.dytanic.cloudnet.lib.interfaces.Nameable;

import java.io.File;
import java.util.Set;

/**
 * Created by Tareko on 21.05.2017.
 */
public interface DocumentAbstract<T extends DocumentAbstract> extends Nameable {

    T append(String key, String value);

    T append(String key, Number value);

    T append(String key, Boolean value);

    T append(String key, JsonElement value);

    @Deprecated
    T append(String key, Object value);

    T remove(String key);

    Set<String> keys();

    String getString(String key);

    int getInt(String key);

    long getLong(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    float getFloat(String key);

    short getShort(String key);

    String convertToJson();

    boolean saveAsConfig(File backend);

    boolean saveAsConfig(String path);

    T getDocument(String key);

}
