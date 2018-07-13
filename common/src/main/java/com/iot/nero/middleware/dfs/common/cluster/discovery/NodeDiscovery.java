package com.iot.nero.middleware.dfs.common.cluster.discovery;

import com.iot.nero.middleware.dfs.common.cluster.node.NodeManager;
import com.iot.nero.middleware.dfs.common.constant.NodeType;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.DataNode;
import com.iot.nero.middleware.dfs.common.cluster.node.entity.IndexNode;
import org.I0Itec.zkclient.IZkDataListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iot.nero.middleware.dfs.common.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/6
 * Time   2:07 PM
 */
public class NodeDiscovery {

    List<DataNode> dataNodeList;
    List<DataNode> slaveNodeList;
    List<IndexNode> monitorNodeList;

    Map<DataNode, List<DataNode>> masterSlaveRelationShip;


    private Integer dataNodePollingIndex = 0;
    private Integer indexNodePollingIndex = 0;

    private void updateData() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        this.dataNodeList = NodeManager.getDefaultInstance().getDataNodes();
        this.slaveNodeList = NodeManager.getDefaultInstance().getSlaveNodes();
        this.monitorNodeList = NodeManager.getDefaultInstance().getMonitorNodes();
        this.masterSlaveRelationShip = new HashMap<>();
        createMasterSlaveRelationShip();
    }

    public NodeDiscovery() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        updateData();


        // 监听zookeeper数据变化
        NodeManager.getDefaultInstance().subscribeDataChanges(new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                pInfo("(ZookeeperDataChanged) " + s);
                updateData();
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                pInfo("(ZookeeperDataDeleted) " + s);
                updateData();
            }
        });
    }

    /**
     * 获取数据节点
     * 负载均衡
     *
     * @return
     */
    public DataNode discoverDataNode() {
        DataNode dataNode = this.dataNodeList.get(dataNodePollingIndex);

        if (dataNodePollingIndex < dataNodeList.size() - 1) {
            dataNodePollingIndex = dataNodePollingIndex + 1;
        } else {
            dataNodePollingIndex = 0;
        }

        pInfo("(Load balancing to data node) " + dataNode);
        return dataNode;

    }

    /**
     * 获取索引节点
     * 负载均衡
     *
     * @return
     */
    public IndexNode discoverIndexNode() {
        IndexNode monitorNode = this.monitorNodeList.get(indexNodePollingIndex);

        if (indexNodePollingIndex < monitorNodeList.size() - 1) {
            indexNodePollingIndex = indexNodePollingIndex + 1;
        } else {
            indexNodePollingIndex = 0;
        }

        pInfo("(Load balancing to index node) " + monitorNode);
        return monitorNode;
    }


    /**
     * 产生逻辑主备关系，其实是平级的
     *
     * @return
     * @throws IOException
     */
    public Map<DataNode, List<DataNode>> createMasterSlaveRelationShip() throws IOException {
        List<DataNode> dataNodes = dataNodeList;
        List<DataNode> slaveNodes = slaveNodeList;

        for (DataNode dataNode : dataNodes) {
            List<DataNode> slaveList = new ArrayList<>();
            for (DataNode slaveNode : slaveNodes) {
                if (slaveNode.getNodeType() == NodeType.DATA_SLAVE) {
                    if (slaveNode.getSlaveFor().equals(dataNode.getNodeName())) {
                        slaveList.add(slaveNode);
                    }
                }
            }
            masterSlaveRelationShip.put(dataNode, slaveList);
        }
        return masterSlaveRelationShip;
    }

    /**
     * 查找逻辑备份
     *
     * @param masterNode
     * @return
     */
    public List<DataNode> getSlaveNode(DataNode masterNode) {
        return masterSlaveRelationShip.get(masterNode);
    }
}
