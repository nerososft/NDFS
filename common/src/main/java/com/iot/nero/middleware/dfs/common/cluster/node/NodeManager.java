package com.iot.nero.middleware.dfs.common.cluster.node;


import com.iot.nero.middleware.dfs.common.cluster.zk.ZookeeperClient;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;

import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/21
 * Time   8:35 AM
 */
public class NodeManager {

    private static volatile ZookeeperClient zookeeperClient;

    public static ZookeeperClient getDefaultInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {

        if(zookeeperClient==null){
            synchronized (NodeManager.class) {
                if(zookeeperClient==null) {
                    zookeeperClient = new ZookeeperClient(ConfigFactory.getConfig().getZookeeperHost(), ConfigFactory.getConfig().getZookeeperTimeout());
                    return zookeeperClient;
                }
            }
        }
        return zookeeperClient;
    }
}
