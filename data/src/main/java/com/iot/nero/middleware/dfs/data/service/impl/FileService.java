package com.iot.nero.middleware.dfs.data.service.impl;

import com.iot.nero.middleware.dfs.common.annotation.Inject;
import com.iot.nero.middleware.dfs.common.annotation.Service;
import com.iot.nero.middleware.dfs.data.core.IndexManager;
import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.entity.FileData;
import com.iot.nero.middleware.dfs.common.exceptions.FileAlreadyExistsInThisChunkException;
import com.iot.nero.middleware.dfs.common.exceptions.FileSizeExceededException;
import com.iot.nero.middleware.dfs.common.exceptions.ThisChunkIndexIsAlreadyFullException;
import com.iot.nero.middleware.dfs.common.service.IFileService;
import com.iot.nero.middleware.dfs.common.utils.ByteUtils;

import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:28
 */

@Service
public class FileService implements IFileService {

    @Inject
    IndexManager indexManager;

    @Override
    public BlockData findFileByHash(String hashCode) {
        return  indexManager.findFileByHash(hashCode);
    }

    @Override
    public FileData getDataBytes(String hashCode) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        BlockData blockData = indexManager.findFileByHash(hashCode);
        blockData.setChunkName(blockData.getChunkName());

        return new FileData(
                blockData.getFileName(),
                blockData.getFileType(),
                blockData.getFileHash(),
                blockData.getChainHash(),
                blockData.getFileSize(),
                blockData.getCompressionSize(),
                blockData.getCompressionAlgorithm(),
                ByteUtils.toByteList(indexManager.readFileCompressedDataFromChunkManager(blockData))
        );
    }

    @Override
    public BlockData delFileByHash(String hash) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        BlockData blockData = indexManager.findFileByHash(hash);
        if(blockData!=null){
            return indexManager.delFile(blockData);
        }
        return blockData;
    }

    @Override
    public BlockData uploadFile(FileData fileData) throws NoSuchMethodException, FileSizeExceededException, InstantiationException, FileAlreadyExistsInThisChunkException, IllegalAccessException, ThisChunkIndexIsAlreadyFullException, IOException {
        return indexManager.writeFileToChunk(fileData);
    }

    @Override
    public BlockData uploadCommit(BlockData blockData) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException {
        return indexManager.commitWriteFileToChunk(blockData);
    }
}
