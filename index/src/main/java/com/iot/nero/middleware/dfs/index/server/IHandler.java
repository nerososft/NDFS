package com.iot.nero.middleware.dfs.index.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   8:49 PM
 */
public interface IHandler {

    void readProcess() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException;

    void writeProcess() throws IOException;

}
