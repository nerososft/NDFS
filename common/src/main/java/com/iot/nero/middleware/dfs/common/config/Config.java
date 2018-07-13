package com.iot.nero.middleware.dfs.common.config;


import com.iot.nero.middleware.dfs.common.annotation.ConfigClass;
import com.iot.nero.middleware.dfs.common.annotation.ConfigField;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午12:49
 */

@ConfigClass
public class Config {

    @ConfigField("chunk.list-file-path")
    private String chunkListFilePath;
    @ConfigField("chunk.chunk-file-path")
    private String chunkFilePath;

    @ConfigField("server.host")
    private String host = "localhost";
    @ConfigField("server.port")
    private Integer port = 1080;


    @ConfigField("zookeeper.host")
    private String zookeeperHost;

    @ConfigField("zookeeper.timeout")
    private Integer zookeeperTimeout;

    @ConfigField("load-balance.type")
    private String loadBalanceType;



    @ConfigField("node.name")
    private String nodeName;
    @ConfigField("node.note")
    private String nodeNote;

    @ConfigField("node.volume")
    private String volume;
    @ConfigField("node.type")
    private String nodeType;
    @ConfigField("node.master")
    private String masterName;


    @ConfigField("data.tamper-proof.enable")
    private Boolean tamperProof;


    @ConfigField("data.chunk-size")
    private Integer chunkSize;

    @ConfigField("data.max-upload-size")
    private Integer maxUploadSize;


    @ConfigField("auth.enable")
    private Boolean auth;
    @ConfigField("auth.key")
    private String authKey;
    @ConfigField("auth.secret")
    private String authSecret;

    public Integer getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(Integer maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public String getChunkListFilePath() {
        return chunkListFilePath;
    }

    public void setChunkListFilePath(String chunkListFilePath) {
        this.chunkListFilePath = chunkListFilePath;
    }

    public String getChunkFilePath() {
        return chunkFilePath;
    }

    public void setChunkFilePath(String chunkFilePath) {
        this.chunkFilePath = chunkFilePath;
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

    public String getZookeeperHost() {
        return zookeeperHost;
    }

    public void setZookeeperHost(String zookeeperHost) {
        this.zookeeperHost = zookeeperHost;
    }

    public Integer getZookeeperTimeout() {
        return zookeeperTimeout;
    }

    public void setZookeeperTimeout(Integer zookeeperTimeout) {
        this.zookeeperTimeout = zookeeperTimeout;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeNote() {
        return nodeNote;
    }

    public void setNodeNote(String nodeNote) {
        this.nodeNote = nodeNote;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }


    public Boolean getTamperProof() {
        return tamperProof;
    }

    public void setTamperProof(Boolean tamperProof) {
        this.tamperProof = tamperProof;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Boolean getAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }
    public String getLoadBalanceType() {
        return loadBalanceType;
    }

    public void setLoadBalanceType(String loadBalanceType) {
        this.loadBalanceType = loadBalanceType;
    }

    @Override
    public String toString() {
        return "Config{" +
                "chunkListFilePath='" + chunkListFilePath + '\'' +
                ", chunkFilePath='" + chunkFilePath + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", zookeeperHost='" + zookeeperHost + '\'' +
                ", zookeeperTimeout=" + zookeeperTimeout +
                ", loadBalanceType='" + loadBalanceType + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeNote='" + nodeNote + '\'' +
                ", volume='" + volume + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", masterName='" + masterName + '\'' +
                ", tamperProof=" + tamperProof +
                ", chunkSize=" + chunkSize +
                ", maxUploadSize=" + maxUploadSize +
                ", auth=" + auth +
                ", authKey='" + authKey + '\'' +
                ", authSecret='" + authSecret + '\'' +
                '}';
    }
}
