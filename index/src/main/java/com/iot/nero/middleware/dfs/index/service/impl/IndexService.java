package com.iot.nero.middleware.dfs.index.service.impl;

import com.iot.nero.middleware.dfs.common.annotation.Inject;
import com.iot.nero.middleware.dfs.common.annotation.Service;
import com.iot.nero.middleware.dfs.index.core.FileIndexManager;
import com.iot.nero.middleware.dfs.common.entity.DataPosition;
import com.iot.nero.middleware.dfs.common.service.IIndexService;

import java.io.IOException;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:28
 */

@Service
public class IndexService implements IIndexService {

    @Inject
    private FileIndexManager fileIndexManager;

    @Override
    public DataPosition getFileByHashCode(String hashCode) throws IOException {

        // 此处查找index
        return fileIndexManager.findFile(hashCode);
    }


    @Override
    public Boolean registerChunkIndex(List<DataPosition> dataPositionList) {
        // 此处由datanode注册索引
        for(DataPosition dataPosition:dataPositionList){
            fileIndexManager.addIndex(dataPosition);
        }
        return true;
    }
}
