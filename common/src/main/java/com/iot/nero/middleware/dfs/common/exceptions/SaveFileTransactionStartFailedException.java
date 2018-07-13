package com.iot.nero.middleware.dfs.common.exceptions;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/21
 * Time   8:16 AM
 */
public class SaveFileTransactionStartFailedException extends Exception {
    public SaveFileTransactionStartFailedException() {
    }

    public SaveFileTransactionStartFailedException(String message) {
        super(message);
    }

    public SaveFileTransactionStartFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveFileTransactionStartFailedException(Throwable cause) {
        super(cause);
    }

    public SaveFileTransactionStartFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
