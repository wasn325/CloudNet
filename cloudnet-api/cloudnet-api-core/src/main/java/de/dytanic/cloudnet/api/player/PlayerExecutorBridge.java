/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PlayerExecutorBridge extends PlayerExecutor {

    public static final PlayerExecutorBridge INSTANCE = new PlayerExecutorBridge();

    private static final String CHANNEL_NAME = "cloudnet_internal";

    public PlayerExecutorBridge() {
        this.available = true;
    }

    @Override
    public void sendPlayer(final CloudPlayer cloudPlayer, final String server) {
        if (cloudPlayer == null || server == null) {
            return;
        }

        CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME, "sendPlayer", new Document("uniqueId",
                                                                                                  cloudPlayer.getUniqueId()).append("name",
                                                                                                                                    cloudPlayer
                                                                                                                                        .getName())
                                                                                                                            .append("server",
                                                                                                                                    server));
    }

    @Override
    public void kickPlayer(final CloudPlayer cloudPlayer, final String reason) {
        if (cloudPlayer == null || reason == null) {
            return;
        }

        CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME, "kickPlayer", new Document("uniqueId",
                                                                                                  cloudPlayer.getUniqueId()).append("name",
                                                                                                                                    cloudPlayer
                                                                                                                                        .getName())
                                                                                                                            .append("reason",
                                                                                                                                    reason));
    }

    @Override
    public void sendMessage(final CloudPlayer cloudPlayer, final String message) {
        if (cloudPlayer == null || message == null) {
            return;
        }

        CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME, "sendMessage", new Document("message", message).append("name",
                                                                                                                              cloudPlayer.getName())
                                                                                                                      .append("uniqueId",
                                                                                                                              cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendActionbar(final CloudPlayer cloudPlayer, final String message) {
        if (cloudPlayer == null || message == null) {
            return;
        }

        CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME,
                                                         "sendActionbar",
                                                         new Document("message", message).append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendTitle(final CloudPlayer cloudPlayer,
                          final String title,
                          final String subTitle,
                          final int fadeIn,
                          final int stay,
                          final int fadeOut) {
        CloudAPI.getInstance().sendCustomSubProxyMessage(CHANNEL_NAME, "sendTitle", new Document("uniqueId",
                                                                                                 cloudPlayer.getUniqueId()).append("title",
                                                                                                                                   title)
                                                                                                                           .append(
                                                                                                                               "subTitle",
                                                                                                                               subTitle)
                                                                                                                           .append("stay",
                                                                                                                                   stay)
                                                                                                                           .append("fadeIn",
                                                                                                                                   fadeIn)
                                                                                                                           .append("fadeOut",
                                                                                                                                   fadeOut));
    }
}
