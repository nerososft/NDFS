package com.iot.nero.middleware.dfs.data.core;

import org.junit.Test;
import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   12:29 PM
 */
public class IndexManagerTest {


    @Test
    public void indexManager() throws IllegalAccessException, NoSuchMethodException, InstantiationException {
        try {
            IndexManager indexManager = new IndexManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findFileByHash() throws Exception {
        IndexManager indexManager = new IndexManager();
        System.out.println(indexManager.findFileByHash("hash1"));
    }

    @Test
    public void readFileSourceDataFromChunkManager() throws Exception {

    }

    @Test
    public void readFileCompressedDataFromChunkManager() throws Exception {

    }

}