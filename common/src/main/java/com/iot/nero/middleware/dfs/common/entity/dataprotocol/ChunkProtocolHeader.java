package com.iot.nero.middleware.dfs.common.entity.dataprotocol;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/22
 * Time   9:10 AM
 */

/**
 * | 版本(4) | UUID(32) | 文件数(4) | 预留,后期扩展(64) |
 */
public class ChunkProtocolHeader implements Serializable{

    private byte[] version           = new byte[32];
    private byte[] uuid              = new byte[32];
    private byte[] fileCount         = new byte[4];
    private byte[] headerUnknown     = new byte[60];

    public ChunkProtocolHeader(byte[] version, byte[] uuid, byte[] fileCount, byte[] headerUnknown) {
        this.version = version;
        this.uuid = uuid;
        this.fileCount = fileCount;
        this.headerUnknown = headerUnknown;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public void setUuid(byte[] uuid) {
        this.uuid = uuid;
    }

    public byte[] getFileCount() {
        return fileCount;
    }

    public void setFileCount(byte[] fileCount) {
        this.fileCount = fileCount;
    }

    public byte[] getHeaderUnknown() {
        return headerUnknown;
    }

    public void setHeaderUnknown(byte[] headerUnknown) {
        this.headerUnknown = headerUnknown;
    }

    @Override
    public String toString() {
        return "ChunkProtocolHeader{" +
                "version=" + Arrays.toString(version) +
                ", uuid=" + Arrays.toString(uuid) +
                ", fileCount=" + Arrays.toString(fileCount) +
                ", headerUnknown=" + Arrays.toString(headerUnknown) +
                '}';
    }
}
