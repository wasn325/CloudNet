package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

/**
 * Created by Tareko on 21.12.2017.
 */
public class VaultChatImpl extends Chat {

    public VaultChatImpl(final Permission perms) {
        super(perms);
    }

    @Override
    public String getName() {
        return "CloudNet-Chat";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPlayerPrefix(final String s, final String s1) {
        final OfflinePlayer offlinePlayer = getPlayer(s1);
        return offlinePlayer.getPermissionEntity().getPrefix() != null ? offlinePlayer.getPermissionEntity()
                                                                                      .getPrefix() : offlinePlayer.getPermissionEntity()
                                                                                                                  .getHighestPermissionGroup(
                                                                                                                      CloudAPI.getInstance()
                                                                                                                              .getPermissionPool())
                                                                                                                  .getPrefix();
    }

    @Override
    public void setPlayerPrefix(final String s, final String s1, final String s2) {
        final OfflinePlayer offlinePlayer = getPlayer(s1);
        offlinePlayer.getPermissionEntity().setPrefix(s2);
        update(offlinePlayer);
    }

    @Override
    public String getPlayerSuffix(final String s, final String s1) {
        final OfflinePlayer offlinePlayer = getPlayer(s1);
        return offlinePlayer.getPermissionEntity().getSuffix() != null ? offlinePlayer.getPermissionEntity()
                                                                                      .getSuffix() : offlinePlayer.getPermissionEntity()
                                                                                                                  .getHighestPermissionGroup(
                                                                                                                      CloudAPI.getInstance()
                                                                                                                              .getPermissionPool())
                                                                                                                  .getSuffix();
    }

    @Override
    public void setPlayerSuffix(final String s, final String s1, final String s2) {
        final OfflinePlayer offlinePlayer = getPlayer(s1);
        offlinePlayer.getPermissionEntity().setSuffix(s2);
        update(offlinePlayer);
    }

    @Override
    public String getGroupPrefix(final String s, final String s1) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            return permissionGroup.getPrefix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(final String s, final String s1, final String s2) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            permissionGroup.setPrefix(s2);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public String getGroupSuffix(final String s, final String s1) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            return permissionGroup.getSuffix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(final String s, final String s1, final String s2) {
        final PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            permissionGroup.setSuffix(s2);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public int getPlayerInfoInteger(final String s, final String s1, final String s2, final int i) {
        return 0;
    }

    @Override
    public void setPlayerInfoInteger(final String s, final String s1, final String s2, final int i) {

    }

    @Override
    public int getGroupInfoInteger(final String s, final String s1, final String s2, final int i) {
        return 0;
    }

    @Override
    public void setGroupInfoInteger(final String s, final String s1, final String s2, final int i) {

    }

    @Override
    public double getPlayerInfoDouble(final String s, final String s1, final String s2, final double v) {
        return 0;
    }

    @Override
    public void setPlayerInfoDouble(final String s, final String s1, final String s2, final double v) {

    }

    @Override
    public double getGroupInfoDouble(final String s, final String s1, final String s2, final double v) {
        return 0;
    }

    @Override
    public void setGroupInfoDouble(final String s, final String s1, final String s2, final double v) {

    }

    @Override
    public boolean getPlayerInfoBoolean(final String s, final String s1, final String s2, final boolean b) {
        return false;
    }

    @Override
    public void setPlayerInfoBoolean(final String s, final String s1, final String s2, final boolean b) {

    }

    @Override
    public boolean getGroupInfoBoolean(final String s, final String s1, final String s2, final boolean b) {
        return false;
    }

    @Override
    public void setGroupInfoBoolean(final String s, final String s1, final String s2, final boolean b) {

    }

    @Override
    public String getPlayerInfoString(final String s, final String s1, final String s2, final String s3) {
        return null;
    }

    @Override
    public void setPlayerInfoString(final String s, final String s1, final String s2, final String s3) {

    }

    @Override
    public String getGroupInfoString(final String s, final String s1, final String s2, final String s3) {
        return null;
    }

    @Override
    public void setGroupInfoString(final String s, final String s1, final String s2, final String s3) {

    }

    private void update(final OfflinePlayer offlinePlayer) {
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
