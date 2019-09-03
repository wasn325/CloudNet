package de.dytanic.cloudnet.bridge.internal.util;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PermissionCheckEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Tareko on 10.01.2018.
 */
public class CloudPlayerCommandSender implements CommandSender {

    private final CloudPlayer cloudPlayer;

    public CloudPlayerCommandSender(final CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public String getName() {
        return cloudPlayer.getName();
    }

    @Override
    public void sendMessage(final String s) {
        cloudPlayer.getPlayerExecutor().sendMessage(cloudPlayer, s);
    }

    @Override
    public void sendMessages(final String... strings) {
        for (final String m : strings) {
            sendMessage(m);
        }
    }

    @Override
    public void sendMessage(final BaseComponent... baseComponents) {
        for (final BaseComponent m : baseComponents) {
            sendMessage(m);
        }
    }

    @Override
    public void sendMessage(final BaseComponent baseComponent) {
        sendMessage(baseComponent.toLegacyText());
    }

    @Override
    public Collection<String> getGroups() {
        return new ArrayList<>();
    }

    @Override
    public void addGroups(final String... strings) {

    }

    @Override
    public void removeGroups(final String... strings) {

    }

    @Override
    public boolean hasPermission(final String s) {
        return new PermissionCheckEvent(this, s, false).hasPermission();
    }

    @Override
    public void setPermission(final String s, final boolean b) {

    }

    @Override
    public Collection<String> getPermissions() {
        return new ArrayDeque<>();
    }
}
