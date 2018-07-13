package com.iot.nero.middleware.dfs.index.core;

import com.iot.nero.middleware.dfs.common.entity.DataPosition;
import com.iot.nero.middleware.dfs.common.utils.algorithm.BPlusTree;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/19
 * Time   10:00 AM
 */

public class FileIndexManager {

    BPlusTree<String,DataPosition> fileIndex;

    private void initFileIndex(){

        // 创建索引 B+T
        this.fileIndex = new BPlusTree<>(3);
    }

    public FileIndexManager(BPlusTree<String, DataPosition> fileIndex) {
        initFileIndex();
    }

    public DataPosition findFile(String fileHash){
        return fileIndex.getRoot().get(fileHash);
    }

    /**
     * 添加索引
     * @param dataPosition
     */
    public void addIndex(DataPosition dataPosition) {
        fileIndex.insertOrUpdate(dataPosition.getHashCode(),dataPosition);
    }
}
