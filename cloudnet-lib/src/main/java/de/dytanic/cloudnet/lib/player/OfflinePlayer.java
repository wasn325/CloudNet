/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.player.permission.Permissible;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayer implements Nameable, Permissible {

    public static final Type TYPE = new TypeToken<OfflinePlayer>() {}.getType();

    protected UUID uniqueId;

    protected String name;

    protected Document metaData;

    protected Long lastLogin;

    protected Long firstLogin;

    protected PlayerConnection lastPlayerConnection;

    protected PermissionEntity permissionEntity;

    public OfflinePlayer(final UUID uniqueId,
                         final String name,
                         final Document metaData,
                         final Long lastLogin,
                         final Long firstLogin,
                         final PlayerConnection lastPlayerConnection,
                         final PermissionEntity permissionEntity) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.metaData = metaData;
        this.lastLogin = lastLogin;
        this.firstLogin = firstLogin;
        this.lastPlayerConnection = lastPlayerConnection;
        this.permissionEntity = permissionEntity;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Document getMetaData() {
        return metaData;
    }

    public void setMetaData(final Document metaData) {
        this.metaData = metaData;
    }

    public Long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(final Long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(final Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public PermissionEntity getPermissionEntity() {
        return permissionEntity;
    }

    public void setPermissionEntity(final PermissionEntity permissionEntity) {
        this.permissionEntity = permissionEntity;
    }

    public PlayerConnection getLastPlayerConnection() {
        return lastPlayerConnection;
    }

    public void setLastPlayerConnection(final PlayerConnection lastPlayerConnection) {
        this.lastPlayerConnection = lastPlayerConnection;
    }
}
