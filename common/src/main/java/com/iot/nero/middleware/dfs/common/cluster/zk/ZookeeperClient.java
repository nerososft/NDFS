package com.iot.nero.middleware.dfs.common.cluster.zk;

import com.google.gson.Gson;
import com.iot.nero.middleware.dfs.common.constant.CONSTANT;
import com.iot.nero.middleware.dfs.common.constant.NodeType;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.DataNode;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.IndexNode;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.Node;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午12:35
 */
public class ZookeeperClient {

    private ZkClient zkc;

    /**
     * zookeeper地址
     */
    private String connectAddress = "www.cenocloud.com:2181";


    /**
     * session超时时间
     */
    private int sessionTimeout = 10000;//ms

    public ZookeeperClient(String connectAddr, int sessionTimeout) {
        this.connectAddress = connectAddr;
        this.sessionTimeout = sessionTimeout;
        connect();
        init();
    }


    /**
     * 获取储存集群
     *
     * @return
     */
    public List<DataNode> getDataNodes() throws IOException {
        List<DataNode> dataNodeList = new ArrayList<>();

        List<String> dataList = zkc.getChildren("/NDFS/DATA");
        for (String data : dataList) {
            String rp = "/NDFS/DATA/" + data;
            byte[] dataIn = zkc.readData(rp);
            Gson gson = new Gson();
            dataNodeList.add(gson.fromJson(Snappy.uncompressString(dataIn), DataNode.class));
        }

        if (dataNodeList.isEmpty()) {
            CONSTANT.pInfo("(DataNodes) Data nodes not found!");
        } else {
            CONSTANT.pInfo("(DataNodes:" + dataNodeList.size() + ") " + dataNodeList);
        }
        return dataNodeList;
    }

    /**
     * 获取文件备份集群
     *
     * @return
     */
    public List<DataNode> getSlaveNodes() throws IOException {
        List<DataNode> slaveNodeList = new ArrayList<>();
        List<String> slaveList = zkc.getChildren("/NDFS/SLAVE");
        for (String slave : slaveList) {
            String rp = "/NDFS/SLAVE/" + slave;
            byte[] data = zkc.readData(rp);
            Gson gson = new Gson();
            slaveNodeList.add(gson.fromJson(Snappy.uncompressString(data), DataNode.class));
        }
        CONSTANT.pInfo("(DataSlaveNodes:" + slaveNodeList.size() + ") " + slaveNodeList);
        return slaveNodeList;
    }


    /**
     * 获取监控集群
     *
     * @return
     */
    public List<IndexNode> getMonitorNodes() throws IOException {
        List<IndexNode> monitorNodeList = new ArrayList<>();

        List<String> monitorList = zkc.getChildren("/NDFS/MONITOR");
        for (String monitor : monitorList) {
            String rp = "/NDFS/MONITOR/" + monitor;
            byte[] data = zkc.readData(rp);
            Gson gson = new Gson();
            monitorNodeList.add(gson.fromJson(Snappy.uncompressString(data), IndexNode.class));
        }
        if (monitorNodeList.isEmpty()) {
            CONSTANT.pInfo("(DataNodes) Index nodes not found!");
        } else {
            CONSTANT.pInfo("(IndexNodes:" + monitorNodeList.size() + ")" + monitorNodeList);
        }
        return monitorNodeList;
    }

    public void connect() {
        zkc = new ZkClient(new ZkConnection(connectAddress), sessionTimeout);
    }

    public void init() {
        initNode();
    }

    public void subscribeDataChanges(IZkDataListener iZkDataListener){
        zkc.subscribeDataChanges("/NDFS",iZkDataListener);
    }


    public void createIndexNode(String nodeName, Node node) throws IOException {
        // 创建临时索引节点

        zkc.createEphemeral("/NDFS/MONITOR/" + nodeName, Snappy.compress(node.toJson()));
    }

    public void createNode(NodeType nodeType, String nodeName, DataNode node) throws IOException {
        switch (nodeType) {
            case INDEX:
                zkc.createEphemeral("/NDFS/MONITOR/" + nodeName, Snappy.compress(node.toJson()));
                break;
            case DATA_MASTER:
                zkc.createEphemeral("/NDFS/DATA/" + nodeName, Snappy.compress(node.toJson()));
                break;
            case DATA_SLAVE:
                zkc.createEphemeral("/NDFS/SLAVE/" + nodeName, Snappy.compress(node.toJson()));
                break;
            default:
                break;
        }
    }


    private void initNode() {
        if (!zkc.exists("/NDFS")) {
            zkc.createPersistent("/NDFS", "Nero 分布式文件储存系统");
        }

        if (!zkc.exists("/NDFS/MONITOR")) {
            zkc.createPersistent("/NDFS/MONITOR", "文件索引集群");
        }

        if (!zkc.exists("/NDFS/DATA")) {
            zkc.createPersistent("/NDFS/DATA", "文件储存集群");
        }

        if (!zkc.exists("/NDFS/SLAVE")) {
            zkc.createPersistent("/NDFS/SLAVE", "文件备份集群");
        }
    }
}
