package com.iot.nero.middleware.dfs.common.exceptions;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/21
 * Time   8:22 AM
 */
public class SaveFileTransactionPrepareFailedException extends Exception {
    public SaveFileTransactionPrepareFailedException() {
    }

    public SaveFileTransactionPrepareFailedException(String message) {
        super(message);
    }

    public SaveFileTransactionPrepareFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveFileTransactionPrepareFailedException(Throwable cause) {
        super(cause);
    }

    public SaveFileTransactionPrepareFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
