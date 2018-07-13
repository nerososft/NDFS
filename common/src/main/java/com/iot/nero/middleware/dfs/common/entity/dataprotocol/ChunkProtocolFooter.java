package com.iot.nero.middleware.dfs.common.entity.dataprotocol;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/22
 * Time   9:11 AM
 */

/**
 * |存放文件名称(32)|
 * |存放文件类型(1)|
 * |文件在chunk内位置索引(4)|
 * |压缩前文件hashCode(32)|
 * |压缩前文件大小(32)|
 * |压缩算法(1)|
 * |压缩后文件大小(32)|
 * |是否删除(1)|
 * |最后一次修改时间(8)|
 * |创建时间(8)|
 * |预留,后期的扩展(32)|
 *
 * 每一条记录使用snappy所后存储，记录通过 0x1E 进行分割
 */
public class ChunkProtocolFooter implements Serializable{
    private byte[] fileName          = new byte[32];
    private byte[] fileType          = new byte[1];
    private byte[] index             = new byte[4];
    private byte[] hashCode          = new byte[32];
    private byte[] size              = new byte[32];
    private byte[] compressAlgorithm = new byte[1];
    private byte[] compressSize      = new byte[32];
    private byte[] del               = new byte[1];
    private byte[] lastMdf           = new byte[8];
    private byte[] createTime        = new byte[8];
    private byte[] footerUnKnown     = new byte[32];

    public ChunkProtocolFooter(byte[] fileName, byte[] fileType, byte[] index, byte[] hashCode, byte[] size, byte[] compressAlgorithm, byte[] compressSize, byte[] del, byte[] lastMdf, byte[] createTime, byte[] footerUnKnown) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.index = index;
        this.hashCode = hashCode;
        this.size = size;
        this.compressAlgorithm = compressAlgorithm;
        this.compressSize = compressSize;
        this.del = del;
        this.lastMdf = lastMdf;
        this.createTime = createTime;
        this.footerUnKnown = footerUnKnown;
    }

    public byte[] getFileName() {
        return fileName;
    }

    public void setFileName(byte[] fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileType() {
        return fileType;
    }

    public void setFileType(byte[] fileType) {
        this.fileType = fileType;
    }

    public byte[] getIndex() {
        return index;
    }

    public void setIndex(byte[] index) {
        this.index = index;
    }

    public byte[] getHashCode() {
        return hashCode;
    }

    public void setHashCode(byte[] hashCode) {
        this.hashCode = hashCode;
    }

    public byte[] getSize() {
        return size;
    }

    public void setSize(byte[] size) {
        this.size = size;
    }

    public byte[] getCompressAlgorithm() {
        return compressAlgorithm;
    }

    public void setCompressAlgorithm(byte[] compressAlgroithm) {
        this.compressAlgorithm = compressAlgroithm;
    }

    public byte[] getCompressSize() {
        return compressSize;
    }

    public void setCompressSize(byte[] compressSize) {
        this.compressSize = compressSize;
    }

    public byte[] getDel() {
        return del;
    }

    public void setDel(byte[] del) {
        this.del = del;
    }

    public byte[] getLastMdf() {
        return lastMdf;
    }

    public void setLastMdf(byte[] lastMdf) {
        this.lastMdf = lastMdf;
    }

    public byte[] getCreateTime() {
        return createTime;
    }

    public void setCreateTime(byte[] createTime) {
        this.createTime = createTime;
    }

    public byte[] getFooterUnKnown() {
        return footerUnKnown;
    }

    public void setFooterUnKnown(byte[] footerUnKnown) {
        this.footerUnKnown = footerUnKnown;
    }

    @Override
    public String toString() {
        return "ChunkProtocolFooter{" +
                "fileName=" + Arrays.toString(fileName) +
                ", fileType=" + Arrays.toString(fileType) +
                ", index=" + Arrays.toString(index) +
                ", hashCode=" + Arrays.toString(hashCode) +
                ", size=" + Arrays.toString(size) +
                ", compressAlgorithn=" + Arrays.toString(compressAlgorithm) +
                ", compressSize=" + Arrays.toString(compressSize) +
                ", del=" + Arrays.toString(del) +
                ", lastMdf=" + Arrays.toString(lastMdf) +
                ", createTime=" + Arrays.toString(createTime) +
                ", footerUnKnown=" + Arrays.toString(footerUnKnown) +
                '}';
    }
}
