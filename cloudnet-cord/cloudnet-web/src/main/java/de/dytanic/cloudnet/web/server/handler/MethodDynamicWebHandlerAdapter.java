package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Adapter for the dynamic method web handler.
 * Returns null by default.
 */
public class MethodDynamicWebHandlerAdapter extends MethodDynamicWebHandler {

    @Override
    public FullHttpResponse connect(final ChannelHandlerContext channelHandlerContext,
                                    final QueryDecoder queryDecoder,
                                    final PathProvider pathProvider,
                                    final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse delete(final ChannelHandlerContext channelHandlerContext,
                                   final QueryDecoder queryDecoder,
                                   final PathProvider pathProvider,
                                   final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse get(final ChannelHandlerContext channelHandlerContext,
                                final QueryDecoder queryDecoder,
                                final PathProvider pathProvider,
                                final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse put(final ChannelHandlerContext channelHandlerContext,
                                final QueryDecoder queryDecoder,
                                final PathProvider pathProvider,
                                final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse head(final ChannelHandlerContext channelHandlerContext,
                                 final QueryDecoder queryDecoder,
                                 final PathProvider pathProvider,
                                 final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse options(final ChannelHandlerContext channelHandlerContext,
                                    final QueryDecoder queryDecoder,
                                    final PathProvider pathProvider,
                                    final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse patch(final ChannelHandlerContext channelHandlerContext,
                                  final QueryDecoder queryDecoder,
                                  final PathProvider pathProvider,
                                  final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse trace(final ChannelHandlerContext channelHandlerContext,
                                  final QueryDecoder queryDecoder,
                                  final PathProvider pathProvider,
                                  final HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse post(final ChannelHandlerContext channelHandlerContext,
                                 final QueryDecoder queryDecoder,
                                 final PathProvider pathProvider,
                                 final HttpRequest httpRequest) throws Exception {
        return null;
    }
}
