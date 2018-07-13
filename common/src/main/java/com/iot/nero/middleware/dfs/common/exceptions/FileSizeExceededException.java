package com.iot.nero.middleware.dfs.common.exceptions;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/29
 * Time   10:01 AM
 */
public class FileSizeExceededException extends Exception {
    public FileSizeExceededException() {
    }

    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSizeExceededException(Throwable cause) {
        super(cause);
    }

    public FileSizeExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
