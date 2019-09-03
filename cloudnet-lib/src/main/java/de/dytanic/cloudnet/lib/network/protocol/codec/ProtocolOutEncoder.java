/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import de.dytanic.cloudnet.lib.network.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolOutEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final Object o, final ByteBuf byteBuf) throws Exception {
        final ProtocolBuffer protocolBuffer = ProtocolProvider.protocolBuffer(byteBuf);

        if (o instanceof ProtocolRequest) {
            final ProtocolRequest protocolRequest = ((ProtocolRequest) o);
            final IProtocol iProtocol = ProtocolProvider.getProtocol(protocolRequest.getId());
            final ProtocolStream protocolStream = iProtocol.createElement(protocolRequest.getElement());
            protocolStream.write(protocolBuffer);
        } else {
            for (final IProtocol iProtocol : ProtocolProvider.protocols()) {
                final ProtocolStream protocolStream = iProtocol.createElement(o);
                if (protocolStream != null) {
                    protocolStream.write(protocolBuffer);
                    break;
                }
            }
        }
    }
}
