package com.iot.nero.middleware.dfs.client.core;

import com.iot.nero.middleware.dfs.common.entity.response.Response;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/4
 * Time   12:10 PM
 */
public interface DFSErrorListener {
    void onError(Response<Object> response);
}
