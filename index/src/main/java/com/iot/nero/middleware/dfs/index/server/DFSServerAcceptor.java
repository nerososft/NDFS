package com.iot.nero.middleware.dfs.index.server;

import com.iot.nero.middleware.dfs.index.constant.CONSTANT;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   下午3:27
 */
public class DFSServerAcceptor implements Runnable {
    final ServerSocketChannel serverSocketChannel;
    final Selector selector;
    final Map<Class<?>, Class<?>> service;

    public DFSServerAcceptor(Map<Class<?>, Class<?>> service, ServerSocketChannel serverSocketChannel, Selector selector) {
        this.service = service;
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        if (this.serverSocketChannel != null) {
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                CONSTANT.pInfo("(SocketChannel)" + socketChannel);
                new WorkerServeHandler(service, socketChannel, this.selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
