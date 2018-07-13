package com.iot.nero.middleware.dfs.common.exceptions;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   8:37 AM
 */
public class ThisChunkIndexIsAlreadyFullException extends Exception {
    public ThisChunkIndexIsAlreadyFullException() {
    }

    public ThisChunkIndexIsAlreadyFullException(String message) {
        super(message);
    }

    public ThisChunkIndexIsAlreadyFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThisChunkIndexIsAlreadyFullException(Throwable cause) {
        super(cause);
    }

    public ThisChunkIndexIsAlreadyFullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
