package de.dytanic.cloudnet.lib.player.permission;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

/**
 * Calls
 */
public class PermissionEntity {

    protected UUID uniqueId;

    protected java.util.Map<String, Boolean> permissions;

    protected String prefix;

    protected String suffix;

    protected Collection<GroupEntityData> groups;

    public PermissionEntity(final UUID uniqueId,
                            final Map<String, Boolean> permissions,
                            final String prefix,
                            final String suffix,
                            final Collection<GroupEntityData> groups) {
        this.uniqueId = uniqueId;
        this.permissions = permissions;
        this.prefix = prefix;
        this.suffix = suffix;
        this.groups = groups;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public boolean hasPermission(final PermissionPool permissionPool, final String permission, final String group) {
        if (permission != null && (permission.equals("bukkit.broadcast") || permission.equals("bukkit.broadcast.admin"))) {
            return true;
        }

        if (permissionPool == null || permission == null) {
            return false;
        }

        if (permissions.containsKey(permission) && !permissions.get(permission)) {
            return false;
        } else if (hasWildcardPermission(permission)) {
            return true;
        } else if (permissions.containsKey("*") && permissions.get("*")) {
            return true;
        } else if ((permissions.containsKey(permission)) && permissions.get(permission)) {
            return true;
        }

        for (final GroupEntityData implg : groups) {
            if (!permissionPool.getGroups().containsKey(implg.getGroup())) {
                continue;
            }
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(implg.getGroup());

            if (hasWildcardPermission(permissionGroup, permission, group)) {
                return true;
            }

            if (checkAccess(permissionGroup, permission, group)) {
                return true;
            }

            for (final String implGroup : permissionGroup.getImplementGroups()) {
                if (!permissionPool.getGroups().containsKey(implGroup)) {
                    continue;
                }

                final PermissionGroup subGroup = permissionPool.getGroups().get(implGroup);
                if (checkAccess(subGroup, permission, group)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasWildcardPermission(final String permission) {
        for (final Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            if (entry.getKey().endsWith("*") && entry.getKey().length() > 1 && permission.startsWith(entry.getKey().substring(0,
                                                                                                                              entry.getKey()
                                                                                                                                   .length() -
                                                                                                                              1))) {
                return entry.getValue();
            }
        }

        return false;
    }

    private boolean hasWildcardPermission(final PermissionGroup permissionGroup, final String permission, final String group) {
        for (final Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet()) {
            if (entry.getKey().endsWith("*") && entry.getKey().length() > 1 && permission.startsWith(entry.getKey().substring(0,
                                                                                                                              entry.getKey()
                                                                                                                                   .length() -
                                                                                                                              1))) {
                return entry.getValue();
            }
        }

        if (group != null && permissionGroup.getServerGroupPermissions().containsKey(group)) {
            for (final String perms : permissionGroup.getServerGroupPermissions().get(group)) {
                if (perms.endsWith("*") && perms.length() > 1 && permission.startsWith(perms.substring(0, perms.length() - 1))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkAccess(final PermissionGroup permissionGroup, final String permission, final String group) {
        if ((permissionGroup.getPermissions().containsKey("*") && !permissionGroup.getPermissions().get("*")) ||
            (permissionGroup.getPermissions().containsKey(permission) && !permissionGroup.getPermissions().get(permission))) {
            return false;
        }

        if ((permissionGroup.getPermissions().containsKey("*") && permissionGroup.getPermissions().get("*"))) {
            return true;
        }

        if ((permissionGroup.getPermissions().containsKey(permission) && permissionGroup.getPermissions().get(permission))) {
            return true;
        }

        if (group != null) {
            if (permissionGroup.getServerGroupPermissions().containsKey(group)) {
                return permissionGroup.getServerGroupPermissions().get(group).contains(permission) ||
                       permissionGroup.getServerGroupPermissions().get(group).contains("*");
            }
        }

        return false;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(final Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public PermissionGroup getHighestPermissionGroup(final PermissionPool permissionPool) {
        return this.groups.stream().map(groupEntityData -> permissionPool.getGroups().get(groupEntityData.getGroup())).min(Comparator
                                                                                                                                    .comparingInt(
                                                                                                                                        PermissionGroup::getTagId))
                   .orElse(null);
    }

    /*= -------------------------------------------------------------------------------- =*/

    public Collection<GroupEntityData> getGroups() {
        return groups;
    }

    public void setGroups(final Collection<GroupEntityData> groups) {
        this.groups = groups;
    }

    public boolean isInGroup(final String group) {
        return CollectionWrapper.filter(this.groups, value -> value.getGroup().equals(group)) != null;
    }

}
