package com.iot.nero.middleware.dfs.data.service.impl;

import com.iot.nero.middleware.dfs.common.entity.BlockData;
import com.iot.nero.middleware.dfs.data.core.IndexManager;
import org.junit.Test;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/2
 * Time   9:04 AM
 */
public class FileServiceTest {


    FileService fileService;

    @Test
    public void findFileByHash() throws Exception {
        fileService = new FileService();
        fileService.indexManager = new IndexManager();
        BlockData blockData = fileService.findFileByHash("");
        System.out.println(blockData);
    }

    @Test
    public void getDataBytes() throws Exception {

    }

    @Test
    public void delFileByHash() throws Exception {

    }

    @Test
    public void uploadFile() throws Exception {

    }

    @Test
    public void uploadCommit() throws Exception {

    }

}