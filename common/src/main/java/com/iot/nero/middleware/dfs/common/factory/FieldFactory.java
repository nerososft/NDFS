package com.iot.nero.middleware.dfs.common.factory;

import org.apache.commons.collections.map.HashedMap;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   2:41 PM
 */
public class FieldFactory {

    static Map<Field, Object> fieldObjectMap = new HashedMap();

    public static Object get(Field field) throws IllegalAccessException, InstantiationException {
        Object object = fieldObjectMap.get(field);
        if (object == null) {
            Object newObj = field.getType().newInstance();
            fieldObjectMap.put(field, newObj);
            return newObj;
        }
        return object;
    }
}
