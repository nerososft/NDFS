package com.iot.nero.middleware.dfs.common.entity.dataprotocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/14
 * Time   8:48 AM
 */
public class ChunkProtocol implements Serializable {

    /**
     * ChunkHeader
     *
     * | 版本(4) | UUID(32) | 文件数(4) | 预留,后期扩展(64) |
     *
     * ChunkBody 64MBytes 2^10^10^6 Byte
     *
     * |数据|
     *
     * ChunkFooter 32+1+4+32+32+2+32+1+16+32 = 184 Byte * 文件数
     *
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
     * 最后一位做CRC校验
     *
     * 假设每个Chunk储存100个文件,则Footer大小为136Byte*100 = 1.53KByte
     * 若一个节点500G储存空间,大致可以储存 500*1024/64 = 500*16 = 9000 个Chunk
     * 9000个Chunk 的 Footer总大小为 9000*1.53KByte = 12.24MBytes
     *
     * 单节点500G储存,启动同步12M文件索引压力不大.
     */


    private ChunkProtocolHeader chunkProtocolHeader;
    private byte[] data;
    private List<ChunkProtocolFooter> chunkProtocolFooters;

    public ChunkProtocol() {
    }

    public ChunkProtocol(ChunkProtocolHeader chunkProtocolHeader, byte[] data, List<ChunkProtocolFooter> chunkProtocolFooters) {
        this.chunkProtocolHeader = chunkProtocolHeader;
        this.data = data;
        this.chunkProtocolFooters = chunkProtocolFooters;
    }

    public ChunkProtocolHeader getChunkProtocolHeader() {
        return chunkProtocolHeader;
    }

    public void setChunkProtocolHeader(ChunkProtocolHeader chunkProtocolHeader) {
        this.chunkProtocolHeader = chunkProtocolHeader;
    }

    public List<ChunkProtocolFooter> getChunkProtocolFooters() {
        return chunkProtocolFooters;
    }

    public void setChunkProtocolFooters(List<ChunkProtocolFooter> chunkProtocolFooters) {
        this.chunkProtocolFooters = chunkProtocolFooters;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChunkProtocol{" +
                "chunkProtocolHeader=" + chunkProtocolHeader +
                ", data=" + Arrays.toString(data) +
                ", chunkProtocolFooters=" + chunkProtocolFooters +
                '}';
    }
}
