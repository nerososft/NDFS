package com.iot.nero.middleware.dfs.common.config;

import com.iot.nero.middleware.dfs.common.App;
import com.iot.nero.middleware.dfs.common.annotation.ConfigClass;
import com.iot.nero.middleware.dfs.common.annotation.ConfigField;
import com.iot.nero.middleware.dfs.common.utils.ClassUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iot.nero.middleware.dfs.common.constant.CONSTANT.pInfo;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/4
 * Time   下午12:49
 */
public class ConfigLoader {

    private static String configFilePath = System.getProperty("user.dir") + "/config/config.properties";

    public static Config loadConfig() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(configFilePath))) {

           pInfo("(Config) "+configFilePath);

            List<Class<?>> configClass = ClassUtil.getAllClassByPackageName(App.class.getPackage()); // 注册扫描
            Map<String, String> configMap = new HashMap<>();
            String line;
            while ((line = fileReader.readLine()) != null) {
                String cc = line.replaceAll("\\s*", "");
                if (!"".equals(cc)) {
                    if (cc.charAt(0) == '#') {
                        continue;
                    } else {
                        String[] ccc = cc.split("=");
                        configMap.put(ccc[0], ccc[1]);
                    }
                }
            }

            for (Class<?> cls : configClass) {
                ConfigClass configClz = cls.getAnnotation(ConfigClass.class);
                if (configClz != null) {
                    Config config = (Config) cls.newInstance();
                    Field[] fieldList = cls.getDeclaredFields();
                    for (Field field : fieldList) {
                        ConfigField configField = field.getAnnotation(ConfigField.class);
                        if (configField != null) {
                            field.setAccessible(true);
                            if (field.getType() == String.class) {
                                field.set(config, configMap.get(configField.value()));
                            }
                            if (field.getType() == Integer.class) {
                                field.set(config, Integer.valueOf(configMap.get(configField.value())));
                            }
                            if (field.getType() == Boolean.class) {
                                field.set(config, Boolean.valueOf(configMap.get(configField.value())));
                            }
                        }
                    }
                    return config;
                }
            }
            return null;
        }
    }
}
