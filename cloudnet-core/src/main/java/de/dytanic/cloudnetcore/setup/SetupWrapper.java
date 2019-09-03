/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.*;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;

import java.util.function.Consumer;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupWrapper {

    private final String name;

    public SetupWrapper(final CommandSender commandSender, final String name) {
        this.name = name;

        final Setup setup = new Setup().setupCancel(new ISetupCancel() {
            @Override
            public void cancel() {
                System.out.println("Setup was cancelled");
            }
        }).setupComplete(new ISetupComplete() {
            @Override
            public void complete(final Document data) {
                final String host = data.getString("address");
                final String user = data.getString("user");

                final WrapperMeta wrapperMeta = new WrapperMeta(name, host, user);
                CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                commandSender.sendMessage("Wrapper [" + wrapperMeta.getId() + "] was registered on CloudNet");
            }
        });

        final Consumer<SetupRequest> request = setup::request;
        request.accept(new SetupRequest("address",
                                        "What's the IP address of the wrapper?",
                                        "Specified IP address is invalid!",
                                        SetupResponseType.STRING,
                                        new Catcher<Boolean, String>() {
                                            @Override
                                            public Boolean doCatch(final String key) {
                                                return key.split("\\.").length == 4 && !key.equalsIgnoreCase("127.0.0.1");
                                            }
                                        }));
        request.accept(new SetupRequest("user",
                                        "What's the user of the wrapper?",
                                        "Specified name is invalid!",
                                        SetupResponseType.STRING,
                                        new Catcher<Boolean, String>() {
                                            @Override
                                            public Boolean doCatch(final String key) {
                                                return CloudNet.getInstance().getUser(key) != null;
                                            }
                                        }));
        setup.start(CloudNet.getLogger().getReader());
    }

    public String getName() {
        return name;
    }
}
