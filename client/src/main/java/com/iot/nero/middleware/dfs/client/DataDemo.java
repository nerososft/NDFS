package com.iot.nero.middleware.dfs.client;

import com.iot.nero.middleware.dfs.client.constant.RpcType;
import com.iot.nero.middleware.dfs.client.core.DFSClient;
import com.iot.nero.middleware.dfs.client.core.DFSErrorListener;
import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.entity.FileData;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.service.IFileService;
import org.xerial.snappy.Snappy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/4
 * Time   2:20 PM
 */
public class DataDemo {
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

        final IFileService fileService = dfsClient.getRemoteProxy(RpcType.DATA, IFileService.class);

        BlockData dataPosition = fileService.findFileByHash("hash1");
        System.out.println(dataPosition);

        try {
            FileData data1 = fileService.getDataBytes("hash2");
            byte[] bytes = new byte[data1.getData().size()];
            for(int i = 0;i<data1.getData().size();i++){
                bytes[i] = data1.getData().get(i);
            }
            try (RandomAccessFile readImg = new RandomAccessFile("/Users/neroyang/project/iotcloud/middleware/ndfs/test_data/steps_received.jpg", "rw")) {
                if(data1.getCompressMethod().equals("snappy")){
                    byte[] ok = Snappy.uncompress(bytes);
                    System.out.println(ok.length);
                    readImg.write(ok);
                }
                readImg.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(data1.getData().size());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
