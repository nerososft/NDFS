package com.iot.nero.middleware.dfs.common.cluster.node.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:19
 */
public class IndexNode extends Node implements Serializable {

    public IndexNode(String nodeName, String host, Integer port) {
        super(nodeName,host,port);
    }
}
