package com.iot.nero.middleware.dfs.data.core;

import com.iot.nero.middleware.dfs.client.constant.RpcType;
import com.iot.nero.middleware.dfs.client.core.DFSClient;
import com.iot.nero.middleware.dfs.client.core.DFSErrorListener;
import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.entity.DataPosition;
import com.iot.nero.middleware.dfs.common.entity.FileData;
import com.iot.nero.middleware.dfs.common.entity.SectionPos;
import com.iot.nero.middleware.dfs.common.entity.response.Response;
import com.iot.nero.middleware.dfs.common.exceptions.FileAlreadyExistsInThisChunkException;
import com.iot.nero.middleware.dfs.common.exceptions.FileSizeExceededException;
import com.iot.nero.middleware.dfs.common.exceptions.ThisChunkIndexIsAlreadyFullException;
import com.iot.nero.middleware.dfs.common.factory.ConfigFactory;
import com.iot.nero.middleware.dfs.common.service.IIndexService;
import com.iot.nero.middleware.dfs.common.utils.ByteUtils;
import com.iot.nero.middleware.dfs.common.utils.MD5Utils;
import com.iot.nero.middleware.dfs.common.utils.algorithm.BPlusTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.iot.nero.middleware.dfs.data.constant.CONSTANT.FILE_SIZE_EXCEEDED;

/**
 * Author neroyang
 * Email  nerosoft@outlosok.com
 * Date   2018/6/27
 * Time   1:41 PM
 */
public class IndexManager {

    // 文件块管理器
    ChunkManager chunkManager;

    // 索引
    BPlusTree<String, BlockData> fileIndex;

    // 空闲段
    List<SectionPos> sectionPosList;

    // 客户端
    final DFSClient dfsClient = DFSClient.getInstance();
    IIndexService fileService;

    public IndexManager() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        initChunkManager();
        initIndexTree();
        initClient();
    }

    /**
     * 初始化客户端
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    private void initClient() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        dfsClient.init();
        // 错误事件监听
        dfsClient.setDfsErrorListener(new DFSErrorListener() {
            @Override
            public void onError(Response<Object> response) {
                // 错误处理
                switch (response.getCode()){
                    case 1: // 未知的请求类型
                        System.out.println(response.getMsg());
                        break;
                    case 2: // 节点认证失败
                        System.out.println(response.getMsg());
                        break;
                    // todo ....
                }
            }
        });
        fileService = dfsClient.getRemoteProxy(RpcType.INDEX, IIndexService.class);
    }

    /**
     * 初始化文件块管理器
     */
    private void initChunkManager() {
        chunkManager = new ChunkManager();
    }

    /**
     * 初始化全局索引
     *
     * @throws IOException
     */
    private void initIndexTree() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        fileIndex = new BPlusTree<>(3);

        // 获取文件块列表
        List<String> chunkList = chunkManager.getChunkList();

        // 遍历文件块列表
        for (String chunkName : chunkList) {

            // 得到文件块内文件列表
            List<BlockData> blockDataList = chunkManager.getBlockDataListFromChunk(chunkName);


            List<DataPosition> dataPositionList = new ArrayList<>();
            // 遍历文件内文件列表并插入全局索引树
            for (BlockData blockData : blockDataList) {
                fileIndex.insertOrUpdate(blockData.getFileHash(), blockData);
                dataPositionList.add(new DataPosition(blockData.getFileHash(), ConfigFactory.getConfig().getNodeName()));
            }

            // 或许可以用另一个线程
            // 向索引节点同步索引
            fileService.registerChunkIndex(dataPositionList);
        }
    }

    /**
     * 在全局索引中查找文件
     *
     * @param hash
     * @return
     */
    public BlockData findFileByHash(String hash) {
        return fileIndex.get(hash);
    }

    /**
     * 从文件块管理器中读取原始数据
     *
     * @param blockData
     * @return
     */
    public byte[] readFileSourceDataFromChunkManager(BlockData blockData) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return chunkManager.readFileFromChunk(blockData);
    }

    /**
     * 从文件块装中读取文件压缩数据
     *
     * @param blockData
     * @return
     */
    public byte[] readFileCompressedDataFromChunkManager(BlockData blockData) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return chunkManager.readFileCompressedFromChunk(blockData);
    }

    /**
     * 文件规划与写入
     * <p>
     * 空闲空间分配算法
     * <p>
     * 1.最佳适应法
     * 最佳适应算法要求空闲区按大小递增的次序排列.在进行空间分配时,从空闲分区表首开始顺序查找,直到找到第一个能满足其大小要求的空闲区为止,如果该空闲区大于请求表中的请求长度,则将剩余空闲区留在可用表中(如果相邻有空闲区,则与之和并),然后修改相关表的表项.按这种方式为作业分配空间,就能把既满足要求又与作业大小接近的空闲分区分配给作业.如果空闲区大于该作业的大小,则与首次适应算法相同,将剩余空闲区仍留在空闲分区表中.
     * 最佳适应算法的特点是:若存在与作业大小一致的空闲分区,则它必然被选中;若不存在与作业大小一致的空闲分区,则只划分比作业稍大的空闲分区,从而保留了大的空闲区.但空闲区一般不可能正好和作业申请的空间大小一样,因而将其分割成两部分时,往往使剩下的空闲区非常小,从而在存储器中留下许多难以利用的小空闲区(也被称为碎片).
     *
     * @param fileData
     * @return
     */
    public BlockData writeFileToChunk(FileData fileData) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException, FileSizeExceededException, FileAlreadyExistsInThisChunkException, ThisChunkIndexIsAlreadyFullException {

        if (fileData.getFileSize() > ChunkManager.CHUNK_DATA_SIZE) {
            throw new FileSizeExceededException(FILE_SIZE_EXCEEDED);
        }

        sectionPosList = chunkManager.getChunksFreeSections();  // 拿到空闲空间列表

        boolean isSectionFound = false;

        SectionPos needSectionPos = new SectionPos();
        for (SectionPos sectionPos : sectionPosList) {
            if (sectionPos.getSectionSize() < fileData.getFileSize()) {
                continue;
            }

            needSectionPos.setChunkName(sectionPos.getChunkName());
            needSectionPos.setSectionIndex(sectionPos.getSectionIndex());
            needSectionPos.setSectionSize(sectionPos.getSectionSize());

            sectionPosList.remove(sectionPos); // 从段列表删除这个即将要用的空闲段
            if (sectionPos.getSectionSize() > fileData.getFileSize()) {
                sectionPosList.add(
                        new SectionPos(
                                sectionPos.getSectionIndex() + fileData.getFileSize(),
                                sectionPos.getSectionSize() - fileData.getFileSize(),
                                sectionPos.getChunkName()
                        )
                ); // 产生新的空闲段
            }

            isSectionFound = true;
            break;
        }

        if (isSectionFound) {

            BlockData prevBlockData = chunkManager.getLastBlockDataFromChunk(needSectionPos.getChunkName());

            if(prevBlockData==null){
                byte[] header = chunkManager.getChunkHeader(needSectionPos.getChunkName());
                byte[] fileChainHash = generateChunkChainHash(header); // 文件块链哈希
                BlockData blockData = new BlockData(
                        needSectionPos.getChunkName(),
                        fileData.getFileName(),
                        fileData.getFileType(),
                        needSectionPos.getSectionIndex(),
                        fileData.getFileHash(),
                        fileData.getFileSize(),
                        fileData.getCompressMethod(),
                        fileData.getCompressedFileSize(),
                        ByteUtils.bytesToString(fileChainHash),
                        true,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                );

                chunkManager.writeFileToChunk(needSectionPos.getChunkName(), ByteUtils.toPrimitives((Byte[]) fileData.getData().toArray()), blockData);

                return blockData;

            }
            // 安排妥了可以写入的文件块，直接写入
            String fileChainHash = generateFileChainHash(
                    prevBlockData
            );
            BlockData blockData = new BlockData(
                    needSectionPos.getChunkName(),
                    fileData.getFileName(),
                    fileData.getFileType(),
                    needSectionPos.getSectionIndex(),
                    fileData.getFileHash(),
                    fileData.getFileSize(),
                    fileData.getCompressMethod(),
                    fileData.getCompressedFileSize(),
                    fileChainHash,
                    true,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
            );

            chunkManager.writeFileToChunk(needSectionPos.getChunkName(), ByteUtils.toPrimitives((Byte[]) fileData.getData().toArray()), blockData);

            return blockData;

        } else { // 没有可以写入的文件块，需要创建新的文件块，在新的文件块内写入
            String chunkName = UUID.randomUUID().toString().replace("-", "");

            // 获取上一个文件块的文件头
            String lastChunkName = chunkManager.getLastChunkName();
            if (lastChunkName == null) {
                byte[] header = new byte[128];
                for (int i = 0; i < ChunkManager.CHUNK_HEADER_SIZE; i++) {
                    header[i] = (byte) 0xBB;
                }
                byte[] chunkChainHash = generateChunkChainHash(header); // 文件块链哈希
                chunkManager.createChunk(chunkName, chunkChainHash); //32 位置为链hash

                String fileChainHash = generateInitialFileChainHash(header, chunkChainHash);

                BlockData blockData = new BlockData(
                        needSectionPos.getChunkName(),
                        fileData.getFileName(),
                        fileData.getFileType(),
                        0,
                        fileData.getFileHash(),
                        fileData.getFileSize(),
                        fileData.getCompressMethod(),
                        fileData.getCompressedFileSize(),
                        fileChainHash,
                        true,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                );

                chunkManager.writeFileToChunk(chunkName, ByteUtils.toPrimitives((Byte[]) fileData.getData().toArray()), blockData);

                return blockData;
            }

            byte[] header = chunkManager.getChunkHeader(lastChunkName);
            byte[] chunkChainHash = generateChunkChainHash(header); // 文件块链哈希
            chunkManager.createChunk(chunkName, chunkChainHash); //32 位置为链hash

            String fileChainHash = generateInitialFileChainHash(header, chunkChainHash);

            BlockData blockData = new BlockData(
                    needSectionPos.getChunkName(),
                    fileData.getFileName(),
                    fileData.getFileType(),
                    0,
                    fileData.getFileHash(),
                    fileData.getFileSize(),
                    fileData.getCompressMethod(),
                    fileData.getCompressedFileSize(),
                    fileChainHash,
                    true,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
            );

            chunkManager.writeFileToChunk(chunkName, ByteUtils.toPrimitives((Byte[]) fileData.getData().toArray()), blockData);

            return blockData;
        }
    }

    /**
     * 生成文件链哈希
     *
     * @param prevBlockData
     * @return
     */
    private String generateFileChainHash(BlockData prevBlockData) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {

        // 获取上一个文件的链哈希
        byte[] prevFileData = chunkManager.readFileCompressedFromChunk(prevBlockData);

        // 上一个的文件+链哈希+时间戳
        byte[] allData = new byte[prevBlockData.getCompressionSize() + prevBlockData.getChainHash().length() + 8];

        for (int i = 0; i < prevFileData.length; i++) {
            allData[i] = prevFileData[i];
        }

        for (int i = prevFileData.length; i < prevFileData.length + prevBlockData.getChainHash().length(); i++) {
            allData[i] = (byte) prevBlockData.getChainHash().charAt(i - prevFileData.length);
        }

        for (int i = prevFileData.length; i < prevFileData.length + prevBlockData.getChainHash().length(); i++) {
            allData[i] = (byte) prevBlockData.getChainHash().charAt(i - prevFileData.length);
        }

        Long currentTimeStamp = System.currentTimeMillis();
        byte[] currentTimeStampBytes = ByteUtils.toByteArray(currentTimeStamp);
        for (int i = prevFileData.length + prevBlockData.getChainHash().length(); i < prevFileData.length + prevBlockData.getChainHash().length() + 8; i++) {
            allData[i] = currentTimeStampBytes[i - prevFileData.length + prevBlockData.getChainHash().length()];
        }

        return MD5Utils.toMD5String(allData);
    }

    /**
     * 生成文件块链哈希
     *
     * @param header
     * @return
     */
    private byte[] generateChunkChainHash(byte[] header) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        // 上一个块头+时间戳
        byte[] allData = new byte[header.length + 8];

        for (int i = 0; i < header.length; i++) {
            allData[i] = header[1];
        }

        for (int i = header.length; i < header.length + 8; i++) {
            allData[i] = header[i - header.length];
        }

        return MD5Utils.toMD5Bytes(allData);
    }

    /**
     * 生成块初始文件链哈希
     *
     * @param header
     * @param headerChainHash
     * @return
     */
    private String generateInitialFileChainHash(byte[] header, byte[] headerChainHash) {
        // 上一个块头+时间戳
        byte[] allData = new byte[header.length + headerChainHash.length + 8];

        for (int i = 0; i < header.length; i++) {
            allData[i] = header[1];
        }

        for (int i = header.length; i < header.length + headerChainHash.length; i++) {
            allData[i] = header[i - header.length];
        }

        for (int i = header.length + headerChainHash.length; i < header.length + headerChainHash.length + 8; i++) {
            allData[i] = header[i - header.length - headerChainHash.length];
        }

        return MD5Utils.toMD5String(allData);
    }

    /**
     * 文件删除
     * @param blockData
     * @return
     */
    public BlockData delFile(BlockData blockData) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        chunkManager.setDelFlagForFooterBlockData(blockData,true);

        return blockData;
    }

    /**
     * 文件正式事务提交
     * @param blockData
     * @return
     */
    public BlockData commitWriteFileToChunk(BlockData blockData) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        chunkManager.setDelFlagForFooterBlockData(blockData,false);

        return blockData;
    }
}
