package com.xiongdwm.fiberGDB.support.lru;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheHandler {
    private ConcurrentHashMap<String,LRUCache<?,?>> map;

     
}
