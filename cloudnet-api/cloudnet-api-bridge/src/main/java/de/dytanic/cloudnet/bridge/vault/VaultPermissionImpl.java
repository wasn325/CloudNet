package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.milkbowl.vault.permission.Permission;

import java.util.Optional;

/**
 * Created by Tareko on 25.11.2017.
 */
public class VaultPermissionImpl extends Permission {

    @Override
    public String getName() {
        return "CloudNet-Permission";
    }

    @Override
    public boolean isEnabled() {
        return CloudAPI.getInstance().getPermissionPool() != null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(final String world, final String player, final String permission) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        final boolean hasPermission = permissionEntity.hasPermission(CloudAPI.getInstance().getPermissionPool(), permission, null);
        CloudAPI.getInstance().getLogger().finest(player + " hasPermission \"" + permission + "\": " + hasPermission);
        return hasPermission;
    }

    @Override
    public boolean playerAdd(final String world, final String player, final String permission) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().put(permission, true);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " added permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean playerRemove(final String world, final String player, final String permission) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().remove(permission);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " removed permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean groupHas(final String world, final String group, final String permission) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        return permissionGroup.getPermissions().getOrDefault(permission, false);
    }

    @Override
    public boolean groupAdd(final String world, final String group, final String permission) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        permissionGroup.getPermissions().put(permission, true);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        CloudAPI.getInstance().getLogger().finest(group + " added permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean groupRemove(final String world, final String group, final String permission) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        permissionGroup.getPermissions().remove(permission);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        CloudAPI.getInstance().getLogger().finest(group + " removed permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean playerInGroup(final String world, final String player, final String group) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        return permissionEntity.isInGroup(group);
    }

    @Override
    public boolean playerAddGroup(final String world, final String player, final String group) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();

        final Optional<GroupEntityData> groupEntityData = permissionEntity.getGroups()
                                                                          .stream()
                                                                          .filter(ged -> ged.getGroup()
                                                                                            .equalsIgnoreCase(group))
                                                                          .findFirst();
        groupEntityData.ifPresent(entityData -> permissionEntity.getGroups().remove(entityData));

        permissionEntity.getGroups().add(new GroupEntityData(group, 0));
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " added to group \"" + group + '"');
        return true;
    }

    @Override
    public boolean playerRemoveGroup(final String world, final String player, final String group) {
        final OfflinePlayer offlinePlayer = getPlayer(player);
        final PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getGroups().stream().filter(ged -> ged.getGroup().equalsIgnoreCase(group)).findFirst().ifPresent(ged -> {
            permissionEntity.getGroups().remove(ged);
        });

        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " removed from group \"" + group + '"');
        return true;
    }

    @Override
    public String[] getPlayerGroups(final String world, final String player) {
        final PermissionEntity permissionEntity = getPlayer(player).getPermissionEntity();
        return (String[]) permissionEntity.getGroups().stream().map(GroupEntityData::getGroup).toArray();
    }

    @Override
    public String getPrimaryGroup(final String world, final String player) {
        return getPlayer(player).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
    }

    @Override
    public String[] getGroups() {
        return CloudAPI.getInstance().getPermissionPool().getGroups().keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    private void updatePlayer(final OfflinePlayer offlinePlayer) {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    private OfflinePlayer getPlayer(final String name) {
        OfflinePlayer offlinePlayer = CloudServer.getInstance().getCachedPlayer(name);

        if (offlinePlayer == null) {
            offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
        }

        return offlinePlayer;
    }
}
