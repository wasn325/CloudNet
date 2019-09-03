/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.player;

public class PlayerExecutor {

    protected boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void sendPlayer(final CloudPlayer cloudPlayer, final String server) {
    }

    public void kickPlayer(final CloudPlayer cloudPlayer, final String reason) {
    }

    public void sendMessage(final CloudPlayer cloudPlayer, final String message) {
    }

    public void sendActionbar(final CloudPlayer cloudPlayer, final String message) {
    }

    public void sendTitle(final CloudPlayer cloudPlayer,
                          final String title,
                          final String subTitle,
                          final int fadeIn,
                          final int stay,
                          final int fadeOut) {
    }

}
