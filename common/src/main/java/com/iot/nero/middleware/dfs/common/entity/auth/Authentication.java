package com.iot.nero.middleware.dfs.common.entity.auth;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/4
 * Time   12:59 PM
 */
public class Authentication implements Serializable {
    private String key;
    private String secret;

    public Authentication(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "key='" + key + '\'' +
                ", secret='" + secret + '\'' +
                '}';
    }
}
