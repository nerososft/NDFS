package com.iot.nero.middleware.dfs.common.service;

import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.common.entity.FileData;
import com.iot.nero.middleware.dfs.common.exceptions.FileAlreadyExistsInThisChunkException;
import com.iot.nero.middleware.dfs.common.exceptions.FileSizeExceededException;
import com.iot.nero.middleware.dfs.common.exceptions.ThisChunkIndexIsAlreadyFullException;

import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:28
 */
public interface IFileService {

    /**
     * 查找文件是否存在
     * @param hashCode
     * @return
     */
    BlockData findFileByHash(String hashCode);

    /**
     * 获取文件
     * @param hashCode
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     */
    FileData getDataBytes(String hashCode) throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException;


    /**
     * 删除文件
     * @param hash
     * @return
     */
    BlockData delFileByHash(String hash) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException;

    /**
     * 上传文件
     * @param fileData
     * @return
     */
    BlockData uploadFile(FileData fileData) throws NoSuchMethodException, FileSizeExceededException, InstantiationException, FileAlreadyExistsInThisChunkException, IllegalAccessException, ThisChunkIndexIsAlreadyFullException, IOException;

    /**
     * 上传提交
     * @param blockData
     * @return
     */
    BlockData uploadCommit(BlockData blockData) throws NoSuchMethodException, InstantiationException, IOException, IllegalAccessException;

}
