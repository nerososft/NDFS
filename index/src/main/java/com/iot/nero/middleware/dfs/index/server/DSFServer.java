package com.iot.nero.middleware.dfs.index.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;

import static com.iot.nero.middleware.dfs.index.constant.CONSTANT.pInfo;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午12:48
 */
public class DSFServer extends NioServer implements IServer {

    Integer port = 8848;


    final ServerSocketChannel serverSocketChannel;
    final Selector selector;
    final Map<Class<?>, Class<?>> service;

    public DSFServer(Map<Class<?>, Class<?>> service, Integer port) throws IOException {
        this.port = port;
        this.service = service;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);

        this.selector = Selector.open();
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new DFSServerAcceptor(service, this.serverSocketChannel, this.selector));

    }


    @Override
    public void start() {
        try {
            pInfo("(LISTENING) Ceno Distributed File System (CDFS) Listening on Port: " + port);
            this.run(this.selector);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // todo
    }

    @Override
    public void restart() {
        // todo
    }
}
