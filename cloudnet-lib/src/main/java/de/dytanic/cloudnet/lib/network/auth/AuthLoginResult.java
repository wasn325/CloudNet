package de.dytanic.cloudnet.lib.network.auth;

/**
 * Created by Tareko on 25.07.2017.
 */
public class AuthLoginResult {

    private final boolean value;

    public AuthLoginResult(final boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }
}
