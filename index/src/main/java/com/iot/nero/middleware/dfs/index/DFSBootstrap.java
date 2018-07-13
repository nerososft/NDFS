package com.iot.nero.middleware.dfs.index;

import com.iot.nero.middleware.dfs.common.annotation.Service;
import com.iot.nero.middleware.dfs.common.cluster.node.NodeManager;
import com.iot.nero.middleware.dfs.common.cluster.zk.ZookeeperClient;
import com.iot.nero.middleware.dfs.common.config.Config;
import com.iot.nero.middleware.dfs.common.config.ConfigLoader;
import com.iot.nero.middleware.dfs.common.constant.CONSTANT;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.IndexNode;
import com.iot.nero.middleware.dfs.common.constant.NodeType;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.index.server.DSFServer;
import com.iot.nero.middleware.dfs.index.server.IServer;
import com.iot.nero.middleware.dfs.common.utils.ClassUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iot.nero.middleware.dfs.index.constant.CONSTANT.printNdfsInfo;
import static com.iot.nero.middleware.dfs.common.utils.System.checkSystem;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:33
 */
public class DFSBootstrap {

    private Integer DFS_SERVER_LISTEN_PORT = 8849;


    Map<Class<?>,Class<?>> classClassMap;



    public void initZk() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        IndexNode indexNode = new IndexNode(
                ConfigFactory.getConfig().getNodeName(),
                ConfigFactory.getConfig().getHost(),
                ConfigFactory.getConfig().getPort()
        );
        NodeManager.getDefaultInstance().createIndexNode(ConfigFactory.getConfig().getNodeName(), indexNode);
    }


    public void initService(){
        classClassMap = new HashMap<>();// 服务容器
        List<Class<?>> clsList = ClassUtil.getAllClassByPackageName(App.class.getPackage()); // 服务扫描

        for(Class<?> s:clsList){
            Service at = s.getAnnotation(Service.class);
            if (at!= null) {
                Class<?> interfaces[] = s.getInterfaces();//获得实现的所有接口
                for (Class<?> inter : interfaces) {
                    CONSTANT.pInfo("(Service) ["+s.getName()+"]<-["+inter.getName()+"]");
                    classClassMap.put(inter, s);// 服务注册
                }
            }
        }
    }

    public void runFileListener() throws IOException {
        IServer ndfsServer = new DSFServer(classClassMap,DFS_SERVER_LISTEN_PORT);
        ndfsServer.start();
    }

    public void start() {
        printNdfsInfo();
        checkSystem();
        try {
            initZk();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        initService();
        try {
            runFileListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
