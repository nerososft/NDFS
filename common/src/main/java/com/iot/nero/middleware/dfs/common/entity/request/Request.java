package com.iot.nero.middleware.dfs.common.entity.request;

import com.iot.nero.middleware.dfs.common.entity.auth.Authentication;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/6
 * Time   1:56 PM
 */
public class Request<T> implements Serializable{
    private Authentication authentication;
    private String requestId;
    private Byte requestType;
    private T data;

    public Request() {
    }

    public Request(Authentication authentication, String requestId, Byte requestType, T data) {
        this.authentication = authentication;
        this.requestId = requestId;
        this.requestType = requestType;
        this.data = data;
    }

    public Request(String requestId, Byte requestType, T data) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.data = data;
    }

    public Byte getRequestType() {
        return requestType;
    }

    public void setRequestType(Byte requestType) {
        this.requestType = requestType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        return "Request{" +
                "authentication=" + authentication +
                ", requestId='" + requestId + '\'' +
                ", requestType=" + requestType +
                ", data=" + data +
                '}';
    }

    public boolean auth(Authentication authentication) {
        if(!authentication.getKey().equals(this.authentication.getKey())||
                !authentication.getSecret().equals(this.authentication.getSecret())){
            return false;
        }
        return true;
    }
}
