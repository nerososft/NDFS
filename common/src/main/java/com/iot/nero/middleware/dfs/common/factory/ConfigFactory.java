package com.iot.nero.middleware.dfs.common.factory;

import com.iot.nero.middleware.dfs.common.config.Config;
import com.iot.nero.middleware.dfs.common.config.ConfigLoader;

import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   4:14 PM
 */
public class ConfigFactory {

    private static Config config;

    public static Config getConfig() throws IOException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        if(config!=null){
            return config;
        }
        config = ConfigLoader.loadConfig();
        return config;
    }
}
