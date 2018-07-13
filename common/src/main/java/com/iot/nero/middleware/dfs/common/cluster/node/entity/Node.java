package com.iot.nero.middleware.dfs.common.cluster.node.entity;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/6
 * Time   2:33 PM
 */
public class Node implements Serializable {
    String nodeName;
    String host;
    Integer port;

    public Node() {
    }

    public Node(String nodeName, String host, Integer port) {
        this.nodeName = nodeName;
        this.host = host;
        this.port = port;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeName='" + nodeName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
