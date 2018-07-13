package com.iot.nero.middleware.dfs.data.constant;

import java.util.Date;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   下午3:37
 */
public class CONSTANT {
    public static final String VERSION = "0.2.3";
    public static final String DATA_NODE = "Data Server";
    public static final String COPY_RIGHT = "www.cenocloud.com All Right Reserved";
    public static final String CLIENT_OFFLINE = "客户端掉线";

    public static void printNdfsInfo() {
        System.out.println("███╗   ██╗██████╗ ███████╗███████╗");
        System.out.println("████╗  ██║██╔══██╗██╔════╝██╔════╝");
        System.out.println("██╔██╗ ██║██║  ██║█████╗  ███████╗");
        System.out.println("██║╚██╗██║██║  ██║██╔══╝  ╚════██║");
        System.out.println("██║ ╚████║██████╔╝██║     ███████║");
        System.out.println("╚═╝  ╚═══╝╚═════╝ ╚═╝     ╚══════╝");
        System.out.println(DATA_NODE +"-"+ VERSION);
        System.out.println(COPY_RIGHT);
    }

    public static void pInfo(Object info) {
        System.out.println("[" + new Date().toString() + "] " + info.toString());
    }


    public static final String INVALID_HOST_NAME = "hostname 不合法";
    public static final String INVALID_PORT = "port 不合法";


    public static final String NOT_FIND_FILE_FROM_CURRENT_CHUNK = "当前文件块中找不到该文件";
    public static final String CHUNK_INDEX_IS_FULL = "这个文件块索引区域已经满了";
    public static final String FILE_ALREADY_EXISTS_IN_THIS_CHUNK  ="该文件在当前文件块中已经存在";
    public static final String FILE_SIZE_EXCEEDED = "文件大小超出块限制";


    public static final String UNKNOWN_REQUEST_TYPE = "未知的请求";
    public static final String AUTHENTICATION_INCORRECT = "认证失败，认证秘钥不正确，青查看配置文件。";
}
