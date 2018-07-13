package com.iot.nero.middleware.dfs.data.core;

import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.entity.SectionPos;
import com.iot.nero.middleware.dfs.common.exceptions.FileAlreadyExistsInThisChunkException;
import com.iot.nero.middleware.dfs.common.exceptions.ThisChunkIndexIsAlreadyFullException;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.common.utils.ByteUtils;
import com.iot.nero.middleware.dfs.common.utils.ProtoStuffUtils;
import org.xerial.snappy.Snappy;
import sun.misc.Cleaner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.CHUNK_INDEX_IS_FULL;
import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.FILE_ALREADY_EXISTS_IN_THIS_CHUNK;
import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.NOT_FIND_FILE_FROM_CURRENT_CHUNK;
import static com.iot.nero.middleware.dfs.common.utils.ByteUtils.bytesToInt;
import static com.iot.nero.middleware.dfs.common.utils.ByteUtils.bytesToString;
import static com.iot.nero.middleware.dfs.common.utils.ByteUtils.toByteArray;


/**
 * 关于 ndfs 文件块的操作
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   5:09 PM
 */
public class ChunkManager {

    static final int VERSION = 0;

    static final int CHUNK_HEADER_SIZE = (2 << 6);          // 128 Byte
    static final int CHUNK_DATA_SIZE = (2 << 25);           // 64  MByte
    static final int CHUNK_FOOTER_SIZE = (2 << 19);         // 1   MByte
    static final byte CHUNK_FOOTER_ITEM_SEPARATOR = (byte) 0xFF;
    static final int CHUNK_SIZE = CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE + CHUNK_FOOTER_SIZE;

    /**
     * 创建文件块
     *
     * @param chunkName
     */
    public synchronized void createChunk(String chunkName, byte[] others) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        try (RandomAccessFile newFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkFilePath() + chunkName + ".ndf", "rw")) {
            newFile.setLength(CHUNK_SIZE);

            String uuid = UUID.randomUUID().toString().replace("-","");

            // chunk 文件头
            byte[] header = new byte[CHUNK_HEADER_SIZE];

            // 版本
            byte[] version = toByteArray(VERSION);
            for (int i = 0; i < 4; i++) {
                header[i] = version[i];
            }

            // UUID
            byte[] uuidBytes = new byte[32];
            for (int i = 0; i < 32; i++) {
                uuidBytes[i] = (byte) uuid.charAt(i);
            }

            for (int i = 32; i < 64; i++) {
                header[i] = uuidBytes[i - 32];
            }

            // 文件数
            byte[] fileCount = toByteArray(0);
            for (int i = 64; i < 68; i++) {
                header[i] = fileCount[i - 64];
            }

            // 预留
            for (int i = 68; i < CHUNK_HEADER_SIZE; i++) {
                header[i] = others[i - 68];
            }

            newFile.write(header);

            // 文件块记录文件
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkListFilePath(), "rw")) {
                FileChannel fileChannel = randomAccessFile.getChannel();
                fileChannel.write(ByteBuffer.wrap(chunkName.getBytes()), fileChannel.size());
                fileChannel.write(ByteBuffer.wrap(new byte[]{CHUNK_FOOTER_ITEM_SEPARATOR}), fileChannel.size() + 1);
            }

        }

    }

    /**
     * 获取 chunk 文件列表
     *
     * @return
     * @throws IOException
     */
    public List<String> getChunkList() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkListFilePath(), "rw")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            List<byte[]> bytesList = ByteUtils.split(byteBuffer.array(), CHUNK_FOOTER_ITEM_SEPARATOR);
            List<String> stringList = new ArrayList<>();


            for (byte[] bytes : bytesList) {
                stringList.add(bytesToString(bytes));
            }
            return stringList;
        }
    }

    /**
     * 获取文件块内的空闲段
     *
     * @param chunkName
     * @return
     */
    public List<SectionPos> getChunkFreeSections(String chunkName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {

        // 找到块中文件列表并排序
        List<BlockData> blockDataList = getBlockDataListFromChunk(chunkName);
        Collections.sort(blockDataList);

        for (BlockData blockData : blockDataList) {
            if (blockData.getDel()) {
                blockDataList.remove(blockData);
            }
        }

        List<SectionPos> sectionPosList = new ArrayList<>();
        for (int i = 1; i < blockDataList.size(); i++) {
            BlockData prevBlockData = blockDataList.get(i - 1);
            BlockData lastBlockData = blockDataList.get(blockDataList.size()-1);
            if (prevBlockData.getIndex() != 0) { // 前面还有空闲空间
                SectionPos sectionPos = new SectionPos(
                        0,
                        prevBlockData.getIndex(),
                        chunkName
                );
                sectionPosList.add(sectionPos);
            } else { // 数据与数据之间的空闲空间
                SectionPos sectionPos = new SectionPos(
                        prevBlockData.getIndex() + prevBlockData.getCompressionSize(),
                        blockDataList.get(i).getIndex() - (prevBlockData.getIndex() + prevBlockData.getCompressionSize()),
                        chunkName
                );
                sectionPosList.add(sectionPos);
            }

            // 最后的空闲空间
            SectionPos sectionPos = new SectionPos(
                    lastBlockData.getIndex()+lastBlockData.getCompressionSize(),
                    CHUNK_DATA_SIZE-(lastBlockData.getIndex()+lastBlockData.getCompressionSize()),
                    chunkName
            );
            sectionPosList.add(sectionPos);
        }

        Collections.sort(sectionPosList);
        return sectionPosList;
    }

    /**
     * 获取所有文件块内的空闲段
     *
     * @return
     */
    public List<SectionPos> getChunksFreeSections() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        List<SectionPos> sectionPosList = new ArrayList<>();
        List<String> chunkList = getChunkList();

        for(String chunkName:chunkList){
            sectionPosList.addAll(getChunkFreeSections(chunkName));
        }
        Collections.sort(sectionPosList);
        return sectionPosList;
    }

    /**
     * 文件块 footer 整理
     *
     * @param footer
     */
    private byte[] consolidationFooter(byte[] footer) {
        // TODO footer 整理
        return null;
    }

    /**
     * 空间碎片整理
     * @param chunkName
     */
    public void dataDefragmentation(String chunkName){
        // TODO 空间碎片整理
    }

    /**
     * 将文件写入文件块
     *
     * @param chunkName
     * @param compressedFileData
     * @param blockData
     */
    public synchronized void writeFileToChunk(String chunkName, byte[] compressedFileData, BlockData blockData) throws IOException, ThisChunkIndexIsAlreadyFullException, FileAlreadyExistsInThisChunkException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        // FBI Warning: A mapped byte buffer and the file mapping that it represents remain valid until the buffer itself  is garbage-collected.
        final MappedByteBuffer mappedByteBuffer;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkFilePath() + chunkName + ".ndf", "rw")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());

            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);


            // 获取该文件块中文件个数
            byte[] fileCountBytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                fileCountBytes[i] = byteBuffer.get(64 + i);
            }
            int fileCount = bytesToInt(fileCountBytes, 0);

            // 检查一下文件有没有
            byte[] allFooter = getFooterFromChunkBytes(byteBuffer.array());
            List<BlockData> blockDataList = getBlockDataListFromChunkFooter(allFooter, fileCount);
            BlockData foundBlockData = findBlockDataFromList(blockDataList, blockData.getFileHash());

            if (foundBlockData != null) {
                throw new FileAlreadyExistsInThisChunkException(FILE_ALREADY_EXISTS_IN_THIS_CHUNK);
            }


            // 写入文件 header 文件数量
            byte[] newFileCountBytes = toByteArray(fileCount + 1);
            for (int i = 0; i < 4; i++) {
                mappedByteBuffer.put(64 + i, newFileCountBytes[i]);
            }

            // 写入文件
            for (int i = 0; i < compressedFileData.length; i++) {
                mappedByteBuffer.put(CHUNK_HEADER_SIZE + blockData.getIndex() + i, compressedFileData[i]);
            }


            byte[] footer;
            byte[] data;
            footer = ProtoStuffUtils.serializer(blockData);
            data = Snappy.compress(footer);

            /**
             *  写入文件列表
             *
             *  首先判断该文件删除标志位，如果该文件已经删除
             *  则将该条文件索引删除
             *  然后将后面索引的向前移动
             *  最后在最后插入最新的文件索引
             */


            // TODO  暂时简单的跟在footer后面吧
            int lastIndex = getLastByteIndex(allFooter, CHUNK_FOOTER_ITEM_SEPARATOR, fileCount + 1);
            if (lastIndex + data.length > CHUNK_FOOTER_SIZE) {
                throw new ThisChunkIndexIsAlreadyFullException(CHUNK_INDEX_IS_FULL);
            }

            int footerIndex;
            for (footerIndex = 0; footerIndex < data.length; footerIndex++) {
                mappedByteBuffer.put(CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE + lastIndex + footerIndex, data[footerIndex]);
            }

            mappedByteBuffer.put(CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE + lastIndex + footerIndex, CHUNK_FOOTER_ITEM_SEPARATOR);

            mappedByteBuffer.flip();
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        Cleaner cleaner = (Cleaner)
                                getCleanerMethod.invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clean();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }

    /**
     * 写入 文件footer
     * @param index
     * @param blockData
     */
    private void writeFileToChunk(Integer index, BlockData blockData) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        // FBI Warning: A mapped byte buffer and the file mapping that it represents remain valid until the buffer itself  is garbage-collected.
        final MappedByteBuffer mappedByteBuffer;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkFilePath() + blockData.getChunkName() + ".ndf", "rw")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());

            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);


            int footerIndex;
            byte[] data = Snappy.compress(ProtoStuffUtils.serializer(blockData));
            for (footerIndex = 0; footerIndex < data.length; footerIndex++) {
                mappedByteBuffer.put(CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE + footerIndex, data[footerIndex]);
            }
            mappedByteBuffer.flip();
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        Cleaner cleaner = (Cleaner)
                                getCleanerMethod.invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clean();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
    }

    /**
     * 查找
     *
     * @param blockDataList
     * @param fileHash
     * @return
     */
    private BlockData findBlockDataFromList(List<BlockData> blockDataList, String fileHash) {
        BlockData foundBlockData = null;
        for (BlockData blockData : blockDataList) {
            if (blockData.getFileHash().equals(fileHash)) {
                foundBlockData = blockData;
            }
        }
        return foundBlockData;
    }


    /**
     * 通过文件的 hashcode 从文件块中读取 文件字节序列
     *
     * @param chunkPath
     * @param fileHash
     * @return
     */
    public byte[] readFileFromChunk(String chunkPath, String fileHash) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ByteBuffer byteBuffer = readChunkToBytes(chunkPath);

        byte[] footer = new byte[CHUNK_FOOTER_SIZE];
        int footerIndex = CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE;
        byte b;
        while ((b = byteBuffer.get(footerIndex)) != CHUNK_FOOTER_ITEM_SEPARATOR) {
            footer[footerIndex - (CHUNK_DATA_SIZE + CHUNK_HEADER_SIZE)] = b;
            footerIndex++;
        }

        List<BlockData> blockDataList = getBlockDataFromChunk(byteBuffer.array());
        BlockData foundBlockData = findBlockDataFromList(blockDataList, fileHash);

        if (foundBlockData == null) {
            throw new FileNotFoundException(NOT_FIND_FILE_FROM_CURRENT_CHUNK);
        }

        byte[] fileBytesCompressed = new byte[foundBlockData.getCompressionSize()];
        for (int i = 0; i < foundBlockData.getCompressionSize(); i++) {
            fileBytesCompressed[i] = byteBuffer.get(CHUNK_HEADER_SIZE + foundBlockData.getIndex() + i);
        }

        if (foundBlockData.getCompressionAlgorithm().equals("snappy")) {
            return Snappy.uncompress(fileBytesCompressed);
        } else {
            return fileBytesCompressed;
        }
    }


    /**
     * 从文件块读取原始数据
     *
     * @param blockData
     * @return
     * @throws IOException
     */
    public byte[] readFileFromChunk(BlockData blockData) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        byte[] fileBytesCompressed = readFileCompressedFromChunk(blockData);

        if (blockData.getCompressionAlgorithm().equals("snappy")) {
            return Snappy.uncompress(fileBytesCompressed);
        } else {
            return fileBytesCompressed;
        }
    }

    /**
     * 从文件块读取压缩过的数据
     *
     * @param blockData
     * @return
     * @throws IOException
     */
    public byte[] readFileCompressedFromChunk(BlockData blockData) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ByteBuffer byteBuffer = readChunkToBytes(blockData.getChunkName());

        byte[] footer = new byte[CHUNK_FOOTER_SIZE];
        int footerIndex = CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE;
        byte b;
        while ((b = byteBuffer.get(footerIndex)) != CHUNK_FOOTER_ITEM_SEPARATOR) {
            footer[footerIndex - (CHUNK_DATA_SIZE + CHUNK_HEADER_SIZE)] = b;
            footerIndex++;
        }

        List<BlockData> blockDataList = getBlockDataFromChunk(byteBuffer.array());
        BlockData foundBlockData = findBlockDataFromList(blockDataList, blockData.getFileHash());

        if (foundBlockData == null) {
            throw new FileNotFoundException(NOT_FIND_FILE_FROM_CURRENT_CHUNK);
        }

        byte[] fileBytesCompressed = new byte[foundBlockData.getCompressionSize()];
        for (int i = 0; i < foundBlockData.getCompressionSize(); i++) {
            fileBytesCompressed[i] = byteBuffer.get(CHUNK_HEADER_SIZE + foundBlockData.getIndex() + i);
        }

        return fileBytesCompressed;
    }

    /**
     * 从文件块中读取文件列表
     *
     * @param chunkBytes
     * @return
     * @throws IOException
     */
    private List<BlockData> getBlockDataFromChunk(byte[] chunkBytes) throws IOException {

        byte[] headerBytes = getHeaderFromChunkBytes(chunkBytes);
        byte[] footerBytes = getFooterFromChunkBytes(chunkBytes);
        int fileCount = getFileCountFromChunkHeaderBytes(headerBytes);

        return getBlockDataListFromChunkFooter(footerBytes, fileCount);
    }

    /**
     * 从文件块中读取文件Map
     *
     * @param chunkBytes
     * @return
     * @throws IOException
     */
    private Map<BlockData,Integer> getBlockDataMapFromChunk(byte[] chunkBytes) throws IOException {

        byte[] headerBytes = getHeaderFromChunkBytes(chunkBytes);
        byte[] footerBytes = getFooterFromChunkBytes(chunkBytes);
        int fileCount = getFileCountFromChunkHeaderBytes(headerBytes);

        return getBlockDataMapFromChunkFooter(footerBytes, fileCount);
    }


    /**
     * 从文件头获取文件个数
     *
     * @param headerBytes
     * @return
     */
    private int getFileCountFromChunkHeaderBytes(byte[] headerBytes) {
        // header 包含文件个数
        byte[] fileCountBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            fileCountBytes[i] = headerBytes[64 + i];
        }
        return bytesToInt(fileCountBytes, 0);
    }


    /**
     * 获取当前chunk文件内的header
     *
     * @param chunkBytes
     * @return
     */
    private byte[] getHeaderFromChunkBytes(byte[] chunkBytes) {
        // 获取当前 Chunk 的 header
        byte[] headerBytes = new byte[CHUNK_HEADER_SIZE];
        for (int i = 0; i < CHUNK_HEADER_SIZE; i++) {
            headerBytes[i] = chunkBytes[i];
        }
        return headerBytes;
    }

    /**
     * 获取该chunk 的 footer
     *
     * @param chunkBytes
     * @return
     */
    private byte[] getFooterFromChunkBytes(byte[] chunkBytes) {
        // 获取当前 Chunk 的 footer
        byte[] footerBytes = new byte[CHUNK_FOOTER_SIZE];
        for (int i = 0; i < CHUNK_FOOTER_SIZE; i++) {
            footerBytes[i] = chunkBytes[CHUNK_HEADER_SIZE + CHUNK_DATA_SIZE + i];
        }
        return footerBytes;
    }

    /**
     * 将 byte 数组按照某个 byte 切割
     *
     * @param src
     * @param spin
     * @param count
     * @return
     */
    private List<byte[]> split(byte[] src, byte spin, int count) {
        List<byte[]> bytes = new ArrayList<>();
        int lastIndex = 0;  // 上一次匹配spin位置
        int num = 0;        // 分隔得到个数

        for (int i = 0; i < src.length; i++) {
            if (src[i] == spin && num <= count) {
                byte[] dataTmp;
                if (lastIndex == 0) {
                    dataTmp = new byte[i - lastIndex];
                    for (int index = lastIndex; index < i; index++) {
                        dataTmp[index - lastIndex] = src[index];
                    }
                } else {
                    dataTmp = new byte[i - lastIndex - 1];
                    for (int index = lastIndex; index < i - 1; index++) {
                        dataTmp[index - lastIndex] = src[index + 1];
                    }
                }

                bytes.add(dataTmp);
                lastIndex = i;
                num += 1;
            }
        }
        return bytes;
    }

    /**
     * 切个字符串 并找出位置
     * @param src
     * @param spin
     * @param count
     * @return
     */
    private Map<Integer,byte[]> splitToMap(byte[] src, byte spin, int count) {
        Map<Integer,byte[]> bytes = new HashMap<>();
        int lastIndex = 0;  // 上一次匹配spin位置
        int num = 0;        // 分隔得到个数

        for (int i = 0; i < src.length; i++) {
            if (src[i] == spin && num <= count) {
                byte[] dataTmp;

                if (lastIndex == 0) {
                    dataTmp = new byte[i - lastIndex];
                    for (int index = lastIndex; index < i; index++) {
                        dataTmp[index - lastIndex] = src[index];
                    }
                } else {
                    dataTmp = new byte[i - lastIndex - 1];
                    for (int index = lastIndex; index < i - 1; index++) {
                        dataTmp[index - lastIndex] = src[index + 1];
                    }
                }

                bytes.put(lastIndex,dataTmp);
                lastIndex = i;
                num += 1;
            }
        }
        return bytes;
    }

    /**
     * 获取该chunk内最后一个字节的位置
     *
     * @param src
     * @param spin
     * @param count
     * @return
     */
    private Integer getLastByteIndex(byte[] src, byte spin, int count) {
        int num = 0;        // 分隔得到个数
        for (int i = 0; i < src.length; i++) {
            if (src[i] == spin) {
                num += 1;
                if (num == count - 1) {
                    return i + 1;
                }
            }
        }
        return 0;
    }

    /**
     * 从文件块 footer 中读取文件列表
     *
     * @param footer
     * @param fileCount
     * @return
     * @throws IOException
     */
    private List<BlockData> getBlockDataListFromChunkFooter(byte[] footer, int fileCount) throws IOException {

        List<byte[]> bytes = split(footer, CHUNK_FOOTER_ITEM_SEPARATOR, fileCount);
        List<BlockData> blockDataList = new ArrayList<>();

        for (byte[] itemBytes : bytes) {
            BlockData blockData = ProtoStuffUtils.deserializer(Snappy.uncompress(itemBytes), BlockData.class);
            blockDataList.add(blockData);
        }

        return blockDataList;
    }



    /**
     * 将footer分隔到map
     * @param footer
     * @param fileCount
     * @return
     * @throws IOException
     */
    private Map<BlockData,Integer> getBlockDataMapFromChunkFooter(byte[] footer, int fileCount) throws IOException {

        Map<Integer,byte[]> bytes = splitToMap(footer, CHUNK_FOOTER_ITEM_SEPARATOR, fileCount);
        Map<BlockData,Integer> blockDataMap = new HashMap<>();
        for(Integer index:bytes.keySet()){
            BlockData blockData = ProtoStuffUtils.deserializer(Snappy.uncompress(bytes.get(index)), BlockData.class);
            blockDataMap.put(blockData,index);
        }
        return blockDataMap;
    }

    /**
     * 获取 chunk 的文件列表
     *
     * @param chunkName
     * @return
     * @throws IOException
     */
    public List<BlockData> getBlockDataListFromChunk(String chunkName) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        ByteBuffer chunkBytes = readChunkToBytes(chunkName);

        return getBlockDataFromChunk(chunkBytes.array());
    }

    /**
     * 将 chunk 读取到字节流
     *
     * @param chunkName
     * @return
     */
    private ByteBuffer readChunkToBytes(String chunkName) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(ConfigFactory.getConfig().getChunkFilePath() + chunkName + ".ndf", "r")) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            return byteBuffer;
        }
    }


    /**
     * 读取 chunk header
     *
     * @param chunkName
     * @return
     */
    public byte[] getChunkHeader(String chunkName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        return getHeaderFromChunkBytes(readChunkToBytes(chunkName).array());
    }


    /**
     * 读取 chunk footer
     *
     * @param chunkName
     * @return
     */
    public byte[] getChunkFooter(String chunkName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        return getFooterFromChunkBytes(readChunkToBytes(chunkName).array());
    }

    /**
     * 获取最后一个文件
     * @param chunkName
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public BlockData getLastBlockDataFromChunk(String chunkName) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        List<BlockData> blockDataList = getBlockDataListFromChunk(chunkName);
        if(blockDataList.isEmpty()){
            return null;
        }
        return blockDataList.get(blockDataList.size()-1);
    }

    /**
     * 获取最后一个chunk文件
     *
     * @return
     */
    public String getLastChunkName() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        List<String> lastChunkName = getChunkList();
        if(lastChunkName.isEmpty()){
            return null;
        }
        return lastChunkName.get(lastChunkName.size()-1);
    }

    /**
     * 找到blockData所在的索引
     * @param blockData
     * @return
     */
    public Integer getFooterBlockDataIndex(BlockData blockData) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        Map<BlockData,Integer> blockDataIntegerMap = getBlockDataMapFromChunk(readFileCompressedFromChunk(blockData));
        return blockDataIntegerMap.get(blockData);
    }

    /**
     * 修改文件 删除 状态
     * @param blockData
     * @param del
     */
    public void setDelFlagForFooterBlockData(BlockData blockData,Boolean del) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        // 查找索引
        Integer index = getFooterBlockDataIndex(blockData);

        blockData.setDel(del);

        writeChunkFooter(index,blockData);
    }

    /**
     * 修改文件 JIO
     * @param index
     * @param blockData
     */
    private void writeChunkFooter(Integer index, BlockData blockData) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        writeFileToChunk(index,blockData);
    }


}