package com.iot.nero.middleware.dfs.index.server;

import com.iot.nero.middleware.dfs.common.constant.CONSTANT;
import com.iot.nero.middleware.dfs.common.entity.request.Request;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.entity.request.InvokeEntity;
import com.iot.nero.middleware.dfs.common.utils.ProtoStuffUtils;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.iot.nero.middleware.dfs.index.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   7:35 PM
 */

public class WorkerServeHandler extends ServerHandler {

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static final int PROCESSING = 2;
    private Map<Class<?>,Class<?>> interfaceClz;



    public WorkerServeHandler(Map<Class<?>,Class<?>> services,SocketChannel socketChannel, Selector selector) throws IOException {
        super(socketChannel, selector);
        interfaceClz = services;
    }

    @Override
    public void read() throws IOException {
        int readCount = socketChannel.read(this.input);
        if (readCount > 0) {
            this.state = PROCESSING;
            executorService.execute(new Processor(readCount));
        }
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    synchronized void processAndHandle(int readCount) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.readBytes(readCount);
        this.state = SENDING;
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    public synchronized void readProcess() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        Request<Object> request = ProtoStuffUtils.deserializer(Snappy.uncompress(receivedBytes),Request.class);
        pInfo("(READ)"+request.toString());

        switch (request.getRequestType()){
            case 0x01:
                InvokeEntity invokeEntity = (InvokeEntity) request.getData();
                FastClass fastClass = FastClass.create(invokeEntity.getaClass());
                FastMethod fastMethod = fastClass.getMethod(
                        invokeEntity.getMethodName(),
                        invokeEntity.getParameterType());
                Class<?> service = interfaceClz.get(invokeEntity.getaClass());
                if(service==null){
                    throw new ClassNotFoundException();
                }
                response = new Response<>(request.getRequestId(),true,
                        fastMethod.invoke(service.newInstance(), invokeEntity.getArgs()),0);
                break;
            default:
                pInfo("(REQUEST) 未知的请求.");
                response = new Response<>(request.getRequestId(),false,1,"未知的请求");
                break;
        }
    }


    @Override
    public void writeProcess() throws IOException {

        this.socketChannel.write(ByteBuffer.wrap(Snappy.compress(ProtoStuffUtils.serializer(response))));
        pInfo("(WRITE)"+response.toString());
    }

    class Processor implements Runnable {
        int readCount;

        public Processor(int readCount) {
            this.readCount = readCount;
        }

        public void run() {
            try {
                processAndHandle(readCount);
            } catch (IOException | IllegalAccessException | ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

}
