package com.iot.nero.middleware.dfs.index.server;

import com.iot.nero.middleware.dfs.index.constant.CONSTANT;
import com.iot.nero.middleware.dfs.common.entity.response.Response;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   7:37 PM
 */
public class ServerHandler implements Runnable, IHandler {

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;

    ByteBuffer input = ByteBuffer.allocate(1024);
    byte[] receivedBytes;

    Response<Object> response;

    static final int READING = 0, SENDING = 1;

    int state = READING;

    public ServerHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;

        this.socketChannel.configureBlocking(false);
        this.selectionKey = socketChannel.register(selector, 0);

        this.selectionKey.attach(this);
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    public void run() {
        try {
            if (this.state == READING) {
                this.read();
            } else if (this.state == SENDING) {
                this.write();
            }
        } catch (IOException e) {
            CONSTANT.pInfo(CONSTANT.CLIENT_OFFLINE);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void read() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        int readCount = socketChannel.read(this.input);
        this.readBytes(readCount);
        this.state = SENDING;
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    protected void readBytes(int readCount) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (readCount > 0) {
            this.input.flip();
            this.receivedBytes = new byte[readCount];
            byte[] array = this.input.array();
            System.arraycopy(array, 0, this.receivedBytes, 0, readCount);
            this.readProcess();
            this.input.clear();
        }
    }


    @Override
    public synchronized void readProcess() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        // need override
    }

    @Override
    public void writeProcess() throws IOException {
        // need override
    }


    void write() throws IOException {
        this.writeProcess();
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        this.state = READING;
    }
}
