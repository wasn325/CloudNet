/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.web.server.handler.WebHandler;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.URI;
import java.util.List;

/**
 * Class that handles incoming channels and instructs a {@link WebServer} to work.
 */
final class WebServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * The web server that is handled by this handler instance.
     */
    private final WebServer webServer;

    /**
     * Constructs a new web server handler for a given web server.
     *
     * @param webServer the web server to handle the inbound channel for.
     */
    public WebServerHandler(final WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            return;
        }
        final HttpRequest httpRequest = ((HttpRequest) msg);

        final URI uri = new URI(httpRequest.uri());
        String path = uri.getRawPath();
        if (path == null) {
            path = NetworkUtils.SLASH_STRING;
        }

        if (path.endsWith(NetworkUtils.SLASH_STRING)) {
            path = path.substring(0, path.length() - 1);
        }

        final List<WebHandler> webHandlers = webServer.getWebServerProvider().getHandlers(path);
        if (!webHandlers.isEmpty()) {
            FullHttpResponse fullHttpResponse = null;
            for (final WebHandler webHandler : webHandlers) {
                if (path.isEmpty() || path.equals(NetworkUtils.SLASH_STRING)) {
                    fullHttpResponse = webHandler.handleRequest(ctx,
                                                                new QueryDecoder(uri.getQuery()),
                                                                new PathProvider(path, new WrappedMap()),
                                                                httpRequest);
                } else {
                    final String[] array = path.replaceFirst(NetworkUtils.SLASH_STRING, NetworkUtils.EMPTY_STRING)
                                               .split(NetworkUtils.SLASH_STRING);
                    final String[] pathArray = webHandler.getPath().replaceFirst(NetworkUtils.SLASH_STRING, NetworkUtils.EMPTY_STRING)
                                                         .split(NetworkUtils.SLASH_STRING);
                    final WrappedMap wrappedMap = new WrappedMap();
                    for (short i = 0; i < array.length; i++) {
                        if (pathArray[i].startsWith("{") && pathArray[i].endsWith("}")) {
                            wrappedMap.append(pathArray[i].replace("{", NetworkUtils.EMPTY_STRING).replace("}", NetworkUtils.EMPTY_STRING),
                                              array[i]);
                        }
                    }
                    fullHttpResponse = webHandler.handleRequest(ctx,
                                                                new QueryDecoder(uri.getQuery()),
                                                                new PathProvider(path, wrappedMap),
                                                                httpRequest);
                }
            }
            if (fullHttpResponse == null) {
                fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(),
                                                               HttpResponseStatus.NOT_FOUND,
                                                               Unpooled.wrappedBuffer("Error 404 page not found!".getBytes()));
            }
            fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
            ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            final FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(),
                                                                                  HttpResponseStatus.NOT_FOUND);
            fullHttpResponse.headers().set("Access-Control-Allow-Origin", "*");
            ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
