package com.iot.nero.middleware.dfs.data;

import com.iot.nero.middleware.dfs.common.annotation.Service;
import com.iot.nero.middleware.dfs.common.cluster.node.NodeManager;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.DataNode;
import com.iot.nero.middleware.dfs.common.constant.NodeType;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.data.server.DSFServer;
import com.iot.nero.middleware.dfs.data.server.IServer;
import com.iot.nero.middleware.dfs.common.utils.ClassUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.pInfo;
import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.printNdfsInfo;
import static com.iot.nero.middleware.dfs.common.utils.System.checkSystem;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:33
 */
public class DFSBootstrap {

    private Integer DFS_SERVER_LISTEN_PORT = 1080;


    Map<Class<?>, Class<?>> classClassMap;

    public DFSBootstrap() {

    }


    public void initZk() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        DataNode dataNode = new DataNode(
                ConfigFactory.getConfig().getNodeName(),
                ConfigFactory.getConfig().getHost(),
                ConfigFactory.getConfig().getPort(),
                NodeType.DATA_MASTER,
                null,
                ConfigFactory.getConfig().getVolume()
                );
        NodeManager.getDefaultInstance().createNode(NodeType.DATA_MASTER, ConfigFactory.getConfig().getNodeName(), dataNode);
    }


    public void initService() throws IllegalAccessException, InstantiationException {
        classClassMap = new HashMap<>();// 服务容器
        List<Class<?>> clsList = ClassUtil.getAllClassByPackageName(App.class.getPackage()); // 服务扫描

        for (Class<?> s : clsList) {
            Service at = s.getAnnotation(Service.class);
            if (at != null) {
                Class<?> interfaces[] = s.getInterfaces();//获得实现的所有接口
                for (Class<?> inter : interfaces) {//打印
                    pInfo("(Service) [" + s.getName() + "]<-[" + inter.getName() + "]");
                    classClassMap.put(inter, s);// 服务注册
                }
            }
        }
    }

    public void runFileListener() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        DFS_SERVER_LISTEN_PORT = ConfigFactory.getConfig().getPort();
        IServer idfsServer = new DSFServer(classClassMap, DFS_SERVER_LISTEN_PORT);
        idfsServer.start();
    }

    public void start() {
        printNdfsInfo();
        checkSystem();

        try {
            initZk();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            initService();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            runFileListener();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
