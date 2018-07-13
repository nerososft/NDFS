package com.iot.nero.middleware.dfs.common.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   上午8:40
 */


/**
 * 使用snappy压缩
 */
public class BlockData implements Serializable,Comparable<BlockData> {

    private String  chunkName;                  // 存放地址
    private String  fileName;                   // 存放文件名称
    private String  fileType;                   // 存放文件类型
    private Integer index;                      // 文件在chunk内位置索引
    private String  fileHash;                   // 压缩前文件hashCode
    private Integer fileSize;                   // 压缩前文件大小
    private String  compressionAlgorithm;       // 压缩算法
    private Integer compressionSize;            // 压缩后文件大小
    private String  chainHash;                  // 链哈希
    private Boolean del;                        // 是否删除
    private Long    lastMdfTime;                // 最后一次修改时间
    private Long    createTime;                 // 创建时间

    public BlockData() {
    }

    public BlockData(String chunkName, String fileName, String fileType, Integer index, String fileHash, Integer fileSize, String compressionAlgorithm, Integer compressionSize, String chainHash, Boolean del, Long lastMdfTime, Long createTime) {
        this.chunkName = chunkName;
        this.fileName = fileName;
        this.fileType = fileType;
        this.index = index;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
        this.compressionAlgorithm = compressionAlgorithm;
        this.compressionSize = compressionSize;
        this.chainHash = chainHash;
        this.del = del;
        this.lastMdfTime = lastMdfTime;
        this.createTime = createTime;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public void setCompressionAlgorithm(String compressionAlgorithm) {
        this.compressionAlgorithm = compressionAlgorithm;
    }

    public Integer getCompressionSize() {
        return compressionSize;
    }

    public void setCompressionSize(Integer compressionSize) {
        this.compressionSize = compressionSize;
    }

    public Long getLastMdfTime() {
        return lastMdfTime;
    }

    public void setLastMdfTime(Long lastMdfTime) {
        this.lastMdfTime = lastMdfTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Boolean getDel() {
        return del;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }

    public String getChainHash() {

        return chainHash;
    }

    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    @Override
    public int compareTo(BlockData o) {
        int i = this.getIndex() - o.getIndex(); //按照索引位置排序
        return i;
    }

    @Override
    public String toString() {
        return "BlockData{" +
                "chunkName='" + chunkName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", index=" + index +
                ", fileHash='" + fileHash + '\'' +
                ", fileSize=" + fileSize +
                ", compressionAlgorithm='" + compressionAlgorithm + '\'' +
                ", compressionSize=" + compressionSize +
                ", chainHash='" + chainHash + '\'' +
                ", del=" + del +
                ", lastMdfTime=" + lastMdfTime +
                ", createTime=" + createTime +
                '}';
    }


}
