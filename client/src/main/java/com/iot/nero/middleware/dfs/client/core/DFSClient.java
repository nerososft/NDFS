package com.iot.nero.middleware.dfs.client.core;

import com.iot.nero.middleware.dfs.client.constant.RpcType;
import com.iot.nero.middleware.dfs.common.cluster.discovery.NodeDiscovery;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.DataNode;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.IndexNode;
import com.iot.nero.middleware.dfs.common.constant.CONSTANT;
import com.iot.nero.middleware.dfs.common.entity.auth.Authentication;
import com.iot.nero.middleware.dfs.common.entity.request.InvokeEntity;
import com.iot.nero.middleware.dfs.common.entity.request.Request;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.exceptions.InvalidHostNameException;
import com.iot.nero.middleware.dfs.common.exceptions.InvalidPostException;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.common.utils.ProtoStuffUtils;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import static com.iot.nero.middleware.dfs.client.constant.RpcType.DATA;
import static com.iot.nero.middleware.dfs.client.constant.RpcType.INDEX;
import static com.iot.nero.middleware.dfs.common.constant.CONSTANT.pInfo;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/6
 * Time   10:58 AM
 */
public class DFSClient {

    SocketChannel socketChannel;
    Selector selector;


    private DataNode dataNode;
    private IndexNode indexNode;
    private NodeDiscovery nodeDiscovery;

    private DFSErrorListener dfsErrorListener;

    private String address = "";
    private int port = -1;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    byte[] receivedBytes;

    private static DFSClient client = new DFSClient();

    public DFSClient() {
    }

    public void init() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        nodeDiscovery = new NodeDiscovery();
        this.selector = Selector.open();

    }


    public static DFSClient getInstance() {
        return client;
    }

    public <T> T getRemoteProxy(final RpcType rpcType, final Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request<InvokeEntity> request = new Request<>(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());

                        InvokeEntity invokeEntity = new InvokeEntity();
                        invokeEntity.setaClass(interfaceClass);
                        invokeEntity.setMethodName(method.getName());
                        invokeEntity.setParameterType(method.getParameterTypes());
                        invokeEntity.setArgs(args);

                        if(ConfigFactory.getConfig().getAuth()){
                            Authentication authentication = new Authentication(
                                    ConfigFactory.getConfig().getAuthKey(),
                                    ConfigFactory.getConfig().getAuthSecret());
                            request.setAuthentication(authentication);
                        }

                        request.setRequestType((byte) 0x03);
                        request.setData(invokeEntity);


                        if (nodeDiscovery != null) {
                            if (rpcType == INDEX) {
                                indexNode = nodeDiscovery.discoverIndexNode(); // 发现索引节点
                                address = indexNode.getHost();
                                port = indexNode.getPort();

                            }
                            if (rpcType == DATA) {
                                dataNode = nodeDiscovery.discoverDataNode(); // 发现数据节点
                                address = dataNode.getHost();
                                port = dataNode.getPort();
                            }
                        }

                        if ("".equals(address)) {
                            throw new InvalidHostNameException(CONSTANT.INVALID_HOST_NAME);
                        }
                        if (port < 0 || port > 65535) {
                            throw new InvalidPostException(CONSTANT.INVALID_PORT);
                        }

                        socketChannel = SocketChannel.open(new InetSocketAddress(address, port));
                        socketChannel.configureBlocking(false);
                        socketChannel.write(ByteBuffer.wrap(Snappy.compress(ProtoStuffUtils.serializer(request))));
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        return getResult();

                    }
                }
        );
    }

    private synchronized Object getResult() throws IOException {
        while (this.selector.select() > 0) {
            for (SelectionKey sk : this.selector.selectedKeys()) {
                this.selector.selectedKeys().remove(sk);
                if (sk.isReadable()) {
                    SocketChannel sc = (SocketChannel) sk.channel();
                    this.buffer.clear();
                    int readCount = sc.read(this.buffer);
                    if (readCount > 0) {
                        this.buffer.flip();
                        this.receivedBytes = new byte[readCount];
                        byte[] array = this.buffer.array();
                        System.arraycopy(array, 0, this.receivedBytes, 0, readCount);
                        Response<Object> response = ProtoStuffUtils.deserializer(Snappy.uncompress(receivedBytes), Response.class);
                        this.buffer.clear();
                        if (response.isStatus()) {
                            return response.getData();
                        }
                        if(dfsErrorListener!=null) {
                            dfsErrorListener.onError(response); // 调用错误处理
                        }
                        pInfo("(Error) "+response.getMsg());
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public DFSErrorListener getDfsErrorListener() {
        return dfsErrorListener;
    }

    public void setDfsErrorListener(DFSErrorListener dfsErrorListener) {
        this.dfsErrorListener = dfsErrorListener;
    }
}
