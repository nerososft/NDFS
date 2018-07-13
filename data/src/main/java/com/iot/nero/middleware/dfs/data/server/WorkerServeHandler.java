package com.iot.nero.middleware.dfs.data.server;

import com.iot.nero.middleware.dfs.common.annotation.Inject;
import com.iot.nero.middleware.dfs.common.entity.auth.Authentication;
import com.iot.nero.middleware.dfs.common.entity.request.InvokeEntity;
import com.iot.nero.middleware.dfs.common.entity.request.Request;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.common.factory.FieldFactory;
import com.iot.nero.middleware.dfs.common.utils.ProtoStuffUtils;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.AUTHENTICATION_INCORRECT;
import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.UNKNOWN_REQUEST_TYPE;
import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.pInfo;

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
    private Map<Class<?>,FastClass> classFastClassCache = new HashMap<>();


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

        if(ConfigFactory.getConfig().getAuth()){
            Authentication authentication = new Authentication(
                    ConfigFactory.getConfig().getAuthKey(),
                    ConfigFactory.getConfig().getAuthSecret());
            if(!request.auth(authentication)){
               response  = new Response<>(request.getRequestId(),false, 2,AUTHENTICATION_INCORRECT);
                return ;
            }
        }


        switch (request.getRequestType()){
            case 0x01:
                InvokeEntity invokeEntity = (InvokeEntity) request.getData();

                FastClass fastClass = classFastClassCache.get(invokeEntity.getaClass());
                if(fastClass==null){
                     fastClass = FastClass.create(invokeEntity.getaClass());
                     classFastClassCache.put(invokeEntity.getaClass(),fastClass);
                }

                FastMethod fastMethod = fastClass.getMethod(
                        invokeEntity.getMethodName(),
                        invokeEntity.getParameterType());

                Class<?> service = interfaceClz.get(invokeEntity.getaClass());
                if(service==null){
                    throw new ClassNotFoundException();
                }

                Object serviceObj = service.newInstance();
                for(Field field:service.getDeclaredFields()){
                    Inject inject = field.getAnnotation(Inject.class);
                    if(inject!=null){
                        field.setAccessible(true);
                        field.set(serviceObj, FieldFactory.get(field));  // IOC 工厂
                    }
                }

                response = new Response<>(request.getRequestId(),true,
                        fastMethod.invoke(serviceObj, invokeEntity.getArgs()),0);
                break;
            default:
                pInfo("(REQUEST) 未知的请求.");
                response = new Response<>(request.getRequestId(),false, 1,UNKNOWN_REQUEST_TYPE);
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
