package com.iot.nero.middleware.dfs.common.exceptions;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   9:17 AM
 */
public class FileAlreadyExistsInThisChunkException extends Exception {
    public FileAlreadyExistsInThisChunkException() {
    }

    public FileAlreadyExistsInThisChunkException(String message) {
        super(message);
    }

    public FileAlreadyExistsInThisChunkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAlreadyExistsInThisChunkException(Throwable cause) {
        super(cause);
    }

    public FileAlreadyExistsInThisChunkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
