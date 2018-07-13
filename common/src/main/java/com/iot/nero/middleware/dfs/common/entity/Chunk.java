package com.iot.nero.middleware.dfs.common.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   上午8:39
 */
public class Chunk implements Serializable {

    private String dataNodeName; // data node name

    private String chunkPath; // chunk 存放路径
    private String chunkName; //chunkName chunk UUID
    private Integer chunkSize; //当前 chunk 字节大小


    public Chunk() {
    }

    public Chunk(String dataNodeName, String chunkPath, String chunkName, Integer chunkSize) {
        this.dataNodeName = dataNodeName;
        this.chunkPath = chunkPath;
        this.chunkName = chunkName;
        this.chunkSize = chunkSize;
    }

    public String getDataNodeName() {
        return dataNodeName;
    }

    public void setDataNodeName(String dataNodeName) {
        this.dataNodeName = dataNodeName;
    }

    public String getChunkPath() {
        return chunkPath;
    }

    public void setChunkPath(String chunkPath) {
        this.chunkPath = chunkPath;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "dataNodeName='" + dataNodeName + '\'' +
                ", chunkPath='" + chunkPath + '\'' +
                ", chunkName='" + chunkName + '\'' +
                ", chunkSize=" + chunkSize +
                '}';
    }
}
