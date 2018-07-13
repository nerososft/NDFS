package com.iot.nero.middleware.dfs.client;

import com.iot.nero.middleware.dfs.client.constant.RpcType;
import com.iot.nero.middleware.dfs.client.core.DFSClient;
import com.iot.nero.middleware.dfs.client.core.DFSErrorListener;
import com.iot.nero.middleware.dfs.common.entity.DataPosition;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.service.IIndexService;

import java.io.IOException;
import java.util.UUID;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/4
 * Time   2:25 PM
 */
public class IndexDemo {
    public static void main(String[] args) throws IOException {
        final DFSClient dfsClient = DFSClient.getInstance();
        try {
            dfsClient.init();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        // 错误事件监听
        dfsClient.setDfsErrorListener(new DFSErrorListener() {
            @Override
            public void onError(Response<Object> response) {
                // 错误处理
                switch (response.getCode()){
                    case 1: // 未知的请求类型
                        System.out.println(response.getMsg());
                    break;
                    case 2: // 节点认证失败
                        System.out.println(response.getMsg());
                        break;
                    // todo ....
                }
            }
        });

        final IIndexService fileService = dfsClient.getRemoteProxy(RpcType.INDEX, IIndexService.class);
        DataPosition dataPosition = fileService.getFileByHashCode(UUID.randomUUID().toString());
        System.out.println(dataPosition);
    }
}
