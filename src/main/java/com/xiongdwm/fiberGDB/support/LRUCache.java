package com.xiongdwm.fiberGDB.support;

import java.util.*;

public class LRUCache<K, V> {
    private static class CacheEntry<K, V> implements Comparable<CacheEntry<K, V>> {
        K key;
        V value;
        long expireTime;
        long accessTime;

        public CacheEntry(K key, V value, long l, long currentTimeMillis) {
            this.key = key;
            this.value = value;
            this.expireTime = currentTimeMillis + l;
            this.accessTime = currentTimeMillis;
        }
        @Override
        public int compareTo(CacheEntry<K, V> other) {
            return Long.compare(this.expireTime, other.expireTime);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    ", expireTime=" + expireTime +
                    ", accessTime=" + accessTime +
                    '}';
        }
    }

    private final int capacity;
    private final long expireTimeLimit;
    private final LinkedHashMap<K, CacheEntry<K, V>> map; // use LinkedHashMap to maintain the order of entries
    private final PriorityQueue<CacheEntry<K, V>> queue;
    private static final long DEFAULT_EXPIRE_TIME = 60*1000; // default expire time is 1 minutes
    private static final int DEFAULT_CAPACITY = 32; // default capacity is 32
    private Timer timer;

    // constructors
    public LRUCache() {
        this(DEFAULT_CAPACITY, DEFAULT_EXPIRE_TIME);
    }

    private LRUCache(int capacity) {
        this(capacity, DEFAULT_EXPIRE_TIME);
    }

    private LRUCache(long expireTimeLimit) {
        this(DEFAULT_CAPACITY, expireTimeLimit);
    }

    public LRUCache(int capacity, long expireTimeLimit) {
        this.capacity = capacity;
        this.expireTimeLimit = expireTimeLimit;
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<K, V>> eldest) {
                return size() > capacity;
            }
        };
        this.queue = new PriorityQueue<>();
    }

    public V get(K key) {
        CacheEntry<K, V> cacheEntry = map.get(key);
        if (cacheEntry == null) return null;

        cacheEntry.accessTime = System.currentTimeMillis();
        // renew expire time,and resort the queue
        cacheEntry.expireTime = cacheEntry.accessTime + expireTimeLimit;
        queue.remove(cacheEntry);
        queue.offer(cacheEntry);
        return cacheEntry.value;
    }

    // put a new entry into the cache
    public void put(K key, V value) {
        CacheEntry<K, V> cacheEntry = new CacheEntry<>(key, value, System.currentTimeMillis() + expireTimeLimit, 0L);
        //if the value already exists, update the value and renew the expire-time
        if (map.containsKey(key)) {
            CacheEntry<K, V> oldCacheEntry = map.get(key);
            map.remove(key);
            queue.remove(oldCacheEntry);
        }
        // if the cache is full, remove the oldest entry from the queue, and because the map is LinkedHashMap, the eldest entry will be automatically removed from the map
        if(map.size() >= capacity) {
            // get the entry automatically removed by LinkedHashMap and remove it from the queue
            CacheEntry<K, V> eldestCacheEntry = map.entrySet().iterator().next().getValue();
            queue.remove(eldestCacheEntry);
        }
        map.put(key, cacheEntry);
        queue.offer(cacheEntry);
        if(isTimerInactive())startTimer();
    }

    private synchronized void clearExpiredEntries() {
        while (!queue.isEmpty() && isExpired(queue.peek())) {
            CacheEntry<K, V> oldestCacheEntry = queue.poll();
            if (oldestCacheEntry == null) continue;
            System.out.println("remove expired entry: " + oldestCacheEntry.key);
            map.remove(oldestCacheEntry.key);
        }
        // if the cache is empty, stop the timer
        if (map.isEmpty()) {
            stopTimer();
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clearExpiredEntries();
            }
        }, expireTimeLimit, expireTimeLimit);
    }

    private void stopTimer() {
        System.out.println("stop timer====================>>>>>>>");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private boolean isExpired(CacheEntry<K, V> cacheEntry) {
        System.out.println("entry"+ cacheEntry.key+": current time: " + System.currentTimeMillis() + ", expire time: " + cacheEntry.expireTime+", time lapse: "+(System.currentTimeMillis()- cacheEntry.expireTime));
        return System.currentTimeMillis() >= cacheEntry.expireTime;
    }

    // check if the timer is inactive
    private boolean isTimerInactive() {
        return timer == null;
    }

    public LinkedHashMap<K, CacheEntry<K, V>> getMap() {
        return map;
    }

    public PriorityQueue<CacheEntry<K, V>> getQueue() {
        return queue;
    }

    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.getMap());
        System.out.println(cache.getQueue());
        Integer v1= cache.get(1);
        System.out.println("================visit v1==================");
        System.out.println(v1);
        System.out.println(cache.getMap());
        System.out.println(cache.getQueue());
    }

}
