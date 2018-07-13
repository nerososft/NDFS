package com.iot.nero.middleware.dfs.common.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   5:17 PM
 */
public class DataPosition implements Serializable {
    private String hashCode;
    private String nodeName;

    public DataPosition() {
    }

    public DataPosition(String hashCode, String nodeName) {
        this.hashCode = hashCode;
        this.nodeName = nodeName;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return "DataPosition{" +
                "hashCode='" + hashCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                '}';
    }
}
