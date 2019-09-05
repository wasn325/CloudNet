package de.dytanic.cloudnet.lib.player.permission;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PermissionPool {

    public static final Type TYPE = new TypeToken<PermissionPool>() {}.getType();
    private final java.util.Map<String, PermissionGroup> groups = new HashMap<>();
    private boolean available = true;

    public PermissionEntity getNewPermissionEntity(final PlayerConnection playerWhereAmI) {
        return new PermissionEntity(playerWhereAmI.getUniqueId(),
            new HashMap<>(),
            null, null, Collections.singletonList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

    public PermissionGroup getDefaultGroup() {
        for (final PermissionGroup group : groups.values()) {
            if (group.isDefaultGroup()) {
                return group;
            }
        }
        return null;
    }

    public PermissionEntity getNewPermissionEntity(final OfflinePlayer playerWhereAmI) {
        return new PermissionEntity(playerWhereAmI.getUniqueId(),
            new HashMap<>(),
            null, null, Collections.singletonList(new GroupEntityData(getDefaultGroup().getName(), 0L)));
    }

    public Map<String, PermissionGroup> getGroups() {
        return groups;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(final boolean available) {
        this.available = available;
    }
}
