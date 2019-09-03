package de.dytanic.cloudnet.lib.player.permission;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tareko on 01.06.2017.
 */
public class PermissionGroup {

    protected String name;
    protected String color;
    protected String prefix;
    protected String suffix;
    protected String display;
    protected int tagId;
    protected int joinPower;
    protected boolean defaultGroup;
    protected Map<String, Boolean> permissions;
    protected Map<String, List<String>> serverGroupPermissions;
    protected Map<String, Object> options;
    protected List<String> implementGroups;

    public PermissionGroup(final String name,
                           final String color,
                           final String prefix,
                           final String suffix,
                           final String display,
                           final int tagId,
                           final int joinPower,
                           final boolean defaultGroup,
                           final Map<String, Boolean> permissions,
                           final Map<String, List<String>> serverGroupPermissions,
                           final Map<String, Object> options,
                           final List<String> implementGroups) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.display = display;
        this.tagId = tagId;
        this.joinPower = joinPower;
        this.defaultGroup = defaultGroup;
        this.permissions = permissions;
        this.serverGroupPermissions = serverGroupPermissions;
        this.options = options;
        this.implementGroups = implementGroups;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (display != null ? display.hashCode() : 0);
        result = 31 * result + tagId;
        result = 31 * result + joinPower;
        result = 31 * result + (defaultGroup ? 1 : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        result = 31 * result + (serverGroupPermissions != null ? serverGroupPermissions.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (implementGroups != null ? implementGroups.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionGroup)) {
            return false;
        }
        final PermissionGroup that = (PermissionGroup) o;
        return tagId == that.tagId && joinPower == that.joinPower && defaultGroup == that.defaultGroup && Objects.equals(name, that.name) &&
               Objects.equals(color, that.color) && Objects.equals(prefix, that.prefix) && Objects.equals(suffix, that.suffix) &&
               Objects.equals(display, that.display) && Objects.equals(permissions, that.permissions) && Objects.equals(
            serverGroupPermissions,
            that.serverGroupPermissions) && Objects.equals(options, that.options) && Objects.equals(implementGroups, that.implementGroups);
    }

    @Override
    public String toString() {
        return "PermissionGroup{" + "name='" + name + '\'' + ", color='" + color + '\'' + ", prefix='" + prefix + '\'' + ", suffix='" +
               suffix + '\'' + ", display='" + display + '\'' + ", tagId=" + tagId + ", joinPower=" + joinPower + ", defaultGroup=" +
               defaultGroup + ", permissions=" + permissions + ", serverGroupPermissions=" + serverGroupPermissions + ", options=" +
               options + ", implementGroups=" + implementGroups + '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(final String display) {
        this.display = display;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public void setJoinPower(final int joinPower) {
        this.joinPower = joinPower;
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

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(final Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(final int tagId) {
        this.tagId = tagId;
    }

    public List<String> getImplementGroups() {
        return implementGroups;
    }

    public void setImplementGroups(final List<String> implementGroups) {
        this.implementGroups = implementGroups;
    }

    public Map<String, List<String>> getServerGroupPermissions() {
        return serverGroupPermissions;
    }

    public void setServerGroupPermissions(final Map<String, List<String>> serverGroupPermissions) {
        this.serverGroupPermissions = serverGroupPermissions;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(final Map<String, Object> options) {
        this.options = options;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(final boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
