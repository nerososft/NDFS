package com.iot.nero.middleware.dfs.common.cluster.node.entity;

import com.iot.nero.middleware.dfs.common.constant.NodeType;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:19
 */
public class DataNode extends Node implements Serializable {

    private NodeType nodeType;
    private String slaveFor;
    private String groupName;

    public DataNode(NodeType nodeType, String slaveFor, String groupName) {
        this.nodeType = nodeType;
        this.slaveFor = slaveFor;
        this.groupName = groupName;
    }

    public DataNode(String nodeName, String host, Integer port, NodeType nodeType, String slaveFor, String groupName) {
        super(nodeName, host, port);
        this.nodeType = nodeType;
        this.slaveFor = slaveFor;
        this.groupName = groupName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getSlaveFor() {
        return slaveFor;
    }

    public void setSlaveFor(String slaveFor) {
        this.slaveFor = slaveFor;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "DataNode{" +
                "nodeType=" + nodeType +
                ", slaveFor='" + slaveFor + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
