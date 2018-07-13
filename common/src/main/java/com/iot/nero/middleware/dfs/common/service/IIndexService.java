package com.iot.nero.middleware.dfs.common.service;

import com.iot.nero.middleware.dfs.common.entity.DataPosition;

import java.io.IOException;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午2:28
 */
public interface IIndexService {

    /**
     * 查找文件
     * @param hashCode
     * @return
     */
    DataPosition getFileByHashCode(String hashCode) throws IOException;


    /**
     * 注册chunk索引
     * @param dataPosition
     * @return
     */
    Boolean registerChunkIndex(List<DataPosition> dataPosition);

}
