package com.lixl.sc.config;

import lombok.Data;

import java.io.Serializable;
@Data
public class CacheMessage implements Serializable {

    private static final long serialVersionUID = 6948340320577054135L;
    private String cacheName;

    private Object key;

    public CacheMessage() {
    }

    public CacheMessage(String cacheName, Object key) {
        super();
        this.cacheName = cacheName;
        this.key = key;
    }

}
