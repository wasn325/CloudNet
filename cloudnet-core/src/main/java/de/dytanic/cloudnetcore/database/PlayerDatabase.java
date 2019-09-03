/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.player.UpdatePlayerEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 01.07.2017.
 */
public class PlayerDatabase extends DatabaseUsable {

    public PlayerDatabase(final Database database) {
        super(database);
    }

    public OfflinePlayer registerPlayer(final PlayerConnection playerConnection) {
        final OfflinePlayer offlinePlayer = new OfflinePlayer(playerConnection.getUniqueId(),
                                                              playerConnection.getName(),
                                                              new Document(),
                                                              System.currentTimeMillis(),
                                                              System.currentTimeMillis(),
                                                              playerConnection,
                                                              new PermissionEntity(playerConnection.getUniqueId(),
                                                                             new HashMap<>(),
                                                                             null,
                                                                             null,
                                                                             new LinkedList<>()));
        database.insert(new DatabaseDocument(playerConnection.getUniqueId().toString()).append("offlinePlayer", offlinePlayer));
        return offlinePlayer;
    }

    public PlayerDatabase updatePlayer(final OfflinePlayer offlinePlayer) {
        CloudNet.getLogger().debug("PlayerDatabase updatePlayer offlinePlayer null: " + (offlinePlayer == null));
        if (offlinePlayer == null) {
            return this;
        }
        final Document document = database.getDocument(offlinePlayer.getUniqueId().toString());
        document.append("offlinePlayer", CloudPlayer.newOfflinePlayer(offlinePlayer));
        database.insert(document);
        CloudNet.getLogger().debug("PlayerDatabase updatePlayer call UpdatePlayerEvent");
        CloudNet.getInstance().getEventManager().callEvent(new UpdatePlayerEvent(offlinePlayer));
        return this;
    }

    public PlayerDatabase updateName(final UUID uuid, final String name) {
        final Document document = database.getDocument(uuid.toString());
        final OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
        offlinePlayer.setName(name);
        document.append("offlinePlayer", offlinePlayer);
        database.insert(document);
        return this;
    }

    public boolean containsPlayer(final UUID uuid) {
        return database.containsDoc(uuid.toString());
    }

    public PlayerDatabase updatePermissionEntity(final UUID uuid, final PermissionEntity permissionEntity) {
        final Document document = database.getDocument(uuid.toString());
        final OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
        offlinePlayer.setPermissionEntity(permissionEntity);
        document.append("offlinePlayer", offlinePlayer);
        database.insert(document);
        return this;
    }

    public OfflinePlayer getPlayer(final UUID uniqueId) {
        CloudNet.getLogger().debug("PlayerDatabase getPlayer uniqueId " + uniqueId);
        if (uniqueId == null) {
            return null;
        }
        final Document document = database.getDocument(uniqueId.toString());
        CloudNet.getLogger().debug("PlayerDatabase getPlayer document null: " + (document == null));
        if (document == null) {
            return null;
        }
        CloudNet.getLogger().debug("PlayerDatabase getPlayer offlinePlayer contained: " + document.contains("offlinePlayer"));
        return document.getObject("offlinePlayer", OfflinePlayer.TYPE);
    }

    public Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        database.loadDocuments();

        final Map<UUID, OfflinePlayer> map = new HashMap<>();

        for (final Document document : database.getDocs()) {
            final OfflinePlayer offlinePlayer = document.getObject("offlinePlayer", OfflinePlayer.TYPE);
            map.put(offlinePlayer.getUniqueId(), offlinePlayer);
        }

        return map;
    }

}
