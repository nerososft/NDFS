package com.iot.nero.middleware.dfs.index.constant;

import java.util.Date;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/5
 * Time   下午3:37
 */
public class CONSTANT {
    public static final String VERSION = "0.1.3";
    public static final String INDEX_NODE = "Index Server";
    public static final String COPY_RIGHT = "www.cenocloud.com All Right Reserved";
    public static final String CLIENT_OFFLINE = "客户端掉线";

    public static void printNdfsInfo() {
        System.out.println("███╗   ██╗██████╗ ███████╗███████╗");
        System.out.println("████╗  ██║██╔══██╗██╔════╝██╔════╝");
        System.out.println("██╔██╗ ██║██║  ██║█████╗  ███████╗");
        System.out.println("██║╚██╗██║██║  ██║██╔══╝  ╚════██║");
        System.out.println("██║ ╚████║██████╔╝██║     ███████║");
        System.out.println("╚═╝  ╚═══╝╚═════╝ ╚═╝     ╚══════╝");
        System.out.println(INDEX_NODE +"-"+ VERSION);
        System.out.println(COPY_RIGHT);
    }

    public static void pInfo(Object info) {
        System.out.println("[" + new Date().toString() + "] " + info.toString());
    }


    public static final String INVALID_HOST_NAME = "hostname 不合法";
    public static final String INVALID_PORT = "port 不合法";


    public static final String SAVE_FILE_TRANSACTION_PREPARE_FAILED = "文件储存事务预请求失败";
    public static final String SAVE_FILE_TRANSACTION_START_FAILED = "文件储存事务发起失败";
}
